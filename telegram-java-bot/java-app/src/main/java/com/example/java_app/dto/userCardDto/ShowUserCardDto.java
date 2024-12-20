package com.example.java_app.dto.userCardDto;

public class ShowUserCardDto {
    private Long id;
    private String cardName;
    private Integer lastNum;

    public ShowUserCardDto() {
    }

    public ShowUserCardDto(Long id, String cardName, Integer lastNum) {
        this.id = id;
        this.cardName = cardName;
        this.lastNum = lastNum;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
