package com.example.java_app.dto.subsDto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ShowSubsDto {
    private Long id;
    private String serviceName;
    private BigDecimal tariffPrice;
    private int tariffDuration;
    private String userCardName;
    private LocalDate date;


    public ShowSubsDto() {

    }

    public ShowSubsDto(Long id, String serviceName, BigDecimal tariffPrice, int tariffDuration, String userCardName, LocalDate date) {
        this.id = id;
        this.serviceName = serviceName;
        this.tariffPrice = tariffPrice;
        this.tariffDuration = tariffDuration;
        this.userCardName = userCardName;
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

    public BigDecimal getTariffPrice() {
        return tariffPrice;
    }
    public void setTariffPrice(BigDecimal tariffPrice) {
        this.tariffPrice = tariffPrice;
    }

    public int getTariffDuration() {
        return tariffDuration;
    }
    public void setTariffDuration(int tariffDuration) {
        this.tariffDuration = tariffDuration;
    }

    public String getUserCardName() {
        return userCardName;
    }
    public void setUserCardName(String userCardName) {
        this.userCardName = userCardName;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
}
