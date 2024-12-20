package com.example.java_app.dto;

import java.math.BigDecimal;

public class TariffDto {
    private Long id;
    private Long serviceId;
    private String serviceName;
    private int durationMonths;
    private BigDecimal price;

    public TariffDto() {

    }

    public TariffDto(Long id, Long serviceId, int durationMonths, BigDecimal price) {
        this.id = id;
        this.serviceId = serviceId;
        this.durationMonths = durationMonths;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
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
}
