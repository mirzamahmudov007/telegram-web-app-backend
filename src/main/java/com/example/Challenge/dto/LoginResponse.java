package com.example.Challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public
class LoginResponse {
    private Long
            id;
    private String
            username;
    private String
            firstName;
    private String
            lastName;
    private String
            role;
    private Set<String>
            permissions;
    private String
            accessToken;
    private String
            refreshToken;
}

