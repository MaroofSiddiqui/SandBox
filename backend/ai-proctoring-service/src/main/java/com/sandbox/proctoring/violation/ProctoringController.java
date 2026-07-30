package com.sandbox.proctoring.violation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/proctoring")
@CrossOrigin(origins = "*")
public class ProctoringController {

    @Autowired
    private ProctoringService proctoringService;

    // route for fast text logs
    @PostMapping("/log-violation")
    public ResponseEntity<ViolationRecord> logViolation(@RequestBody ViolationRecord record) {
        System.out.println(">>> LOG VIOLATION RECEIVED: " + record.getViolationType());
        ViolationRecord savedRecord = proctoringService.logViolation(record);
        return ResponseEntity.ok(savedRecord);
    }

    // route for uploading video evidence files
    @PostMapping("/upload-evidence")
    public ResponseEntity<?> uploadEvidence(
            @RequestParam(value = "webcamVideo", required = false) MultipartFile webcamVideo,
            @RequestParam(value = "screenVideo", required = false) MultipartFile screenVideo,
            @RequestParam("violationType") String violationType,
            @RequestParam(value = "candidateId", required = false, defaultValue = "TEMP_CANDIDATE") String candidateId,
            @RequestParam(value = "examId", required = false, defaultValue = "TEMP_EXAM") String examId,
            @RequestParam(value = "timestamp", required = false) String timestamp) {

        System.out.println(">>> UPLOAD EVIDENCE RECEIVED: " + violationType);

        try {
            ViolationRecord savedRecord = proctoringService.saveEvidence(
                    webcamVideo, screenVideo, violationType, candidateId, examId, timestamp
            );
            return ResponseEntity.ok(savedRecord);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error saving video evidence: " + e.getMessage());
        }
    }
}