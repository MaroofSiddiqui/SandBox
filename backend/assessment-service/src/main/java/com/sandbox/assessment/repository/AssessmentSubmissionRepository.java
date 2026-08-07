package com.sandbox.assessment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sandbox.assessment.entity.AssessmentSubmission;
import com.sandbox.assessment.entity.AssessmentSubmission.SubmissionStatus;

@Repository
public interface AssessmentSubmissionRepository extends JpaRepository<AssessmentSubmission, Long> {

	List<AssessmentSubmission> findByCandidateId(Long candidateId);

	Optional<AssessmentSubmission> findByIdAndCandidateId(Long id, Long candidateId);

	List<AssessmentSubmission> findByCandidateIdAndStatus(Long candidateId, SubmissionStatus status);

	boolean existsByAssessmentIdAndCandidateIdAndStatus(Long assessmentId, Long candidateId,
			AssessmentSubmission.SubmissionStatus status);
}