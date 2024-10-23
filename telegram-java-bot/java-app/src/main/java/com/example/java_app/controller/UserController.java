package com.example.java_app.controller;

import com.example.java_app.dto.UserDto;
import com.example.java_app.model.AppUser;
import com.example.java_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<AppUser> createUser(@RequestBody UserDto userDto) {
        AppUser appUser = new AppUser();
        appUser.setTelegramId(userDto.getTelegramId());
        appUser.setNickname(userDto.getNickname());
        appUser.setEmail(userDto.getEmail());

        System.out.println("Сохраняемый пользователь: " + appUser);

        AppUser savedUser = userService.saveUser(appUser);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @GetMapping("/by-telegram-id/{telegramId}")
    public ResponseEntity<AppUser> getUserByTelegramId(@PathVariable Long telegramId) {
        Optional<AppUser> userOptional = userService.findByTelegramId(telegramId);
        if (userOptional.isPresent()) {
            return new ResponseEntity<>(userOptional.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}