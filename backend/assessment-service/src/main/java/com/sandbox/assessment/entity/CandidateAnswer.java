package com.sandbox.assessment.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "candidate_answers",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"submission_id", "question_id"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private AssessmentSubmission submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    /*
     * Used for MCQ answers.
     * Null for coding questions.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_option_id")
    private McqOption selectedOption;

    /*
     * Coding evaluation is owned by 8083/MongoDB.
     * We only store its evaluation ID here.
     */
    private String codingEvaluationId;

    private Boolean correct;

    private Double awardedMarks;

    private LocalDateTime answeredAt;

    @PrePersist
    @PreUpdate
    protected void updateTimestamp() {
        answeredAt = LocalDateTime.now();
    }
}