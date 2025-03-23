package com.example.Challenge.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TestCreateRequest {
    private String title;
    private String subject;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationMinutes;
    private Boolean isActive = false;
    private List<QuestionCreateRequest> questions;
}