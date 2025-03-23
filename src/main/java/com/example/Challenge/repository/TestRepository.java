package com.example.Challenge.repository;

import com.example.Challenge.model.Test;
import com.example.Challenge.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    List<Test> findByCreatedBy(User createdBy);

    @Query("SELECT t FROM Test t WHERE t.isActive = true AND t.startTime <= ?1 AND t.endTime >= ?1")
    List<Test> findActiveTests(LocalDateTime now);

    @Query("SELECT t FROM Test t WHERE t.isActive = true AND t.startTime <= ?1 AND t.endTime >= ?1 AND t.subject = ?2")
    List<Test> findActiveTestsBySubject(LocalDateTime now, String subject);

    @Query("SELECT DISTINCT t.subject FROM Test t WHERE t.isActive = true")
    List<String> findAllActiveSubjects();
}

