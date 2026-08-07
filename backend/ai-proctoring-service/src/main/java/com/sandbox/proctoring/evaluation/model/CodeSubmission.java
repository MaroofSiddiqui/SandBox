package com.sandbox.proctoring.evaluation.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeSubmission {

	private String submissionId;
    private String questionId;
    private String sourceCode;
    private int languageId;
}