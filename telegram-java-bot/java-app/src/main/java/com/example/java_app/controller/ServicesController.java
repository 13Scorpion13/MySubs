package com.example.java_app.controller;

import com.example.java_app.dto.ServiceDto;
import com.example.java_app.model.Services;
import com.example.java_app.service.ServicesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServicesController {
    private final ServicesService servicesService;

    @Autowired
    public ServicesController(ServicesService servicesService) {
        this.servicesService = servicesService;
    }

    /*@GetMapping
    public List<Services> getAllServices() {
        return servicesService.getAllServices();
    }*/

    @GetMapping
    public ResponseEntity<List<ServiceDto>> getAllServices() {
        List<ServiceDto> servicesDtoList = servicesService.getAllServicesDto();
        return ResponseEntity.ok(servicesDtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Services> getServiceById(@PathVariable Long id) {
        return servicesService.getServiceById(id)
                .map(service -> new ResponseEntity<>(service, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Services> createService(@RequestBody Services service) {
        Services savedService = servicesService.saveService(service);
        return new ResponseEntity<>(savedService, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        servicesService.deleteService(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
