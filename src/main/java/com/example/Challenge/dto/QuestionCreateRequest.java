package com.example.Challenge.dto;

import com.example.Challenge.model.Question;
import lombok.Data;

import java.util.List;

@Data
public class QuestionCreateRequest {
    private String text;
    private Integer points = 1;
    private Question.QuestionType type = Question.QuestionType.SINGLE_CHOICE;
    private List<OptionCreateRequest> options;
}

