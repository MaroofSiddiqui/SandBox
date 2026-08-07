package com.sandbox.assessment.assignment.schedule.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * ==========================================================
 * Member 4
 *
 * Assignment Schedule Entity
 *
 * Stores scheduling information for assigned assessments.
 * This module is completely independent from Member 3.
 * ==========================================================
 */
@Entity
@Table(name = "assignment_schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Assignment created from Assignment Module.
     */
    @Column(nullable = false)
    private Long assignmentId;

    /**
     * Candidate can start exam after this time.
     */
    @Column(nullable = false)
    private LocalDateTime startTime;

    /**
     * Candidate cannot access exam after this time.
     */
    @Column(nullable = false)
    private LocalDateTime endTime;

    /**
     * Maximum attempts allowed.
     */
    @Builder.Default
    private Integer maxAttempts = 1;

    /**
     * Whether schedule is active.
     */
    @Builder.Default
    private Boolean active = true;

    /**
     * Record creation timestamp.
     */
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }
}