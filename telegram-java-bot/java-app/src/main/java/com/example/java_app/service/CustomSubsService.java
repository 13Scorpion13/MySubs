package com.example.java_app.service;

import com.example.java_app.dto.customSubsDto.*;
import com.example.java_app.exceptions.ResourceNotFoundException;
import com.example.java_app.model.AppUser;
import com.example.java_app.model.CustomSubs;
import com.example.java_app.model.UserCard;
import com.example.java_app.repository.CustomSubsRepository;
import com.example.java_app.repository.UserCardRepository;
import com.example.java_app.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomSubsService {
    private final CustomSubsRepository customSubsRepository;
    private final UserRepository userRepository;
    private final UserCardRepository userCardRepository;

    @Autowired
    public CustomSubsService (CustomSubsRepository customSubsRepository, UserRepository userRepository,
                              UserCardRepository userCardRepository) {
        this.customSubsRepository = customSubsRepository;
        this.userRepository = userRepository;
        this.userCardRepository = userCardRepository;
    }

    public List<ShowCustomSubsDto> getCustomSubscriptionsForCurrentMonth(Long userId) {
        LocalDate now = LocalDate.now();
        YearMonth currentMonth = YearMonth.of(now.getYear(), now.getMonthValue());

        return customSubsRepository.findByUserId(userId).stream()
                .filter(customSub -> {
                    LocalDate endDate = customSub.getDate();
                    YearMonth subMonth = YearMonth.of(endDate.getYear(), endDate.getMonthValue());
                    return subMonth.equals(currentMonth);
                })
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public CustomSubsDto saveCustomSubscription(CustomSubsDto customSubsDto) {
        Optional<AppUser> userOptional = userRepository.findByTelegramId(customSubsDto.getUserId());
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Пользователь с ID " + customSubsDto.getUserId() + "не найден");
        }

        Optional<UserCard> userCardOptional = userCardRepository.findById(customSubsDto.getUserCardId());
        if (userCardOptional.isEmpty()) {
            throw new IllegalArgumentException("Карта с ID " + customSubsDto.getUserCardId() + "не найдена");
        }

        LocalDate date = customSubsDto.getDate();
        int durationMonths = customSubsDto.getDurationMonths();
        LocalDate endDate = date.plusMonths(durationMonths);


        CustomSubs customSubs = new CustomSubs();
        customSubs.setUser(userOptional.get());
        customSubs.setServiceName(customSubsDto.getServiceName());
        customSubs.setDurationMonths(customSubsDto.getDurationMonths());
        customSubs.setPrice(customSubsDto.getPrice());
        customSubs.setUserCard(userCardOptional.get());
        customSubs.setDate(endDate);

        CustomSubs savedCustomSubs = customSubsRepository.save(customSubs);
        return new CustomSubsDto(
                savedCustomSubs.getId(),
                savedCustomSubs.getUser().getId(),
                savedCustomSubs.getServiceName(),
                savedCustomSubs.getDurationMonths(),
                savedCustomSubs.getPrice(),
                savedCustomSubs.getUserCard().getId(),
                savedCustomSubs.getDate()
        );
    }

    public List<ShowCustomSubsDto> getCustomSubscriptionsByUserId(Long userId) {
        List<CustomSubs> customSubs = customSubsRepository.findByUserId(userId);
        return customSubs.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ShowCustomSubsDto convertToDto(CustomSubs customSub) {
        return new ShowCustomSubsDto(
                customSub.getId(),
                customSub.getServiceName(),
                customSub.getDurationMonths(),
                customSub.getPrice(),
                customSub.getUserCard().getCardName()
        );
    }

    @Transactional
    public void updateCustomSubscription(Long id, UpdateCustomSubsDto updateDto) {
        CustomSubs customSubs = customSubsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Custom subscription not found"));

        if (updateDto.getServiceName() != null) {
            customSubs.setServiceName(updateDto.getServiceName());
        }

        if (updateDto.getDurationMonths() > 0) {
            customSubs.setDurationMonths(updateDto.getDurationMonths());
        }

        if (updateDto.getPrice() != null) {
            customSubs.setPrice(updateDto.getPrice());
        }

        if (updateDto.getUserCardId() != null) {
            UserCard userCard = userCardRepository.findById(updateDto.getUserCardId())
                    .orElseThrow(() -> new ResourceNotFoundException("User card not found"));
            customSubs.setUserCard(userCard);
        }

        if (updateDto.getDate() != null) {
            LocalDate date = updateDto.getDate();
            int durationMonths = customSubs.getDurationMonths();
            LocalDate endDate = date.plusMonths(durationMonths);
            customSubs.setDate(endDate);
        }

        customSubsRepository.save(customSubs);
    }

    public void deleteCustomSubscription(Long id) {
        if (!customSubsRepository.existsById(id)) {
            throw new ResourceNotFoundException("Custom subscription not found");
        }
        customSubsRepository.deleteById(id);
    }
}
