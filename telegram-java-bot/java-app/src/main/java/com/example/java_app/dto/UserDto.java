package com.example.java_app.dto;

public class UserDto {

    private Long id;
    private Long telegramId;
    private String nickname;
    private String email;
    /*private String username;
    private String firstName;
    private String lastName;*/

    public UserDto() {}

    public UserDto(Long id, Long telegramId, String nickname, String email) {
        this.id = id;
        this.telegramId = telegramId;
        this.nickname = nickname;
        this.email = email;
        /*this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;*/
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /*public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }*/
}