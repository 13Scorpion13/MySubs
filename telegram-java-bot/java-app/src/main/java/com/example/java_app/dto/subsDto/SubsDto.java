package com.example.java_app.dto.subsDto;

import java.time.LocalDate;

public class SubsDto {
    private Long id;
    private Long userId;
    private Long serviceId;
    private Long categoryId;
    private Long tariffId;
    private Long userCardId;
    private LocalDate date;

    public SubsDto() {

    }

    public SubsDto(Long id,
                   Long userId,
                   Long serviceId,
                   Long categoryId,
                   Long tariffId,
                   Long userCardId,
                   LocalDate date
    ) {
        this.id = id;
        this.userId = userId;
        this.serviceId = serviceId;
        this.categoryId = categoryId;
        this.tariffId = tariffId;
        this.userCardId = userCardId;
        this.date = date;
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

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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
