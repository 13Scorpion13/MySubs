package com.example.java_app.dto;

import java.math.BigDecimal;

public class TariffDto {
    private Long id;
    private int durationMonths;
    private BigDecimal price;

    public TariffDto() {

    }

    public TariffDto(Long id, int durationMonths, BigDecimal price) {
        this.id = id;
        this.durationMonths = durationMonths;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
