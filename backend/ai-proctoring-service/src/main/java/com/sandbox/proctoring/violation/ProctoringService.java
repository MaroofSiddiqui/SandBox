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
import java.util.UUID;

@Service
public class ProctoringService {

    // database repo link
    @Autowired
    private ViolationRecordRepository repository;

    // local folder path to store uploaded videos
    private final String UPLOAD_DIR = "uploads/evidence/";

    // saves small json logs received via DTO
    public ViolationRecord logViolation(ViolationLogRequest request) {
        ViolationRecord record = new ViolationRecord();
        record.setCandidateId(request.getCandidateId());
        record.setExamId(request.getExamId());
        record.setViolationType(request.getViolationType());
        
        // Convert Long timestamp from DTO to String for ViolationRecord
        if (request.getTimestamp() != null) {
            record.setTimestamp(String.valueOf(request.getTimestamp()));
        }
        
        record.setDetails(request.getDetails());

        return repository.save(record);
    }

    // saves video files to disk and links in mongo
    public ViolationRecord saveEvidence(MultipartFile webcamVideo,
                                         MultipartFile screenVideo,
                                         String violationType,
                                         String candidateId,
                                         String examId,
                                         String timestamp) throws IOException {

        ViolationRecord record = new ViolationRecord(candidateId, examId, violationType, timestamp);

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

        // save complete record in mongo
        return repository.save(record);
    }
}