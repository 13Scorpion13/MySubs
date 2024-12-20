package com.example.java_app.controller;

import com.example.java_app.dto.customSubsDto.*;
import com.example.java_app.exceptions.ResourceNotFoundException;
import com.example.java_app.model.AppUser;
import com.example.java_app.service.CustomSubsService;
import com.example.java_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("${api.custom-subs-url}")
public class CustomSubsController {
    private final CustomSubsService customSubsService;
    private final UserService userService;

    @Autowired
    public CustomSubsController(CustomSubsService customSubsService, UserService userService) {
        this.customSubsService = customSubsService;
        this.userService = userService;
    }

    @PostMapping("/create-custom-subs")
    public ResponseEntity<CustomSubsDto> createCustomSubscription(@RequestBody CustomSubsDto customSubsDto) {
        try {
            CustomSubsDto savedCustomSubs = customSubsService.saveCustomSubscription(customSubsDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomSubs);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/analytics/{telegramId}")
    public ResponseEntity<?> getCustomAnalytics(@PathVariable Long telegramId) {
        Optional<AppUser> userOptional = userService.findByTelegramId(telegramId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        AppUser user = userOptional.get();

        List<ShowCustomSubsDto> customSubscriptions = customSubsService.getCustomSubscriptionsForCurrentMonth(user.getId());
        BigDecimal totalAmount = customSubscriptions.stream()
                .map(ShowCustomSubsDto::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> response = new HashMap<>();
        response.put("totalAmount", totalAmount);
        response.put("customSubscriptions", customSubscriptions);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-telegram-id/{telegramId}")
    public ResponseEntity<List<ShowCustomSubsDto>> getCustomSubscriptionsByTelegramId(@PathVariable Long telegramId) {
        Optional<AppUser> userOptional = userService.findByTelegramId(telegramId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        AppUser user = userOptional.get();

        List<ShowCustomSubsDto> customSubs = customSubsService.getCustomSubscriptionsByUserId(user.getId());
        if (customSubs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(customSubs);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateCustomSubscription(
            @PathVariable Long id,
            @RequestBody UpdateCustomSubsDto updateDto
    ) {
        try {
            customSubsService.updateCustomSubscription(id, updateDto);
            return ResponseEntity.ok("Custom subscription updated successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update custom subscription");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomSubscription(@PathVariable Long id) {
        try {
            customSubsService.deleteCustomSubscription(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete custom subscription");
        }
    }
}
