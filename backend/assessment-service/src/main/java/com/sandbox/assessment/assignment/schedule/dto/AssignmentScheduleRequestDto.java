package com.sandbox.assessment.assignment.schedule.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Request DTO used by HR
 * to schedule an assigned assessment.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentScheduleRequestDto {

    private Long assignmentId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer maxAttempts;
}