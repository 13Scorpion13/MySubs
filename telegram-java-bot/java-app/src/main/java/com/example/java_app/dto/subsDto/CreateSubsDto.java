package com.example.java_app.dto.subsDto;

import java.time.LocalDate;

public class CreateSubsDto {
    private Long telegramId;
    private Long categoryId;
    private Long serviceId;
    private Long tariffId;
    private Long userCardId;
    private LocalDate date;

    public CreateSubsDto() {

    }

    public CreateSubsDto(Long telegramId, Long categoryId, Long serviceId, Long tariffId, Long userCardId, LocalDate date) {
        this.telegramId = telegramId;
        this.categoryId = categoryId;
        this.serviceId = serviceId;
        this.tariffId = tariffId;
        this.userCardId = userCardId;
        this.date = date;
    }

    public Long getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(Long telegramId) {
        this.telegramId = telegramId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long getTariffId() {
        return tariffId;
    }

    public void setTariffId(Long tariffId) {
        this.tariffId = tariffId;
    }

    public Long getUserCardId() {
        return userCardId;
    }

    public void setUserCardId(Long userCardId) {
        this.userCardId = userCardId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
