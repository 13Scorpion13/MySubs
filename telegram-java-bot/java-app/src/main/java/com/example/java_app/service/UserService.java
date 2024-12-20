package com.example.java_app.service;

import com.example.java_app.dto.userDto.ShowUserDto;
import com.example.java_app.dto.userDto.UpdateUserDto;
import com.example.java_app.dto.userDto.UserDto;
import com.example.java_app.exceptions.ResourceNotFoundException;
import com.example.java_app.model.AppUser;
import com.example.java_app.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AppUser createUser(UserDto dto) {
        AppUser user = new AppUser();
        user.setTelegramId(dto.getTelegramId());
        user.setNickname(dto.getNickname());
        user.setConsent(dto.isConsent());
        return userRepository.save(user);
    }

    public Optional<AppUser> findByTelegramId(Long telegramId) {
        return userRepository.findByTelegramId(telegramId);
    }

    public Optional<ShowUserDto> getUserDtoByTelegramId(Long telegramId) {
        return userRepository.findByTelegramId(telegramId)
                .map(this::convertToDto);
    }

    private ShowUserDto convertToDto(AppUser user) {
        return new ShowUserDto(
                user.getId(),
                user.getNickname(),
                user.isConsent()
        );
    }

    @Transactional
    public void updateUserInfo(Long id, UpdateUserDto updateUserDto) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (updateUserDto.getNickname() != null) {
            user.setNickname(updateUserDto.getNickname());
        }

        if (updateUserDto.getConsent() != null) {
            user.setConsent(updateUserDto.getConsent());
        }

        userRepository.save(user);
    }

    public Optional<AppUser> findById(Long id) {
        return userRepository.findById(id);
    }


}