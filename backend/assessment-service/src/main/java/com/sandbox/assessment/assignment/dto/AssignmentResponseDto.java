package com.sandbox.assessment.assignment.dto;

import com.sandbox.assessment.assignment.enums.AssignmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Response returned after assignment operations.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentResponseDto {

    private Long assignmentId;

    private Long assessmentId;

    private Long candidateId;

    private AssignmentStatus status;

    private LocalDateTime assignedAt;

    private LocalDateTime startedAt;

    private LocalDateTime submittedAt;
}