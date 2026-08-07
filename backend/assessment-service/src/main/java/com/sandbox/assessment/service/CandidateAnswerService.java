package com.sandbox.assessment.service;

import org.springframework.stereotype.Service;

import com.sandbox.assessment.entity.AssessmentSubmission;
import com.sandbox.assessment.entity.CandidateAnswer;
import com.sandbox.assessment.entity.Question;
import com.sandbox.assessment.repository.AssessmentSubmissionRepository;
import com.sandbox.assessment.repository.CandidateAnswerRepository;
import com.sandbox.assessment.repository.QuestionRepository;
import com.sandbox.assessment.repository.AssessmentQuestionRepository;

@Service
public class CandidateAnswerService {

	private final CandidateAnswerRepository candidateAnswerRepository;
	private final AssessmentSubmissionRepository submissionRepository;
	private final QuestionRepository questionRepository;
	private final AssessmentQuestionRepository assessmentQuestionRepository;

	public CandidateAnswerService(CandidateAnswerRepository candidateAnswerRepository,
			AssessmentSubmissionRepository submissionRepository, QuestionRepository questionRepository,
			AssessmentQuestionRepository assessmentQuestionRepository) {

		this.candidateAnswerRepository = candidateAnswerRepository;
		this.submissionRepository = submissionRepository;
		this.questionRepository = questionRepository;
		this.assessmentQuestionRepository = assessmentQuestionRepository;
	}

	public CandidateAnswer saveCodingEvaluation(Long submissionId, Long questionId, String codingEvaluationId,
			Long candidateId) {

		AssessmentSubmission submission = submissionRepository.findByIdAndCandidateId(submissionId, candidateId)
				.orElseThrow(() -> new RuntimeException("Assessment submission not found"));

		if (submission.getStatus() != AssessmentSubmission.SubmissionStatus.IN_PROGRESS) {

			throw new RuntimeException("Assessment submission is not in progress");
		}

		Long assessmentId = submission.getAssessment().getId();

		boolean questionBelongsToAssessment = assessmentQuestionRepository
				.existsByAssessmentIdAndQuestionId(assessmentId, questionId);

		if (!questionBelongsToAssessment) {
			throw new RuntimeException("Question does not belong to this assessment");
		}

		Question question = questionRepository.findById(questionId)
				.orElseThrow(() -> new RuntimeException("Question not found with ID: " + questionId));

		if (question.getQuestionType() != Question.QuestionType.CODING) {

			throw new RuntimeException("Question is not a coding question");
		}

		CandidateAnswer answer = candidateAnswerRepository.findBySubmissionIdAndQuestionId(submissionId, questionId)
				.orElseGet(() -> CandidateAnswer.builder().submission(submission).question(question).build());

		answer.setCodingEvaluationId(codingEvaluationId);

		return candidateAnswerRepository.save(answer);
	}
}