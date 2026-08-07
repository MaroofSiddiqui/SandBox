package com.sandbox.assessment.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "assessment_submissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Candidate belongs to Auth Service,
     * so we store only the ID here.
     *
     * Do NOT create a JPA relationship to User
     * because User is owned by another microservice.
     */
    @Column(nullable = false)
    private Long candidateId;

    /*
     * Assessment belongs to this service,
     * therefore a normal JPA relationship is correct.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    private LocalDateTime submittedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus status;

    private Double score;

    public enum SubmissionStatus {
        IN_PROGRESS,
        SUBMITTED,
        EVALUATED
    }

    @PrePersist
    protected void onCreate() {

        if (startedAt == null) {
            startedAt = LocalDateTime.now();
        }

        if (status == null) {
            status = SubmissionStatus.IN_PROGRESS;
        }
    }
}