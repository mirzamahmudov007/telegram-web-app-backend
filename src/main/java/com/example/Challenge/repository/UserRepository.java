package com.example.Challenge.repository;

import com.example.Challenge.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByTelegramId(String telegramId);
    boolean existsByUsername(String username);
    boolean existsByTelegramId(String telegramId);
}


