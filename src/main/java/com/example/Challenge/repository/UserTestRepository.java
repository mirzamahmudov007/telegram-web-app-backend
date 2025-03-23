package com.example.Challenge.repository;


import com.example.Challenge.model.Test;
import com.example.Challenge.model.User;
import com.example.Challenge.model.UserTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserTestRepository extends JpaRepository<UserTest, Long> {
    List<UserTest> findByUser(User user);
    List<UserTest> findByTest(Test test);
    Optional<UserTest> findByUserAndTest(User user, Test test);

    @Query("SELECT ut FROM UserTest ut WHERE ut.user = ?1 AND ut.test = ?2 AND ut.isCompleted = false AND ut.expiresAt > ?3")
    Optional<UserTest> findActiveUserTest(User user, Test test, LocalDateTime now);

    @Query("SELECT ut FROM UserTest ut WHERE ut.user = ?1 AND ut.isCompleted = false AND ut.expiresAt > ?2")
    List<UserTest> findActiveUserTests(User user, LocalDateTime now);
}

