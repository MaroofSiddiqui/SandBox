package com.sandbox.assessment.dto.request;

import com.sandbox.assessment.enums.AssessmentStatus;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssessmentRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be greater than 0")
    private Integer durationMinutes;

    @NotNull(message = "Total marks are required")
    @Min(value = 1, message = "Total marks must be greater than 0")
    private Integer totalMarks;

    @NotNull(message = "Passing marks are required")
    @Min(value = 1, message = "Passing marks must be greater than 0")
    private Integer passingMarks;

    @NotNull(message = "Status is required")
    private AssessmentStatus status;

    @NotNull(message = "Created By is required")
    @Min(value = 1, message = "Created By must be greater than 0")
    private Long createdBy;
}