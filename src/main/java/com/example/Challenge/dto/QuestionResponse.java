package com.example.Challenge.dto;

import com.example.Challenge.model.Question;
import lombok.Data;

import java.util.List;

@Data
public class QuestionResponse {
    private Long questionId;
    private String questionText;
    private Question.QuestionType questionType;
    private Integer points;
    private List<OptionResponse> options;
}

