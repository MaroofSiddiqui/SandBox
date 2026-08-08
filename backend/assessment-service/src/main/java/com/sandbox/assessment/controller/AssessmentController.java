package com.sandbox.assessment.controller;

import com.sandbox.assessment.dto.AssessmentDto;
import com.sandbox.assessment.dto.AssignCandidatesRequest;
import com.sandbox.assessment.entity.ExamAssignment;
import com.sandbox.assessment.repository.ExamAssignmentRepository;
import com.sandbox.assessment.service.AssessmentService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assessment")
public class AssessmentController {

    private final AssessmentService assessmentService;
    private final ExamAssignmentRepository examAssignmentRepository;

    public AssessmentController(
            AssessmentService assessmentService,
            ExamAssignmentRepository examAssignmentRepository) {

        this.assessmentService = assessmentService;
        this.examAssignmentRepository = examAssignmentRepository;
    }

    // ============================================================
    // CREATE ASSESSMENT
    // ============================================================

    @PostMapping("/create")
    public ResponseEntity<AssessmentDto> createAssessment(
            @RequestBody AssessmentDto dto
    ) {

        return new ResponseEntity<>(
                assessmentService.createAssessment(dto),
                HttpStatus.CREATED
        );
    }

    // ============================================================
    // PUBLISH ASSESSMENT
    // ============================================================

    @PutMapping("/{id}/publish")
    public ResponseEntity<AssessmentDto> publishAssessment(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                assessmentService.publishAssessment(id)
        );
    }

    // ============================================================
    // GET ALL ASSESSMENTS
    // ============================================================

    @GetMapping("/all")
    public ResponseEntity<List<AssessmentDto>> getAllAssessments() {

        return ResponseEntity.ok(
                assessmentService.getAllAssessments()
        );
    }

    // ============================================================
    // GET ASSESSMENT BY ID
    // ============================================================

    @GetMapping("/{id}")
    public ResponseEntity<AssessmentDto> getAssessmentById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                assessmentService.getAssessmentById(id)
        );
    }

    // ============================================================
    // ASSIGN CANDIDATES
    // ============================================================

    @PostMapping("/{id}/assign")
    public ResponseEntity<String> assignCandidatesToAssessment(
            @PathVariable Long id,
            @RequestBody AssignCandidatesRequest request
    ) {

        assessmentService.assignCandidates(
                id,
                request.getCandidateIds()
        );

        return ResponseEntity.ok(
                "Candidates assigned successfully!"
        );
    }

    // ============================================================
    // GET ASSIGNMENTS FOR A CANDIDATE
    // ============================================================

    @GetMapping("/assignments/candidate/{candidateId}")
    public ResponseEntity<List<ExamAssignment>> getAssignmentsForCandidate(
            @PathVariable Long candidateId
    ) {

        return ResponseEntity.ok(
                examAssignmentRepository.findByCandidateId(candidateId)
        );
    }
}