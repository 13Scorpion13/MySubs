package com.example.java_app.repository;

import com.example.java_app.model.Services;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Services, Long> {
    Optional<Services> findByServiceName(String serviceName);
    List<Services> findByCategoryId(Long categoryId);
}
