package com.example.Challenge.dto;

import lombok.Data;

import java.util.List;

@Data
public class AnswerSubmitRequest {
    private Long questionId;
    private List<Long> optionIds;
}

