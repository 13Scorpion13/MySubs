package com.example.java_app.dto.subsDto;

import java.time.LocalDate;

public class UpdateSubsDto {
    private Long id;
    private Long serviceId;
    private Long tariffId;
    private Long userCardId;
    private LocalDate date;

    public UpdateSubsDto() {

    }

    public UpdateSubsDto(Long id, Long serviceId, Long tariffId, Long userCardId, LocalDate date) {
        this.id = id;
        this.serviceId = serviceId;
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
