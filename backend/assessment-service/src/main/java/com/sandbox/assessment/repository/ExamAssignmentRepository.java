package com.sandbox.assessment.repository;

import com.sandbox.assessment.entity.ExamAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExamAssignmentRepository extends JpaRepository<ExamAssignment, Long> {

    List<ExamAssignment> findByCandidateId(Long candidateId);

    List<ExamAssignment> findByAssessmentId(Long assessmentId);

    Optional<ExamAssignment> findByAssessmentIdAndCandidateId(
            Long assessmentId,
            Long candidateId
    );
}