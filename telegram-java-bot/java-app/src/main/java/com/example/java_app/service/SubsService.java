package com.example.java_app.service;

import com.example.java_app.dto.subsDto.*;
import com.example.java_app.exceptions.ResourceNotFoundException;
import com.example.java_app.model.*;
import com.example.java_app.repository.ServiceRepository;
import com.example.java_app.repository.SubsRepository;
import com.example.java_app.repository.TariffRepository;
import com.example.java_app.repository.UserCardRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubsService {

    private final SubsRepository subsRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final TariffService tariffService;
    private final ServicesService servicesService;
    private final ServiceRepository serviceRepository;
    private final TariffRepository tariffRepository;
    private final UserCardService userCardService;
    private final UserCardRepository userCardRepository;

    @Autowired
    public SubsService(SubsRepository subsRepository, UserService userService, CategoryService categoryService,
                       TariffService tariffService,
                       ServicesService servicesService,
                       ServiceRepository serviceRepository,
                       TariffRepository tariffRepository,
                       UserCardService userCardService,
                       UserCardRepository userCardRepository) {
        this.subsRepository = subsRepository;
        this.userService = userService;
        this.categoryService = categoryService;
        this.tariffService = tariffService;
        this.servicesService = servicesService;
        this.serviceRepository = serviceRepository;
        this.tariffRepository = tariffRepository;
        this.userCardService = userCardService;
        this.userCardRepository = userCardRepository;
    }

    public List<Subs> getAllSubscriptions() {
        return subsRepository.findAll();
    }

    public List<ShowSubsDto> getSubscriptionsForCurrentMonth(Long userId) {
        LocalDate now = LocalDate.now();
        YearMonth currentMonth = YearMonth.of(now.getYear(), now.getMonthValue());

        return subsRepository.findByUserId(userId).stream()
                .filter(sub -> {
                    LocalDate endDate = sub.getDate();
                    YearMonth subMonth = YearMonth.of(endDate.getYear(), endDate.getMonthValue());
                    return subMonth.equals(currentMonth);
                })
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public UpdateSubsDto getSubscriptionById(Long id) {
        Optional<Subs> optionalSubscriptions = subsRepository.findById(id);
        if (optionalSubscriptions.isEmpty()){
            throw new RuntimeException("Subs not found");
        }
        Subs subscription = optionalSubscriptions.get();

        return new UpdateSubsDto(
                subscription.getId(),
                subscription.getService().getId(),
                subscription.getTariff().getId(),
                subscription.getUserCard().getId(),
                subscription.getDate()
        );
    }

    public Subs createSubscription(CreateSubsDto createSubsDto) {
        Optional<AppUser> userOptional = userService.findByTelegramId(createSubsDto.getTelegramId());
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        AppUser user = userOptional.get();

        Optional<Category> categoryOptional = categoryService.getCategoryById(createSubsDto.getCategoryId());
        if (categoryOptional.isEmpty()) {
            throw new IllegalArgumentException("Category not found");
        }
        Category category = categoryOptional.get();

        Optional<Services> servicesOptional = servicesService.getServiceById(createSubsDto.getServiceId());
        if (servicesOptional.isEmpty()) {
            throw new IllegalArgumentException("Service not found");
        }
        Services services = servicesOptional.get();

        Optional<Tariff> tariffOptional = tariffService.getTariffById(createSubsDto.getTariffId());
        if (tariffOptional.isEmpty()) {
            throw new IllegalArgumentException("Tariff not found");
        }
        Tariff tariff = tariffOptional.get();

        Optional<UserCard> userCardOptional = userCardService.getUserCardById(createSubsDto.getUserCardId());
        if (userCardOptional.isEmpty()) {
            throw new IllegalArgumentException("User card not found");
        }
        UserCard userCard = userCardOptional.get();

        LocalDate date = createSubsDto.getDate();
        int durationMonths = tariff.getDurationMonths();
        LocalDate endDate = date.plusMonths(durationMonths);

        Subs subs = new Subs();
        subs.setUser(user);
        subs.setCategory(category);
        subs.setService(services);
        subs.setTariff(tariff);
        subs.setUserCard(userCard);
        subs.setDate(endDate);

        return subsRepository.save(subs);
    }

    public List<ShowSubsDto> getSubscriptionsByUserId(Long userId) {
        List<Subs> subscriptions = subsRepository.findByUserId(userId);
        return subscriptions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ShowSubsDto convertToDto(Subs subscription) {
        return new ShowSubsDto(
                subscription.getId(),
                subscription.getService().getServiceName(),
                subscription.getTariff().getPrice(),
                subscription.getTariff().getDurationMonths(),
                subscription.getUserCard().getCardName(),
                subscription.getDate()
        );
    }

    @Transactional
    public void updateSubscription(Long subscriptionId, UpdateSubsDto dto) {
        Subs subscription = subsRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        if (dto.getTariffId() != null) {
            Tariff tariff = tariffRepository.findById(dto.getTariffId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tariff not found"));
            subscription.setTariff(tariff);
        }

        if (dto.getServiceId() != null) {
            Services service = serviceRepository.findById(dto.getServiceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
            subscription.setService(service);
        }

        if (dto.getUserCardId() != null) {
            UserCard userCard = userCardRepository.findById(dto.getUserCardId())
                    .orElseThrow(() -> new ResourceNotFoundException("User card not found"));
            subscription.setUserCard(userCard);
        }

        if (dto.getDate() != null) {
            LocalDate date = dto.getDate();
            int durationMonths = subscription.getTariff().getDurationMonths();
            LocalDate endDate = date.plusMonths(durationMonths);
            subscription.setDate(endDate);
        }

        subsRepository.save(subscription);
    }

    public void deleteSubscription(Long id) {
        subsRepository.deleteById(id);
    }
}
