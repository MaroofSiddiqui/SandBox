package com.sandbox.proctoring.evaluation.dto;

public class SubmissionResponse {
    private boolean success;
    private String message;
    private int hiddenTestsPassed;
    private int hiddenTotalTests;

    public SubmissionResponse(boolean success, String message, int hiddenTestsPassed, int hiddenTotalTests) {
        this.success = success;
        this.message = message;
        this.hiddenTestsPassed = hiddenTestsPassed;
        this.hiddenTotalTests = hiddenTotalTests;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public int getHiddenTestsPassed() { return hiddenTestsPassed; }
    public int getHiddenTotalTests() { return hiddenTotalTests; }
}