package com.example.Challenge.dto;
import lombok.Data;

@Data
public class OptionResultResponse {
    private Long optionId;
    private String optionText;
    private Boolean isCorrect;
}
