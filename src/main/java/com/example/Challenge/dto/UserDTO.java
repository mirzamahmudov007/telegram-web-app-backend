package com.example.Challenge.dto;

import com.example.Challenge.model.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {
    private Long id;
    private String telegramId;
    private String username;
    private String firstName;
    private String lastName;
    private String role;
    private Set<String> permissions;

    // Static method to convert User entity to UserDTO
    public static UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setTelegramId(user.getTelegramId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole());
        dto.setPermissions(user.getPermissions());
        return dto;
    }
}

