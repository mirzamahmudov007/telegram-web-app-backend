package com.example.Challenge.dto;
import lombok.Data;

import java.util.List;

@Data
public class QuestionResultResponse {
    private Long questionId;
    private String questionText;
    private Integer points;
    private Boolean isAnswered;
    private Boolean isCorrect;
    private Integer earnedPoints;
    private List<Long> selectedOptionIds;
    private List<OptionResultResponse> options;
}