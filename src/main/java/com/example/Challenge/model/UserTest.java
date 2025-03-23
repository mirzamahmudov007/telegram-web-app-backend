package com.example.Challenge.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_tests")
public class UserTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Boolean isCompleted = false;

    private Integer score = 0;

    private Integer maxScore = 0;

    @OneToMany(mappedBy = "userTest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAnswer> answers = new ArrayList<>();

    // Helper method to add an answer
    public void addAnswer(UserAnswer answer) {
        answers.add(answer);
        answer.setUserTest(this);
    }

    // Helper method to remove an answer
    public void removeAnswer(UserAnswer answer) {
        answers.remove(answer);
        answer.setUserTest(null);
    }
}
