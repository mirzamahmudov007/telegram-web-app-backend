package com.example.Challenge.dto;

import com.example.Challenge.model.UserTest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTestStartDTO {
    private Long id;
    private Long userId;
    private Long testId;
    private String testTitle;
    private LocalDateTime startedAt;
    private LocalDateTime expiresAt;
    private Boolean isCompleted;
    private Integer maxScore;

    public static UserTestStartDTO fromEntity(UserTest userTest) {
        UserTestStartDTO dto = new UserTestStartDTO();
        dto.setId(userTest.getId());
        dto.setUserId(userTest.getUser().getId());
        dto.setTestId(userTest.getTest().getId());
        dto.setTestTitle(userTest.getTest().getTitle());
        dto.setStartedAt(userTest.getStartedAt());
        dto.setExpiresAt(userTest.getExpiresAt());
        dto.setIsCompleted(userTest.getIsCompleted());
        dto.setMaxScore(userTest.getMaxScore());
        return dto;
    }
}