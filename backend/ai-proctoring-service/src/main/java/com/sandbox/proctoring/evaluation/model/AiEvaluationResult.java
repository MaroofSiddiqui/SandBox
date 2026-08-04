package com.sandbox.proctoring.evaluation.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

// Marks this class as a MongoDB document mapped to the specified collection
@Document(collection = "ai_evaluation_results")
public class AiEvaluationResult {

    @Id
    private String id;
    private String studentId;
    private String submissionId;
    private double score;
    private String feedback;
    private LocalDateTime evaluatedAt = LocalDateTime.now();
    private String sourceCode;
    private int languageId;
    private String stdout;
    private double codeQualityScore;
    private String efficiencyComments;
    private String bugsFound;
    private String constructiveFeedback;
    private int testsPassed;
    private int totalTests;
    private double correctnessScore;
    private int hiddenTestsPassed;
    private int hiddenTotalTests;

    // Default constructor
    public AiEvaluationResult() {
        this.evaluatedAt = LocalDateTime.now();
    }

    // Parameterized constructor
    public AiEvaluationResult(String studentId, String submissionId, double score, String feedback) {
        this.studentId = studentId;
        this.submissionId = submissionId;
        this.score = score;
        this.feedback = feedback;
        this.evaluatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public LocalDateTime getEvaluatedAt() {
        return evaluatedAt;
    }

    public void setEvaluatedAt(LocalDateTime evaluatedAt) {
        this.evaluatedAt = evaluatedAt;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public int getLanguageId() {
        return languageId;
    }

    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }

    public String getStdout() {
        return stdout;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }
    
    public double getCodeQualityScore() {
        return codeQualityScore;
    }

    public void setCodeQualityScore(double codeQualityScore) {
        this.codeQualityScore = codeQualityScore;
    }

    public String getEfficiencyComments() {
        return efficiencyComments;
    }

    public void setEfficiencyComments(String efficiencyComments) {
        this.efficiencyComments = efficiencyComments;
    }

    public String getBugsFound() {
        return bugsFound;
    }

    public void setBugsFound(String bugsFound) {
        this.bugsFound = bugsFound;
    }

    public String getConstructiveFeedback() {
        return constructiveFeedback;
    }

    public void setConstructiveFeedback(String constructiveFeedback) {
        this.constructiveFeedback = constructiveFeedback;
    }
    public int getTestsPassed() {
        return testsPassed;
    }

    public void setTestsPassed(int testsPassed) {
        this.testsPassed = testsPassed;
    }

    public int getTotalTests() {
        return totalTests;
    }

    public void setTotalTests(int totalTests) {
        this.totalTests = totalTests;
    }

    public double getCorrectnessScore() {
        return correctnessScore;
    }

    public void setCorrectnessScore(double correctnessScore) {
        this.correctnessScore = correctnessScore;
    }
    public int getHiddenTestsPassed() {
        return hiddenTestsPassed;
    }

    public void setHiddenTestsPassed(int hiddenTestsPassed) {
        this.hiddenTestsPassed = hiddenTestsPassed;
    }

    public int getHiddenTotalTests() {
        return hiddenTotalTests;
    }

    public void setHiddenTotalTests(int hiddenTotalTests) {
        this.hiddenTotalTests = hiddenTotalTests;
    }
}