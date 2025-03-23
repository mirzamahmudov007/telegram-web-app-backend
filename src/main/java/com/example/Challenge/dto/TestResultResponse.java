package com.example.Challenge.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TestResultResponse {
    private Long userTestId;
    private Long testId;
    private String testTitle;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private Integer score;
    private Integer maxScore;
    private Double scorePercentage;
    private List<QuestionResultResponse> questionResults;
}