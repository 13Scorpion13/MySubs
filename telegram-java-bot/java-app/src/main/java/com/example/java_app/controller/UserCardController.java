package com.example.java_app.controller;

import com.example.java_app.dto.userCardDto.ShowUserCardDto;
import com.example.java_app.dto.userCardDto.UpdateUserCardDto;

import com.example.java_app.exceptions.ResourceNotFoundException;
import com.example.java_app.model.AppUser;
import com.example.java_app.model.UserCard;

import com.example.java_app.service.UserCardService;
import com.example.java_app.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("${api.user-card-url}")
public class UserCardController {

    private final UserCardService userCardService;
    private final UserService userService;

    @Autowired
    public UserCardController(UserCardService userCardService, UserService userService) {
        this.userCardService = userCardService;
        this.userService = userService;
    }

    @PostMapping("/by-telegram-id")
    public ResponseEntity<UserCard> createUserCard(@RequestParam Long telegramId, @RequestBody UserCard userCardRequest) {
        String cardName = userCardRequest.getCardName();
        Integer lastNum = userCardRequest.getLastNum();

        return userCardService.createUserCardForTelegramUser(telegramId, cardName, lastNum)
                .map(userCard -> ResponseEntity.status(201).body(userCard))
                .orElseGet(() -> ResponseEntity.status(404).build());
    }

    @GetMapping("/by-telegram-id/{telegramId}")
    public ResponseEntity<List<ShowUserCardDto>> getUserCardsByTelegramId(@PathVariable Long telegramId) {
        Optional<AppUser> userOptional = userService.findByTelegramId(telegramId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        AppUser user = userOptional.get();

        List<ShowUserCardDto> userCard = userCardService.getUserCardByUserId(user.getId());
        if(userCard.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(userCard);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateCard(
            @PathVariable Long id,
            @RequestBody UpdateUserCardDto updateDto
    ) {
        try {
            userCardService.updateUserCard(id, updateDto);
            return ResponseEntity.ok("Card updated successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update card");
        }
    }

    @PostMapping
    public ResponseEntity<UserCard> createUserCard(@RequestBody UserCard userCard) {
        UserCard savedCard = userCardService.saveUserCard(userCard);
        return new ResponseEntity<>(savedCard, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserCard(@PathVariable Long id) {
        userCardService.deleteUserCard(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
