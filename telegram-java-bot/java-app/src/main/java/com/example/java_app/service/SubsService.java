package com.example.java_app.service;

import com.example.java_app.model.*;
import com.example.java_app.repository.SubsRepository;
import com.example.java_app.repository.TariffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SubsService {

    private final SubsRepository subsRepository;
    private final UserService userService;
    private final ServicesService servicesService;
    private final CategoryService categoryService;
    private final TariffRepository tariffRepository;
    private final UserCardService userCardService;

    @Autowired
    public SubsService(SubsRepository subsRepository, UserService userService, ServicesService servicesService,
                       CategoryService categoryService, TariffRepository tariffRepository,
                       UserCardService userCardService) {
        this.subsRepository = subsRepository;
        this.userService = userService;
        this.servicesService = servicesService;
        this.categoryService = categoryService;
        this.tariffRepository = tariffRepository;
        this.userCardService = userCardService;
    }

    public List<Subs> getAllSubscriptions() {
        return subsRepository.findAll();
    }

    public Optional<Subs> getSubscriptionById(Long id) {
        return subsRepository.findById(id);
    }

    public Subs createSubscription(Subs subscription) {
        return subsRepository.save(subscription);
    }

    /*@Transactional
    public Optional<Subs> createSubscriptionWithDetails(Long telegramId, Long serviceId, Long categoryId, Long tariffId, Long userCardId) {
        Optional<AppUser> userOptional = userService.findByTelegramId(telegramId);
        Optional<Services> serviceOptional = servicesService.getServiceById(serviceId);
        Optional<Category> categoryOptional = categoryService.getCategoryById(categoryId);
        Optional<Tariff> tariffOptional = tariffRepository.findById(tariffId);
        Optional<UserCard> userCardOptional = userCardService.getUserCardById(userCardId);

        if (userOptional.isPresent() && serviceOptional.isPresent() && categoryOptional.isPresent()
                && tariffOptional.isPresent() && userCardOptional.isPresent()) {
            Subs subscription = new Subs();
            subscription.setUser(userOptional.get());
            subscription.setService(serviceOptional.get());
            subscription.setCategory(categoryOptional.get());
            subscription.setTariff(tariffOptional.get());
            subscription.setUserCard(userCardOptional.get());
            subscription.setDate(LocalDate.now());

            return Optional.of(subsRepository.save(subscription));
        }

        return Optional.empty();
    }*/

    public Subs saveSubscription(Subs subs) {
        return subsRepository.save(subs);
    }

    public void deleteSubscription(Long id) {
        subsRepository.deleteById(id);
    }
}
