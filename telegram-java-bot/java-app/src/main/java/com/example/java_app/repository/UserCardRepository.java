package com.example.java_app.repository;

import com.example.java_app.model.UserCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCardRepository extends JpaRepository<UserCard, Long> {
    List<UserCard> findByUserId(Long userId);
}
