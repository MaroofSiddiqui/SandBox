package com.sandbox.assessment.assignment.service;

import com.sandbox.assessment.assignment.dto.AssignmentRequestDto;
import com.sandbox.assessment.assignment.dto.AssignmentResponseDto;
import com.sandbox.assessment.assignment.enums.AssignmentStatus;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * ==========================================================
 * Member 4
 * Assignment Service Interface
 * ==========================================================
 */
public interface AssignmentService {

    /**
     * Assign assessment to candidate.
     */
    AssignmentResponseDto assignAssessment(AssignmentRequestDto requestDto);

    /**
     * Get all assignments.
     */
    List<AssignmentResponseDto> getAllAssignments();

    /**
     * Get assignment by ID.
     */
    AssignmentResponseDto getAssignmentById(Long assignmentId);

    /**
     * Start assigned assessment.
     */
    AssignmentResponseDto startAssessment(Long assignmentId);

    /**
     * Submit assessment.
     */
    AssignmentResponseDto submitAssessment(Long assignmentId);

    /**
     * Delete assignment.
     */
    void deleteAssignment(Long assignmentId);
    
    /**
     * Returns all assignments of one candidate.
     */
    List<AssignmentResponseDto> getAssignmentsByCandidate(Long candidateId);

    /**
     * Returns all candidates assigned to one assessment.
     */
    List<AssignmentResponseDto> getAssignmentsByAssessment(Long assessmentId);
    
    Page<AssignmentResponseDto> getAssignments(
            int page,
            int size,
            String sortBy
    );
    
    /**
     * Get assignments by status.
     */
    List<AssignmentResponseDto> getAssignmentsByStatus(
            AssignmentStatus status);

    /**
     * Get assignments by candidate and status.
     */
    List<AssignmentResponseDto> getAssignmentsByCandidateAndStatus(
            Long candidateId,
            AssignmentStatus status);
}