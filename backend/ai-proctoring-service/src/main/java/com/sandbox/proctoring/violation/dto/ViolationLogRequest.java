package com.sandbox.proctoring.violation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Simple DTO class to validate incoming violation data
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViolationLogRequest {

    // Ensures candidate ID is provided
    @NotBlank(message = "Candidate ID cannot be blank")
    private String candidateId;

    // Ensures exam ID is provided
    @NotBlank(message = "Exam ID cannot be blank")
    private String examId;

    // Defines the type of violation detected
    @NotBlank(message = "Violation type is required")
    private String violationType;

    // Timestamp when the event happened
    @NotNull(message = "Timestamp is required")
    private Long timestamp;

    // Optional extra information or metadata
    private String details;
}