package com.example.java_app.repository;

import com.example.java_app.model.CustomSubs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomSubsRepository extends JpaRepository<CustomSubs, Long> {
    List<CustomSubs> findByUserId(Long userId);
}
