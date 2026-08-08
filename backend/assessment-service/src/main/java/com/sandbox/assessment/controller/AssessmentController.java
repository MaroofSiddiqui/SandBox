package com.sandbox.assessment.controller;

import com.sandbox.assessment.dto.AssessmentDto;
import com.sandbox.assessment.service.AssessmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assessment")
public class AssessmentController {

    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @PostMapping("/create")
    public ResponseEntity<AssessmentDto> createAssessment(@RequestBody AssessmentDto dto) {
        return new ResponseEntity<>(assessmentService.createAssessment(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<AssessmentDto> publishAssessment(@PathVariable Long id) {
        return ResponseEntity.ok(assessmentService.publishAssessment(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<AssessmentDto>> getAllAssessments() {
        return ResponseEntity.ok(assessmentService.getAllAssessments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssessmentDto> getAssessmentById(@PathVariable Long id) {
        return ResponseEntity.ok(assessmentService.getAssessmentById(id));
    }
    
    
    @PostMapping("/{id}/assign")
    public ResponseEntity<String> assignCandidatesToAssessment(
            @PathVariable Long id, 
            @RequestBody com.sandbox.assessment.dto.AssignCandidatesRequest request) {
        
        // You will need to create a simple method in AssessmentService to save these to the database
        // assessmentService.assignCandidates(id, request.getCandidateIds());
        
        return ResponseEntity.ok("Candidates assigned successfully!");
    }
}