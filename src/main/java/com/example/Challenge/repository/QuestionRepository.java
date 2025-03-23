package com.example.Challenge.repository;

import com.example.Challenge.model.Question;
import com.example.Challenge.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByTest(Test test);
}

