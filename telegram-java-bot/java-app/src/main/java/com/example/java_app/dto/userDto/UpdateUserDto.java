package com.example.java_app.dto.userDto;

public class UpdateUserDto {
    private Long id;
    private String nickname;
    private Boolean consent;

    public UpdateUserDto() {

    }

    public UpdateUserDto(Long id, String nickname, boolean consent) {
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

    public Boolean getConsent() {
        return consent;
    }

    public void setConsent(Boolean consent) {
        this.consent = consent;
    }
}
