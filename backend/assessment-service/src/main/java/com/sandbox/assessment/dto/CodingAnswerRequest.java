package com.sandbox.assessment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodingAnswerRequest {

    private Long submissionId;
    private Long questionId;
    private String codingEvaluationId;
}