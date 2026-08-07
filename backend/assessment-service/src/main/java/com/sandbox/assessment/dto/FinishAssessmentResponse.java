package com.sandbox.assessment.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FinishAssessmentResponse {

    private Long submissionId;
    private Long assessmentId;
    private Long candidateId;
    private String status;
    private Double score;
    private LocalDateTime submittedAt;
}