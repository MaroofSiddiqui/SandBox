package com.sandbox.assessment.assignment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

/**
 * Request DTO used to assign an assessment to a candidate.
 *
 * Member 4 Module
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentRequestDto {

    /**
     * Assessment ID must be present
     * and greater than zero.
     */
    @NotNull(message = "Assessment Id is required")
    @Positive(message = "Assessment Id must be greater than zero")
    private Long assessmentId;

    /**
     * Candidate ID must be present
     * and greater than zero.
     */
    @NotNull(message = "Candidate Id is required")
    @Positive(message = "Candidate Id must be greater than zero")
    private Long candidateId;
}