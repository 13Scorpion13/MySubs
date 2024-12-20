package com.example.java_app.dto.customSubsDto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UpdateCustomSubsDto {
    private Long id;
    private String serviceName;
    private int durationMonths;
    private BigDecimal price;
    private Long userCardId;
    private LocalDate date;

    public UpdateCustomSubsDto() {

    }

    public UpdateCustomSubsDto(Long id, String serviceName, int durationMonths, BigDecimal price,
                               Long userCardId, LocalDate date
    ) {
        this.id = id;
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
