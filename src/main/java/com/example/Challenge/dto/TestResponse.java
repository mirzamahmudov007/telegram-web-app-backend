package com.example.Challenge.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TestResponse {
    private Long id;
    private String title;
    private String subject;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationMinutes;
    private Boolean isActive;
    private String createdBy;
    private Integer questionCount;
    private Integer totalPoints;
}
