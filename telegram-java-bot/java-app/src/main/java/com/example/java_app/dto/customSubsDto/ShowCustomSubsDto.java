package com.example.java_app.dto.customSubsDto;

import java.math.BigDecimal;

public class ShowCustomSubsDto {
    private Long id;
    private String serviceName;
    private int durationMonths;
    private BigDecimal price;
    private String userCardName;

    public ShowCustomSubsDto() {

    }

    public ShowCustomSubsDto(Long id, String serviceName, int durationMonths, BigDecimal price, String userCardName) {
        this.id = id;
        this.serviceName = serviceName;
        this.durationMonths = durationMonths;
        this.price = price;
        this.userCardName = userCardName;
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

    public String getUserCardName() {
        return userCardName;
    }

    public void setUserCardName(String userCardName) {
        this.userCardName = userCardName;
    }
}
