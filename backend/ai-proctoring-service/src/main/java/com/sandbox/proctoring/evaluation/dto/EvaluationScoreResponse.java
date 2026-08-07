package com.sandbox.proctoring.evaluation.dto;

public class EvaluationScoreResponse {

    private String evaluationId;
    private String studentId;
    private String submissionId;
    private double score;

    public EvaluationScoreResponse(
            String evaluationId,
            String studentId,
            String submissionId,
            double score) {

        this.evaluationId = evaluationId;
        this.studentId = studentId;
        this.submissionId = submissionId;
        this.score = score;
    }

    public String getEvaluationId() {
        return evaluationId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public double getScore() {
        return score;
    }
}