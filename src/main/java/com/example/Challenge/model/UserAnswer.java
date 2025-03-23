package com.example.Challenge.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_answers")
public class UserAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_test_id", nullable = false)
    private UserTest userTest;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_answer_options",
            joinColumns = @JoinColumn(name = "user_answer_id"),
            inverseJoinColumns = @JoinColumn(name = "option_id")
    )
    private List<Option> selectedOptions = new ArrayList<>();

    private Boolean isCorrect = false;

    private Integer earnedPoints = 0;
}
