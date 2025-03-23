package com.example.Challenge.repository;

import com.example.Challenge.model.Option;
import com.example.Challenge.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {
    List<Option> findByQuestion(Question question);
    List<Option> findByQuestionAndIsCorrect(Question question, Boolean isCorrect);
}

