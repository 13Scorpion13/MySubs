package com.example.java_app.controller;

import com.example.java_app.model.*;
import com.example.java_app.repository.TariffRepository;
import com.example.java_app.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/subscriptions")
public class SubsController {
    private final SubsService subsService;
    private final UserService userService;
    private final ServicesService servicesService;
    private final CategoryService categoryService;
    private final TariffService tariffService;
    private final UserCardService userCardService;

    @Autowired
    public SubsController(SubsService subsService, UserService userService, ServicesService servicesService,
                          CategoryService categoryService, TariffService tariffService,
                          UserCardService userCardService) {
        this.subsService = subsService;
        this.userService = userService;
        this.servicesService = servicesService;
        this.categoryService = categoryService;
        this.tariffService = tariffService;
        this.userCardService = userCardService;
    }

    @GetMapping
    public List<Subs> getAllSubscriptions() {
        return subsService.getAllSubscriptions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subs> getSubscriptionById(@PathVariable Long id) {
        return subsService.getSubscriptionById(id)
                .map(subs -> new ResponseEntity<>(subs, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createSubscription(
            @RequestParam Long telegramId,
            @RequestParam Long serviceId,
            @RequestParam Long categoryId,
            @RequestParam Long tariffId,
            @RequestParam Long userCardId
    ) {
        Optional<AppUser> userOptional = userService.findByTelegramId(telegramId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }
        AppUser user = userOptional.get();

        Optional<Services> serviceOptional = servicesService.getServiceById(serviceId);
        if (serviceOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Service not found");
        }
        Services service = serviceOptional.get();

        Optional<Category> categoryOptional = categoryService.getCategoryById(categoryId);
        if (categoryOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Category not found");
        }
        Category category = categoryOptional.get();

        Optional<Tariff> tariffOptional = tariffService.getTariffById(tariffId);
        if (tariffOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Tariff not found");
        }
        Tariff tariff = tariffOptional.get();

        Optional<UserCard> userCardOptional = userCardService.getUserCardById(userCardId);
        if (userCardOptional.isEmpty()) {
            return ResponseEntity.status(404).body("User card not found");
        }
        UserCard userCard = userCardOptional.get();

        Subs subscription = new Subs();
        subscription.setUser(user);
        subscription.setService(service);
        subscription.setCategory(category);
        subscription.setTariff(tariff);
        subscription.setUserCard(userCard);
        subscription.setDate(LocalDate.now());

        Subs createdSubscription = subsService.saveSubscription(subscription);
        return ResponseEntity.status(201).body(createdSubscription);
    }

    @PostMapping
    public ResponseEntity<Subs> createSubscription(@RequestBody Subs subs) {
        Subs savedSubs = subsService.saveSubscription(subs);
        return new ResponseEntity<>(savedSubs, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable Long id) {
        subsService.deleteSubscription(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
