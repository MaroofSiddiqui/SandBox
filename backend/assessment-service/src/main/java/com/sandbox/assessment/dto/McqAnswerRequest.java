package com.sandbox.assessment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class McqAnswerRequest {

    private Long submissionId;
    private Long questionId;
    private Long selectedOptionId;
}