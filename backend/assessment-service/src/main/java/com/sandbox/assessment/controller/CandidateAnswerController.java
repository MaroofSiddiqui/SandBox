package com.sandbox.assessment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.sandbox.assessment.dto.CodingAnswerRequest;
import com.sandbox.assessment.entity.CandidateAnswer;
import com.sandbox.assessment.service.CandidateAnswerService;
import com.sandbox.assessment.dto.McqAnswerRequest;

import io.jsonwebtoken.Claims;

@RestController
@RequestMapping("/candidate-answer")
public class CandidateAnswerController {

	private final CandidateAnswerService candidateAnswerService;

	public CandidateAnswerController(CandidateAnswerService candidateAnswerService) {

		this.candidateAnswerService = candidateAnswerService;
	}

	@PostMapping("/coding")
	public ResponseEntity<?> saveCodingAnswer(@RequestBody CodingAnswerRequest request, Authentication authentication) {

		Claims claims = (Claims) authentication.getDetails();

		Number userId = claims.get("userId", Number.class);

		if (userId == null) {
			throw new RuntimeException("User ID not found in authentication token");
		}

		Long candidateId = userId.longValue();

		CandidateAnswer answer = candidateAnswerService.saveCodingEvaluation(request.getSubmissionId(),
				request.getQuestionId(), request.getCodingEvaluationId(), candidateId);

		return ResponseEntity.ok(new CodingAnswerResponse(answer.getId(), answer.getSubmission().getId(),
				answer.getQuestion().getId(), answer.getCodingEvaluationId()));
	}

	@PostMapping("/mcq")
	public ResponseEntity<?> saveMcqAnswer(@RequestBody McqAnswerRequest request, Authentication authentication) {

		Claims claims = (Claims) authentication.getDetails();

		Number userId = claims.get("userId", Number.class);

		if (userId == null) {
			throw new RuntimeException("User ID not found in authentication token");
		}

		Long candidateId = userId.longValue();

		CandidateAnswer answer = candidateAnswerService.saveMcqAnswer(request.getSubmissionId(),
				request.getQuestionId(), request.getSelectedOptionId(), candidateId);

		return ResponseEntity
				.ok(new McqAnswerResponse(answer.getId(), answer.getSubmission().getId(), answer.getQuestion().getId(),
						answer.getSelectedOption().getId(), answer.getCorrect(), answer.getAwardedMarks()));
	}

	public record CodingAnswerResponse(Long answerId, Long submissionId, Long questionId, String codingEvaluationId) {
	}

	public record McqAnswerResponse(Long answerId, Long submissionId, Long questionId, Long selectedOptionId,
			Boolean correct, Double awardedMarks) {
	}
}