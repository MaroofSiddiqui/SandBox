package com.sandbox.assessment.assignment.schedule.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Response DTO returned after
 * scheduling operations.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentScheduleResponseDto {

    private Long id;

    private Long assignmentId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer maxAttempts;

    private Boolean active;

    private LocalDateTime createdAt;
}