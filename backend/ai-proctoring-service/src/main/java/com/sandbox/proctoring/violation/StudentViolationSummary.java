package com.sandbox.proctoring.violation;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// saves aggregated violation counts per candidate inside mongo collection student_violation_summaries
@Document(collection = "student_violation_summaries")
public class StudentViolationSummary {

    @Id
    private String id;
    
    private String candidateId;
    private String examId;
    
    // specific violation counters
    private int windowSwitchCount = 0;     // tab switch + window blur
    private int fullscreenExitCount = 0;   // fullscreen exits
    private int noFaceCount = 0;           // ai no face detected
    private int multiFaceCount = 0;        // ai multiple faces detected
    private int copyPasteCount = 0;        // copy paste attempts
    private int totalViolations = 0;       // total sum

    public StudentViolationSummary() {}

    public StudentViolationSummary(String candidateId, String examId) {
        this.candidateId = candidateId;
        this.examId = examId;
    }

    // helper method to increment specific counters dynamically
    public void incrementViolation(String violationType) {
        if (violationType == null) return;
        
        switch (violationType.toUpperCase()) {
            case "TAB_SWITCH_OVER_5SEC":
            case "WINDOW_BLUR_OVER_5SEC":
            case "KEYBOARD_APPLICATION_SWITCH":
                this.windowSwitchCount++;
                break;
            case "FULLSCREEN_EXIT":
                this.fullscreenExitCount++;
                break;
            case "NO_FACE_DETECTED":
                this.noFaceCount++;
                break;
            case "MULTIPLE_FACES_DETECTED":
                this.multiFaceCount++;
                break;
            case "COPY_PASTE_ATTEMPT":
                this.copyPasteCount++;
                break;
            default:
                break;
        }
        
        this.totalViolations = this.windowSwitchCount + 
                               this.fullscreenExitCount + 
                               this.noFaceCount + 
                               this.multiFaceCount + 
                               this.copyPasteCount;
    }

    // getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCandidateId() { return candidateId; }
    public void setCandidateId(String candidateId) { this.candidateId = candidateId; }

    public String getExamId() { return examId; }
    public void setExamId(String examId) { this.examId = examId; }

    public int getWindowSwitchCount() { return windowSwitchCount; }
    public void setWindowSwitchCount(int windowSwitchCount) { this.windowSwitchCount = windowSwitchCount; }

    public int getFullscreenExitCount() { return fullscreenExitCount; }
    public void setFullscreenExitCount(int fullscreenExitCount) { this.fullscreenExitCount = fullscreenExitCount; }

    public int getNoFaceCount() { return noFaceCount; }
    public void setNoFaceCount(int noFaceCount) { this.noFaceCount = noFaceCount; }

    public int getMultiFaceCount() { return multiFaceCount; }
    public void setMultiFaceCount(int multiFaceCount) { this.multiFaceCount = multiFaceCount; }

    public int getCopyPasteCount() { return copyPasteCount; }
    public void setCopyPasteCount(int copyPasteCount) { this.copyPasteCount = copyPasteCount; }

    public int getTotalViolations() { return totalViolations; }
    public void setTotalViolations(int totalViolations) { this.totalViolations = totalViolations; }
}