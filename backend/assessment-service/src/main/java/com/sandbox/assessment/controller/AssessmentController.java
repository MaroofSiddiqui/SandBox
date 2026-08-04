package com.sandbox.assessment.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sandbox.assessment.dto.common.ApiResponse;
import com.sandbox.assessment.dto.request.AssessmentRequest;
import com.sandbox.assessment.dto.response.AssessmentResponse;
import com.sandbox.assessment.service.AssessmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/assessments")
public class AssessmentController {

    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    // ==========================================
    // CREATE
    // ==========================================
    @PostMapping
    public ResponseEntity<ApiResponse<AssessmentResponse>> createAssessment(
            @Valid @RequestBody AssessmentRequest request) {

        AssessmentResponse response =
                assessmentService.createAssessment(request);

        ApiResponse<AssessmentResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        "Assessment created successfully",
                        LocalDateTime.now(),
                        response
                );

        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    // ==========================================
    // GET ALL
    // ==========================================
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<AssessmentResponse>>> getAllAssessments() {

        List<AssessmentResponse> response =
                assessmentService.getAllAssessments();

        ApiResponse<List<AssessmentResponse>> apiResponse =
                new ApiResponse<>(
                        true,
                        "Assessments fetched successfully",
                        LocalDateTime.now(),
                        response
                );

        return ResponseEntity.ok(apiResponse);
    }

    // ==========================================
    // GET BY ID
    // ==========================================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AssessmentResponse>> getAssessmentById(
            @PathVariable Long id) {

        AssessmentResponse response =
                assessmentService.getAssessmentById(id);

        ApiResponse<AssessmentResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        "Assessment fetched successfully",
                        LocalDateTime.now(),
                        response
                );

        return ResponseEntity.ok(apiResponse);
    }

    // ==========================================
    // UPDATE
    // ==========================================
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AssessmentResponse>> updateAssessment(
            @PathVariable Long id,
            @Valid @RequestBody AssessmentRequest request) {

        AssessmentResponse response =
                assessmentService.updateAssessment(id, request);

        ApiResponse<AssessmentResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        "Assessment updated successfully",
                        LocalDateTime.now(),
                        response
                );

        return ResponseEntity.ok(apiResponse);
    }

    // ==========================================
    // DELETE
    // ==========================================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAssessment(
            @PathVariable Long id) {

        assessmentService.deleteAssessment(id);

        ApiResponse<String> apiResponse =
                new ApiResponse<>(
                        true,
                        "Assessment deleted successfully",
                        LocalDateTime.now(),
                        "Deleted Successfully"
                );

        return ResponseEntity.ok(apiResponse);
    }

    // ==========================================
    // SEARCH
    // ==========================================
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<AssessmentResponse>>> searchAssessments(
            @RequestParam String keyword) {

        List<AssessmentResponse> response =
                assessmentService.searchAssessments(keyword);

        ApiResponse<List<AssessmentResponse>> apiResponse =
                new ApiResponse<>(
                        true,
                        "Search completed successfully",
                        LocalDateTime.now(),
                        response
                );

        return ResponseEntity.ok(apiResponse);
    }

    // ==========================================
    // PAGINATION
    // ==========================================
    @GetMapping
    public ResponseEntity<ApiResponse<Page<AssessmentResponse>>> getAssessments(
            Pageable pageable) {

        Page<AssessmentResponse> response =
                assessmentService.getAllAssessments(pageable);

        ApiResponse<Page<AssessmentResponse>> apiResponse =
                new ApiResponse<>(
                        true,
                        "Assessments fetched successfully",
                        LocalDateTime.now(),
                        response
                );

        return ResponseEntity.ok(apiResponse);
    }
}