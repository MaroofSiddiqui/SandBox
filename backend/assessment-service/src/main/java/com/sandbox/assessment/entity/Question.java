package com.sandbox.assessment.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType questionType; // MCQ or CODING

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DifficultyLevel difficulty; // EASY, MEDIUM, HARD

    @Column(nullable = false)
    private Double marks;

    private String category;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<McqOption> mcqOptions;

    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private CodingQuestion codingQuestion;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public enum QuestionType {
        MCQ,
        CODING
    }

    public enum DifficultyLevel {
        EASY,
        MEDIUM,
        HARD
    }
}