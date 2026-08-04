package com.sandbox.proctoring.violation;

import com.sandbox.proctoring.violation.dto.ViolationLogRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class ProctoringService {

    // individual raw violation records repository
    @Autowired
    private ViolationRecordRepository repository;

    // cumulative candidate counters summary repository
    @Autowired
    private StudentViolationSummaryRepository summaryRepository;

    // local folder path to store uploaded videos
    private final String UPLOAD_DIR = "uploads/evidence/";

    // Formatter for readable IST timestamp
    private static final DateTimeFormatter IST_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss 'IST'");

    // helper method to get current time in IST
    private String getCurrentISTTimestamp() {
        return ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).format(IST_FORMATTER);
    }

    // saves small json logs received via DTO and updates candidate violation counter
    public ViolationRecord logViolation(ViolationLogRequest request) {
        ViolationRecord record = new ViolationRecord();
        record.setCandidateId(request.getCandidateId());
        record.setExamId(request.getExamId());
        record.setViolationType(request.getViolationType());
        
        // convert Long timestamp from DTO to String for ViolationRecord
        if (request.getTimestamp() != null) {
            record.setTimestamp(String.valueOf(request.getTimestamp()));
        }
        
        record.setDetails(request.getDetails());
        
        // set readable IST timestamp
        record.setCreatedAtIST(getCurrentISTTimestamp());

        ViolationRecord savedRecord = repository.save(record);

        // update or create cumulative summary counter for student
        updateStudentSummary(request.getCandidateId(), request.getExamId(), request.getViolationType());

        return savedRecord;
    }

    // saves video files to disk, links in mongo, and updates candidate violation counter
    public ViolationRecord saveEvidence(MultipartFile webcamVideo,
                                         MultipartFile screenVideo,
                                         String violationType,
                                         String candidateId,
                                         String examId,
                                         String timestamp) throws IOException {

        ViolationRecord record = new ViolationRecord(candidateId, examId, violationType, timestamp);

        // set readable IST timestamp
        record.setCreatedAtIST(getCurrentISTTimestamp());

        // create uploads folder if missing
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // process webcam file
        if (webcamVideo != null && !webcamVideo.isEmpty()) {
            String webcamFileName = UUID.randomUUID() + "_webcam.webm";
            Path webcamPath = Paths.get(UPLOAD_DIR + webcamFileName);
            
            Files.write(webcamPath, webcamVideo.getBytes());
            record.setWebcamVideoUrl("/uploads/evidence/" + webcamFileName);
        }

        // process screen recording file
        if (screenVideo != null && !screenVideo.isEmpty()) {
            String screenFileName = UUID.randomUUID() + "_screen.webm";
            Path screenPath = Paths.get(UPLOAD_DIR + screenFileName);
            
            Files.write(screenPath, screenVideo.getBytes());
            record.setScreenVideoUrl("/uploads/evidence/" + screenFileName);
        }

        ViolationRecord savedRecord = repository.save(record);

        // update or create cumulative summary counter for student
        updateStudentSummary(candidateId, examId, violationType);

        return savedRecord;
    }

    // helper method to update student violation summary in mongo
    private void updateStudentSummary(String candidateId, String examId, String violationType) {
        if (candidateId == null || examId == null) return;

        StudentViolationSummary summary = summaryRepository
                .findByCandidateIdAndExamId(candidateId, examId)
                .orElse(new StudentViolationSummary(candidateId, examId));

        summary.incrementViolation(violationType);
        summaryRepository.save(summary);
    }
}