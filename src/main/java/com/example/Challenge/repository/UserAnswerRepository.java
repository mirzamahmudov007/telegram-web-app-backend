package com.example.Challenge.repository;

import com.example.Challenge.model.Question;
import com.example.Challenge.model.UserAnswer;
import com.example.Challenge.model.UserTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
    List<UserAnswer> findByUserTest(UserTest userTest);
    Optional<UserAnswer> findByUserTestAndQuestion(UserTest userTest, Question question);
}

