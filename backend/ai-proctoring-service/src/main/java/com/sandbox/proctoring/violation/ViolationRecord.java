package com.sandbox.proctoring.violation;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

// saves data inside mongo collection named violation_records
@Document(collection = "violation_records")
public class ViolationRecord {

    // unique db id
    @Id
    private String id;
    
    // basic user and exam info
    private String candidateId;
    private String examId;
    private String violationType;
    private String timestamp;
    private String details; // optional extra information
    
    // saved video paths on server
    private String webcamVideoUrl;
    private String screenVideoUrl;
    
    // creation time tag
    private LocalDateTime createdAt = LocalDateTime.now();

    // empty constructor for spring
    public ViolationRecord() {}

    // constructor for quick text logs
    public ViolationRecord(String candidateId, String examId, String violationType, String timestamp) {
        this.candidateId = candidateId;
        this.examId = examId;
        this.violationType = violationType;
        this.timestamp = timestamp;
    }

    // getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCandidateId() { return candidateId; }
    public void setCandidateId(String candidateId) { this.candidateId = candidateId; }

    public String getExamId() { return examId; }
    public void setExamId(String examId) { this.examId = examId; }

    public String getViolationType() { return violationType; }
    public void setViolationType(String violationType) { this.violationType = violationType; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getWebcamVideoUrl() { return webcamVideoUrl; }
    public void setWebcamVideoUrl(String webcamVideoUrl) { this.webcamVideoUrl = webcamVideoUrl; }

    public String getScreenVideoUrl() { return screenVideoUrl; }
    public void setScreenVideoUrl(String screenVideoUrl) { this.screenVideoUrl = screenVideoUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}