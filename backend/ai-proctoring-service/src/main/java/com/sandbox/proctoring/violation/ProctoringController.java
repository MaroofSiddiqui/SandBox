package com.sandbox.proctoring.violation;

import com.sandbox.proctoring.violation.dto.ViolationLogRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/proctoring")
@CrossOrigin(origins = "*")
public class ProctoringController {

    @Autowired
    private ProctoringService proctoringService;

    // Route for fast text logs using validated DTO
    @PostMapping("/log-violation")
    public ResponseEntity<ViolationRecord> logViolation(@Valid @RequestBody ViolationLogRequest request) {
        System.out.println(">>> LOG VIOLATION RECEIVED: " + request.getViolationType());
        ViolationRecord savedRecord = proctoringService.logViolation(request);
        return ResponseEntity.ok(savedRecord);
    }

    // Route for uploading video evidence files with guardrail check
    @PostMapping("/upload-evidence")
    public ResponseEntity<?> uploadEvidence(
            @RequestParam(value = "webcamVideo", required = false) MultipartFile webcamVideo,
            @RequestParam(value = "screenVideo", required = false) MultipartFile screenVideo,
            @RequestParam("violationType") String violationType,
            @RequestParam(value = "candidateId", required = false, defaultValue = "TEMP_CANDIDATE") String candidateId,
            @RequestParam(value = "examId", required = false, defaultValue = "TEMP_EXAM") String examId,
            @RequestParam(value = "timestamp", required = false) String timestamp) {

        System.out.println(">>> UPLOAD EVIDENCE RECEIVED: " + violationType);

        if ((webcamVideo == null || webcamVideo.isEmpty()) && (screenVideo == null || screenVideo.isEmpty())) {
            return ResponseEntity.badRequest().body("Error: At least one video evidence file (webcam or screen) must be provided.");
        }

        try {
            ViolationRecord savedRecord = proctoringService.saveEvidence(
                    webcamVideo, screenVideo, violationType, candidateId, examId, timestamp
            );
            return ResponseEntity.ok(savedRecord);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error saving video evidence: " + e.getMessage());
        }
    }

    // Local exception handler for validation errors ONLY on this controller
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}