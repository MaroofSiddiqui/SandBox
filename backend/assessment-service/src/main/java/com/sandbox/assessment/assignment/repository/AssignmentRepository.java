package com.sandbox.assessment.assignment.repository;

import com.sandbox.assessment.assignment.entity.Assignment;
import com.sandbox.assessment.assignment.enums.AssignmentStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Assignment module.
 *
 * Member 4
 */
@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    /**
     * Returns all assignments of a candidate.
     */
    List<Assignment> findByCandidateId(Long candidateId);

    /**
     * Returns all assignments of an assessment.
     */
    List<Assignment> findByAssessmentId(Long assessmentId);

    /**
     * Checks duplicate assignment.
     */
    boolean existsByAssessmentIdAndCandidateId(Long assessmentId,
                                               Long candidateId);

    /**
     * Returns assignment using assessment and candidate.
     */
    Optional<Assignment> findByAssessmentIdAndCandidateId(Long assessmentId,
                                                          Long candidateId);
    
    long countByStatus(AssignmentStatus status);

    long count();
    
    /**
     * Returns assignments by status.
     */
    List<Assignment> findByStatus(AssignmentStatus status);

    /**
     * Returns assignments by candidate and status.
     */
    List<Assignment> findByCandidateIdAndStatus(
            Long candidateId,
            AssignmentStatus status);
    
    
    
    
    
}