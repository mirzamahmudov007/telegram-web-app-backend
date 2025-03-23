package com.example.Challenge.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "test"})
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String text;

    @Column(nullable = false)
    private Integer points = 1;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private QuestionType type = QuestionType.SINGLE_CHOICE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    @JsonBackReference
    private Test test;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options = new ArrayList<>();

    // Helper method to add an option
    public void addOption(Option option) {
        options.add(option);
        option.setQuestion(this);
    }

    // Helper method to remove an option
    public void removeOption(Option option) {
        options.remove(option);
        option.setQuestion(null);
    }

    public enum QuestionType {
        SINGLE_CHOICE,
        MULTIPLE_CHOICE
    }
}
