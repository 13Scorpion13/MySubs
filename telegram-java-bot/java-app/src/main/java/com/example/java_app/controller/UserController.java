package com.example.java_app.controller;

import com.example.java_app.dto.userDto.ShowUserDto;
import com.example.java_app.dto.userDto.UpdateUserDto;
import com.example.java_app.dto.userDto.UserDto;
import com.example.java_app.exceptions.ResourceNotFoundException;
import com.example.java_app.model.AppUser;
import com.example.java_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("${api.user-url}")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<AppUser> createUser(@RequestBody UserDto userDto) {
        try {
            AppUser createdUser = userService.createUser(userDto);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/{telegramId}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long telegramId,
            @RequestBody UpdateUserDto updateUserDto
    ) {
        try {
            Optional<AppUser> userOptional = userService.findByTelegramId(telegramId);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            AppUser user = userOptional.get();
            userService.updateUserInfo(user.getId(), updateUserDto);
            return ResponseEntity.ok("User info updated successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update user info");
        }
    }

    @GetMapping("/by-telegram-id/{telegramId}")
    public ResponseEntity<ShowUserDto> getUserByTelegramId(@PathVariable Long telegramId) {
        Optional<ShowUserDto> userDto = userService.getUserDtoByTelegramId(telegramId);
        return userDto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}