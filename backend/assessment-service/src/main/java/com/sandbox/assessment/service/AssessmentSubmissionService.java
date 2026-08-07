package com.sandbox.assessment.service;

import org.springframework.stereotype.Service;

import com.sandbox.assessment.dto.StartAssessmentResponse;
import com.sandbox.assessment.entity.Assessment;
import com.sandbox.assessment.entity.AssessmentSubmission;
import com.sandbox.assessment.repository.AssessmentRepository;
import com.sandbox.assessment.repository.AssessmentSubmissionRepository;
import java.time.LocalDateTime;
import com.sandbox.assessment.dto.FinishAssessmentResponse;
import java.util.List;

import com.sandbox.assessment.client.AiEvaluationClient;
import com.sandbox.assessment.entity.CandidateAnswer;
import com.sandbox.assessment.repository.CandidateAnswerRepository;

@Service
public class AssessmentSubmissionService {

	private final AssessmentRepository assessmentRepository;
	private final AssessmentSubmissionRepository submissionRepository;
	private final CandidateAnswerRepository candidateAnswerRepository;
	private final AiEvaluationClient aiEvaluationClient;

	public AssessmentSubmissionService(AssessmentRepository assessmentRepository,
			AssessmentSubmissionRepository submissionRepository, CandidateAnswerRepository candidateAnswerRepository,
			AiEvaluationClient aiEvaluationClient) {

		this.assessmentRepository = assessmentRepository;
		this.submissionRepository = submissionRepository;
		this.candidateAnswerRepository = candidateAnswerRepository;
		this.aiEvaluationClient = aiEvaluationClient;
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

	public FinishAssessmentResponse finishAssessment(Long submissionId, Long candidateId, String authorizationHeader) {

		AssessmentSubmission submission = submissionRepository.findByIdAndCandidateId(submissionId, candidateId)
				.orElseThrow(() -> new RuntimeException("Assessment submission not found"));

		if (submission.getStatus() != AssessmentSubmission.SubmissionStatus.IN_PROGRESS) {

			throw new RuntimeException("Assessment has already been submitted.");
		}

		/*
		 * Get all answers saved by this candidate for this particular submission.
		 */
		List<CandidateAnswer> answers = candidateAnswerRepository.findBySubmissionId(submissionId);

		double totalScore = 0.0;

		for (CandidateAnswer answer : answers) {

			/*
			 * ============================ MCQ ============================
			 *
			 * MCQ marks were already calculated when the candidate selected an option.
			 */
			if (answer.getSelectedOption() != null) {

				if (answer.getAwardedMarks() != null) {
					totalScore += answer.getAwardedMarks();
				}

				continue;
			}

			/*
			 * ============================ CODING ============================
			 *
			 * 8083 gives us a percentage score.
			 *
			 * Example: Question marks = 10 Evaluation score = 80%
			 *
			 * Awarded marks = 8
			 */
			if (answer.getCodingEvaluationId() != null && !answer.getCodingEvaluationId().isBlank()) {

				Double evaluationPercentage = aiEvaluationClient.getEvaluationScore(answer.getCodingEvaluationId(),
						authorizationHeader);

				double questionMarks = answer.getQuestion().getMarks();

				double awardedMarks = (evaluationPercentage / 100.0) * questionMarks;

				/*
				 * Save the converted coding marks in MySQL too.
				 */
				answer.setAwardedMarks(awardedMarks);

				candidateAnswerRepository.save(answer);

				totalScore += awardedMarks;
			}
		}

		/*
		 * Assessment is now fully evaluated.
		 */
		submission.setScore(totalScore);

		submission.setSubmittedAt(LocalDateTime.now());

		submission.setStatus(AssessmentSubmission.SubmissionStatus.EVALUATED);

		AssessmentSubmission saved = submissionRepository.save(submission);

		return new FinishAssessmentResponse(saved.getId(), saved.getAssessment().getId(), saved.getCandidateId(),
				saved.getStatus().name(), saved.getScore(), saved.getSubmittedAt());
	}

	public boolean existsActiveSubmission(Long assessmentId, Long candidateId) {

		return submissionRepository.existsByAssessmentIdAndCandidateIdAndStatus(assessmentId, candidateId,
				AssessmentSubmission.SubmissionStatus.IN_PROGRESS);
	}
}