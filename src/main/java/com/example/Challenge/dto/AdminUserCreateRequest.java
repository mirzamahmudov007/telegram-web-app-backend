package com.example.Challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserCreateRequest {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String telegramId;
    private String role;
}

