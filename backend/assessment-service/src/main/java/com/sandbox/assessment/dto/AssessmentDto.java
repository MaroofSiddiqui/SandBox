package com.sandbox.assessment.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentDto {
    private Long id;
    private String title;
    private String description;
    private Integer durationInMinutes;
    private Double passingMarks;
    private Double negativeMarks;
    private Boolean isPublished;
    private List<Long> questionIds; // List of question IDs attached to this test
}