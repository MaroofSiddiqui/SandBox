package com.sandbox.proctoring.evaluation.dto;

public class SubmissionResponse {

    private boolean success;
    private String message;
    private int hiddenTestsPassed;
    private int hiddenTotalTests;

    // MongoDB ID of the saved AI evaluation.
    // Assessment Service can store this in CandidateAnswer.codingEvaluationId.
    private String evaluationId;

    public SubmissionResponse(
            boolean success,
            String message,
            int hiddenTestsPassed,
            int hiddenTotalTests,
            String evaluationId) {

        this.success = success;
        this.message = message;
        this.hiddenTestsPassed = hiddenTestsPassed;
        this.hiddenTotalTests = hiddenTotalTests;
        this.evaluationId = evaluationId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getHiddenTestsPassed() {
        return hiddenTestsPassed;
    }

    public int getHiddenTotalTests() {
        return hiddenTotalTests;
    }

    public String getEvaluationId() {
        return evaluationId;
    }
}