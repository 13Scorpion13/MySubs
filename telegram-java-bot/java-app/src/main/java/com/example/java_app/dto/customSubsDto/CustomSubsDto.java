package com.example.java_app.dto.customSubsDto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CustomSubsDto {
    private Long id;
    private Long userId;
    private String serviceName;
    private int durationMonths;
    private BigDecimal price;
    private Long userCardId;
    private LocalDate date;

    public CustomSubsDto() {

    }

    public CustomSubsDto(Long id, Long userId, String serviceName, int durationMonths, BigDecimal price, Long userCardId, LocalDate date) {
        this.id = id;
        this.userId = userId;
        this.serviceName = serviceName;
        this.durationMonths = durationMonths;
        this.price = price;
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

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(int durationMonths) {
        this.durationMonths = durationMonths;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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
