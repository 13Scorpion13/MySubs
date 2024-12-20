package com.example.java_app.dto.userDto;

public class UserDto {

    private Long id;
    private Long telegramId;
    private String nickname;
    private boolean consent;

    public UserDto() {

    }

    public UserDto(Long id, Long telegramId, String nickname, boolean consent) {
        this.id = id;
        this.telegramId = telegramId;
        this.nickname = nickname;
        this.consent = consent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(Long telegramId) {
        this.telegramId = telegramId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isConsent() {
        return consent;
    }

    public void setConsent(boolean consent) {
        this.consent = consent;
    }
}