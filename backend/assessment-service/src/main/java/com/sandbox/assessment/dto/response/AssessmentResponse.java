package com.sandbox.assessment.dto.response;

import java.time.LocalDateTime;

import com.sandbox.assessment.enums.AssessmentStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssessmentResponse {

    private Long id;

    private String title;

    private String description;

    private Integer durationMinutes;

    private Integer totalMarks;

    private Integer passingMarks;

    private AssessmentStatus status;

    private Long createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}