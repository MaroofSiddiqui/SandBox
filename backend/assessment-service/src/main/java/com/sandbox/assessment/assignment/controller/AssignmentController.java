package com.sandbox.assessment.assignment.controller;

import com.sandbox.assessment.assignment.dto.AssignmentRequestDto;
import com.sandbox.assessment.assignment.dto.AssignmentResponseDto;
import com.sandbox.assessment.assignment.enums.AssignmentStatus;
import com.sandbox.assessment.assignment.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ==========================================================
 * Member 4
 *
 * Assignment Controller
 *
 * Handles Assignment APIs.
 * Independent Module.
 * ==========================================================
 */

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AssignmentController {

    private final AssignmentService assignmentService;

    /**
     * Assign Assessment
     */
    @PostMapping
    public ResponseEntity<AssignmentResponseDto> assignAssessment(
            @Valid @RequestBody AssignmentRequestDto requestDto) {

        AssignmentResponseDto response =
                assignmentService.assignAssessment(requestDto);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get All Assignments
     */
    @GetMapping
    public ResponseEntity<List<AssignmentResponseDto>> getAllAssignments() {

        return ResponseEntity.ok(
                assignmentService.getAllAssignments());
    }

    /**
     * Get Assignment By ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<AssignmentResponseDto> getAssignmentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                assignmentService.getAssignmentById(id));
    }

    /**
     * Start Assessment
     */
    @PutMapping("/{id}/start")
    public ResponseEntity<AssignmentResponseDto> startAssessment(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                assignmentService.startAssessment(id));
    }

    /**
     * Submit Assessment
     */
    @PutMapping("/{id}/submit")
    public ResponseEntity<AssignmentResponseDto> submitAssessment(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                assignmentService.submitAssessment(id));
    }

    /**
     * Delete Assignment
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAssignment(
            @PathVariable Long id) {

        assignmentService.deleteAssignment(id);

        return ResponseEntity.ok("Assignment deleted successfully.");
    }

    /**
     * Get assignments by Candidate
     */
    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<AssignmentResponseDto>>
    getAssignmentsByCandidate(
            @PathVariable Long candidateId) {

        return ResponseEntity.ok(
                assignmentService.getAssignmentsByCandidate(candidateId));
    }

    /**
     * Get assignments by Assessment
     */
    @GetMapping("/assessment/{assessmentId}")
    public ResponseEntity<List<AssignmentResponseDto>>
    getAssignmentsByAssessment(
            @PathVariable Long assessmentId) {

        return ResponseEntity.ok(
                assignmentService.getAssignmentsByAssessment(assessmentId));
    }

    /**
     * Pagination & Sorting API
     */
    @GetMapping("/page")
    public ResponseEntity<Page<AssignmentResponseDto>> getAssignments(

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "5") int size,

            @RequestParam(defaultValue = "id") String sortBy) {

        return ResponseEntity.ok(
                assignmentService.getAssignments(
                        page,
                        size,
                        sortBy));
    }
    
    /**
     * Search assignments by status.
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<AssignmentResponseDto>>
    getAssignmentsByStatus(
            @PathVariable AssignmentStatus status) {

        return ResponseEntity.ok(
                assignmentService.getAssignmentsByStatus(status)
        );
    }
    
    /**
     * Search assignments by candidate and status.
     */
    @GetMapping("/candidate/{candidateId}/status/{status}")
    public ResponseEntity<List<AssignmentResponseDto>>
    getAssignmentsByCandidateAndStatus(

            @PathVariable Long candidateId,

            @PathVariable AssignmentStatus status) {

        return ResponseEntity.ok(

                assignmentService
                        .getAssignmentsByCandidateAndStatus(
                                candidateId,
                                status));

    }

}