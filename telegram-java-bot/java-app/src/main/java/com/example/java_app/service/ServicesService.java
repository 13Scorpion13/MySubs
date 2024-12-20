package com.example.java_app.service;

import com.example.java_app.dto.ServiceDto;
import com.example.java_app.model.Services;
import com.example.java_app.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServicesService {
    private final ServiceRepository serviceRepository;

    @Autowired
    public ServicesService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public List<Services> getAllServices() {
        return serviceRepository.findAll();
    }

    private ServiceDto convertToDto(Services services) {
        Long categoryId = services.getCategory().getId();
        return new ServiceDto(services.getId(), services.getServiceName(), categoryId);
    }

    public List<ServiceDto> getServiceByCategoryId(Long categoryId) {
        List<Services> servicesList = serviceRepository.findByCategoryId(categoryId);
        return servicesList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ServiceDto> getAllServicesDto() {
        List<Services> servicesList = serviceRepository.findAll();
        return servicesList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<Services> getServiceById(Long id) {
        return serviceRepository.findById(id);
    }

    public Services saveService(Services services) {
        return serviceRepository.save(services);
    }

    public void deleteService(Long id) {
        serviceRepository.deleteById(id);
    }
}
