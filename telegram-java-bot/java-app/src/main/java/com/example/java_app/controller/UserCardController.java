package com.example.java_app.controller;

import com.example.java_app.dto.UserCardDto;
import com.example.java_app.model.UserCard;
import com.example.java_app.service.UserCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-cards")
public class UserCardController {

    private final UserCardService userCardService;

    @Autowired
    public UserCardController(UserCardService userCardService) {
        this.userCardService = userCardService;
    }

    @PostMapping("/by-telegram-id")
    public ResponseEntity<UserCard> createUserCard(@RequestParam Long telegramId, @RequestBody UserCard userCardRequest) {
        String cardName = userCardRequest.getCardName();
        Integer lastNum = userCardRequest.getLastNum();

        return userCardService.createUserCardForTelegramUser(telegramId, cardName, lastNum)
                .map(userCard -> ResponseEntity.status(201).body(userCard))
                .orElseGet(() -> ResponseEntity.status(404).build());
    }

    @GetMapping
    public List<UserCard> getAllUserCards() {
        return userCardService.getAllUserCards();
    }

    @GetMapping("/by-telegram-id/{telegramId}")
    public ResponseEntity<List<UserCardDto>> getUserCardsByTelegramId(@PathVariable Long telegramId) {
        List<UserCardDto> userCardDto = userCardService.getUserCardDtoByTelegramId(telegramId);
        if (!userCardDto.isEmpty()) {
            return ResponseEntity.ok(userCardDto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserCard> getUserCardById(@PathVariable Long id) {
        return userCardService.getUserCardById(id)
                .map(userCard -> new ResponseEntity<>(userCard, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
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
