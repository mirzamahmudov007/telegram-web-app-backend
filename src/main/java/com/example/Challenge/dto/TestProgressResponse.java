package com.example.Challenge.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TestProgressResponse {
    private Long userTestId;
    private Long testId;
    private String testTitle;
    private LocalDateTime startedAt;
    private LocalDateTime expiresAt;
    private Boolean isCompleted;
    private Long remainingSeconds;
    private Integer totalQuestions;
    private Integer answeredQuestions;
    private Double progressPercentage;
}