package com.example.java_app.dto;

public class ServiceDto {
    private Long id;
    private String serviceName;
    private Long categoryId;

    public ServiceDto() {

    }

    public ServiceDto(Long id, String serviceName, Long categoryId) {
        this.id = id;
        this.serviceName = serviceName;
        this.categoryId = categoryId;
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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
