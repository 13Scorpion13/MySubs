package com.example.java_app.repository;

import com.example.java_app.model.Subs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

@Repository
public interface SubsRepository extends JpaRepository<Subs, Long> {
    List<Subs> findByUserId(Long userId);
    List<Subs> findByUserCardId(Long userCardId);
}
