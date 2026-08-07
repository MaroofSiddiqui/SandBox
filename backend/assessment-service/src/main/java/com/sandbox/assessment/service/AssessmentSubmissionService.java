package com.sandbox.assessment.service;

import org.springframework.stereotype.Service;

import com.sandbox.assessment.dto.StartAssessmentResponse;
import com.sandbox.assessment.entity.Assessment;
import com.sandbox.assessment.entity.AssessmentSubmission;
import com.sandbox.assessment.repository.AssessmentRepository;
import com.sandbox.assessment.repository.AssessmentSubmissionRepository;
import java.time.LocalDateTime;
import com.sandbox.assessment.dto.FinishAssessmentResponse;

@Service
public class AssessmentSubmissionService {

	private final AssessmentRepository assessmentRepository;
	private final AssessmentSubmissionRepository submissionRepository;

	public AssessmentSubmissionService(AssessmentRepository assessmentRepository,
			AssessmentSubmissionRepository submissionRepository) {

		this.assessmentRepository = assessmentRepository;
		this.submissionRepository = submissionRepository;
	}

	public StartAssessmentResponse startAssessment(Long assessmentId, Long candidateId) {

		Assessment assessment = assessmentRepository.findById(assessmentId)
				.orElseThrow(() -> new RuntimeException("Assessment not found with ID: " + assessmentId));

		/*
		 * Candidate should only be able to start an assessment that HR has published.
		 */
		if (!Boolean.TRUE.equals(assessment.getIsPublished())) {
			throw new RuntimeException("Assessment is not published.");
		}

		AssessmentSubmission submission = AssessmentSubmission.builder().candidateId(candidateId).assessment(assessment)
				.status(AssessmentSubmission.SubmissionStatus.IN_PROGRESS).build();

		AssessmentSubmission saved = submissionRepository.save(submission);

		return new StartAssessmentResponse(saved.getId(), assessment.getId(), saved.getCandidateId(),
				saved.getStatus().name(), saved.getStartedAt());
	}

	public FinishAssessmentResponse finishAssessment(Long submissionId, Long candidateId) {

		AssessmentSubmission submission = submissionRepository.findByIdAndCandidateId(submissionId, candidateId)
				.orElseThrow(() -> new RuntimeException("Assessment submission not found"));

		if (submission.getStatus() != AssessmentSubmission.SubmissionStatus.IN_PROGRESS) {

			throw new RuntimeException("Assessment has already been submitted.");
		}

		submission.setSubmittedAt(LocalDateTime.now());

		submission.setStatus(AssessmentSubmission.SubmissionStatus.SUBMITTED);

		AssessmentSubmission saved = submissionRepository.save(submission);

		return new FinishAssessmentResponse(saved.getId(), saved.getAssessment().getId(), saved.getCandidateId(),
				saved.getStatus().name(), saved.getScore(), saved.getSubmittedAt());
	}
}