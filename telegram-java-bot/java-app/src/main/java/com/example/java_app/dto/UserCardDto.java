package com.example.java_app.dto;

public class UserCardDto {
    private Long id;
    private Long userId;
    private String cardName;
    private Integer lastNum;

    public UserCardDto() {

    }

    public UserCardDto(Long id, Long userId, String cardName, Integer lastNum) {
        this.id = id;
        this.userId = userId;
        this.cardName = cardName;
        this.lastNum = lastNum;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public Integer getLastNum() {
        return lastNum;
    }

    public void setLastNum(Integer lastNum) {
        this.lastNum = lastNum;
    }
}
