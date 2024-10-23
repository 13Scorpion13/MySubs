package com.example.java_app.service;

import com.example.java_app.dto.UserCardDto;
import com.example.java_app.model.AppUser;
import com.example.java_app.model.UserCard;
import com.example.java_app.repository.UserCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserCardService {

    private final UserCardRepository userCardRepository;
    private final UserService userService;

    @Autowired
    public UserCardService(UserCardRepository userCardRepository, UserService userService) {
        this.userCardRepository = userCardRepository;
        this.userService = userService;
    }

    public List<UserCard> getAllUserCards() {
        return userCardRepository.findAll();
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

    public List<UserCardDto> getUserCardDtoByTelegramId(Long telegramId) {
        Optional<AppUser> userOptional = userService.findByTelegramId(telegramId);
        if (userOptional.isPresent()) {
            Long userId = userOptional.get().getId();
            List<UserCard> userCards = userCardRepository.findByUserId(userId);
            return userCards.stream()
                    .map(card -> new UserCardDto(card.getId(), card.getUser().getId(), card.getCardName(), card.getLastNum()))
                    .toList();
        }
        return List.of();
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
}
