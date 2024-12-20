package com.example.java_app.dto.userDto;

public class ShowUserDto {
    private Long id;
    private String nickname;
    private boolean consent;

    public ShowUserDto() {

    }

    public ShowUserDto(Long id, String nickname, boolean consent) {
        this.id = id;
        this.nickname = nickname;
        this.consent = consent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
