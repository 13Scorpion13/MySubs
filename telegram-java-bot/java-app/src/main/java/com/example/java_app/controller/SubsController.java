package com.example.java_app.controller;

import com.example.java_app.dto.subsDto.*;
import com.example.java_app.model.*;
import com.example.java_app.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("${api.subs-url}")
public class SubsController {
    private final SubsService subsService;
    private final UserService userService;

    @Autowired
    public SubsController(SubsService subsService, UserService userService) {
        this.subsService = subsService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UpdateSubsDto> getSubscription(@PathVariable Long id) {
        UpdateSubsDto subscriptionDto = subsService.getSubscriptionById(id);
        return ResponseEntity.ok(subscriptionDto);
    }

    @GetMapping("/analytics/{telegramId}")
    public ResponseEntity<?> getAnalytics(@PathVariable Long telegramId) {
        Optional<AppUser> userOptional = userService.findByTelegramId(telegramId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        AppUser user = userOptional.get();

        List<ShowSubsDto> subscriptions = subsService.getSubscriptionsForCurrentMonth(user.getId());
        BigDecimal totalAmount = subscriptions.stream()
                .map(ShowSubsDto::getTariffPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> response = new HashMap<>();
        response.put("totalAmount", totalAmount);
        response.put("subscriptions", subscriptions);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-telegram-id/{telegramId}")
    public ResponseEntity<List<ShowSubsDto>> getSubscriptionsByTelegramId(@PathVariable Long telegramId) {
        Optional<AppUser> userOptional = userService.findByTelegramId(telegramId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        AppUser user = userOptional.get();

        List<ShowSubsDto> subscriptions = subsService.getSubscriptionsByUserId(user.getId());
        return ResponseEntity.ok(subscriptions);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createSubscription(@RequestBody CreateSubsDto createSubsDto) {
        try {
            Subs createdSubscription = subsService.createSubscription(createSubsDto);
            return ResponseEntity.status(201).body(createdSubscription);
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(400).body("Failed to create subscription " + e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateSubscription(
            @PathVariable Long id,
            @RequestBody UpdateSubsDto updateSubsDto
    ) {
        try {
            subsService.updateSubscription(id, updateSubsDto);
            return ResponseEntity.ok("Subscription updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to update subscription: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable Long id) {
        subsService.deleteSubscription(id);
        return ResponseEntity.noContent().build();
    }
}
