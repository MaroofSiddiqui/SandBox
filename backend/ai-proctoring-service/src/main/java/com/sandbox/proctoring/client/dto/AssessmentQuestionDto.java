package com.sandbox.proctoring.client.dto;

public class AssessmentQuestionDto {

    private Long id;
    private String title;
    private String questionType;
    private String difficulty;
    private Double marks;
    private String category;

    private String problemStatement;
    private String driverCode;
    private String sampleTestCasesJson;
    private String hiddenTestCasesJson;
    private Integer timeLimitInSeconds;
    private Integer memoryLimitInMb;

    public AssessmentQuestionDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Double getMarks() {
        return marks;
    }

    public void setMarks(Double marks) {
        this.marks = marks;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProblemStatement() {
        return problemStatement;
    }

    public void setProblemStatement(String problemStatement) {
        this.problemStatement = problemStatement;
    }

    public String getDriverCode() {
        return driverCode;
    }

    public void setDriverCode(String driverCode) {
        this.driverCode = driverCode;
    }

    public String getSampleTestCasesJson() {
        return sampleTestCasesJson;
    }

    public void setSampleTestCasesJson(String sampleTestCasesJson) {
        this.sampleTestCasesJson = sampleTestCasesJson;
    }

    public String getHiddenTestCasesJson() {
        return hiddenTestCasesJson;
    }

    public void setHiddenTestCasesJson(String hiddenTestCasesJson) {
        this.hiddenTestCasesJson = hiddenTestCasesJson;
    }

    public Integer getTimeLimitInSeconds() {
        return timeLimitInSeconds;
    }

    public void setTimeLimitInSeconds(Integer timeLimitInSeconds) {
        this.timeLimitInSeconds = timeLimitInSeconds;
    }

    public Integer getMemoryLimitInMb() {
        return memoryLimitInMb;
    }

    public void setMemoryLimitInMb(Integer memoryLimitInMb) {
        this.memoryLimitInMb = memoryLimitInMb;
    }
}