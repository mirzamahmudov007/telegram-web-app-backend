package com.example.Challenge.dto;
import lombok.Data;

@Data
public class OptionCreateRequest {
    private String text;
    private Boolean isCorrect = false;
}

