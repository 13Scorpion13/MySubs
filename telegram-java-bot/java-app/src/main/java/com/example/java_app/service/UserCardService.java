package com.example.java_app.service;

import com.example.java_app.dto.userCardDto.ShowUserCardDto;
import com.example.java_app.dto.userCardDto.UpdateUserCardDto;
import com.example.java_app.exceptions.ResourceNotFoundException;
import com.example.java_app.model.AppUser;
import com.example.java_app.model.UserCard;
import com.example.java_app.repository.UserCardRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserCardService {

    private final UserCardRepository userCardRepository;
    private final UserService userService;

    @Autowired
    public UserCardService(UserCardRepository userCardRepository, UserService userService) {
        this.userCardRepository = userCardRepository;
        this.userService = userService;
    }

    public Optional<UserCard> getUserCardById(Long id) {
        return userCardRepository.findById(id);
    }

    public UserCard saveUserCard(UserCard userCard) {
        return userCardRepository.save(userCard);
    }

    public void deleteUserCard(Long id) {
        userCardRepository.deleteById(id);
    }

    public List<ShowUserCardDto> getUserCardByUserId(Long userId) {
        List<UserCard> userCard = userCardRepository.findByUserId(userId);
        return userCard.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ShowUserCardDto convertToDto(UserCard userCard) {
        return new ShowUserCardDto(
                userCard.getId(),
                userCard.getCardName(),
                userCard.getLastNum()
        );
    }

    public Optional<UserCard> createUserCardForTelegramUser(Long telegramId, String cardName, Integer lastNum) {
        Optional<AppUser> userOptional = userService.findByTelegramId(telegramId);

        if (userOptional.isPresent()) {
            AppUser user = userOptional.get();
            UserCard userCard = new UserCard();
            userCard.setUser(user);
            userCard.setCardName(cardName);
            userCard.setLastNum(lastNum);

            return Optional.of(userCardRepository.save(userCard));
        }

        return Optional.empty();
    }

    @Transactional
    public void updateUserCard(Long id, UpdateUserCardDto updateDto) {
        UserCard userCard = userCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        if (updateDto.getCardName() != null) {
            userCard.setCardName(updateDto.getCardName());
        }

        if (updateDto.getLastNum() != null) {
            userCard.setLastNum(updateDto.getLastNum());
        }

        userCardRepository.save(userCard);
    }
}