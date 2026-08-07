package com.sandbox.assessment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.sandbox.assessment.dto.CodingAnswerRequest;
import com.sandbox.assessment.dto.McqAnswerRequest;
import com.sandbox.assessment.entity.CandidateAnswer;
import com.sandbox.assessment.service.CandidateAnswerService;

import io.jsonwebtoken.Claims;

import java.util.Map;

@RestController
@RequestMapping("/candidate-answer")
public class CandidateAnswerController {

	private final CandidateAnswerService candidateAnswerService;

	public CandidateAnswerController(CandidateAnswerService candidateAnswerService) {

		this.candidateAnswerService = candidateAnswerService;
	}

	@PostMapping("/coding")
	public ResponseEntity<?> saveCodingAnswer(@RequestBody CodingAnswerRequest request, Authentication authentication) {

		try {

			if (authentication == null || authentication.getDetails() == null) {

				return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
			}

			Claims claims = (Claims) authentication.getDetails();

			Number userId = claims.get("userId", Number.class);

			if (userId == null) {

				return ResponseEntity.status(401).body(Map.of("error", "User ID missing in JWT"));
			}

			Long candidateId = userId.longValue();

			CandidateAnswer answer = candidateAnswerService.saveCodingEvaluation(request.getSubmissionId(),
					request.getQuestionId(), request.getCodingEvaluationId(), candidateId);

			return ResponseEntity.ok(new CodingAnswerResponse(answer.getId(), answer.getSubmission().getId(),
					answer.getQuestion().getId(), answer.getCodingEvaluationId()));

		} catch (Exception e) {

			e.printStackTrace();

			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		}
	}

	@PostMapping("/mcq")
	public ResponseEntity<?> saveMcqAnswer(@RequestBody McqAnswerRequest request, Authentication authentication) {

		try {

			if (authentication == null || authentication.getDetails() == null) {

				return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
			}

			Claims claims = (Claims) authentication.getDetails();

			Number userId = claims.get("userId", Number.class);

			if (userId == null) {

				return ResponseEntity.status(401).body(Map.of("error", "User ID missing in JWT"));
			}

			Long candidateId = userId.longValue();

			CandidateAnswer answer = candidateAnswerService.saveMcqAnswer(request.getSubmissionId(),
					request.getQuestionId(), request.getSelectedOptionId(), candidateId);

			return ResponseEntity.ok(
					new McqAnswerResponse(answer.getId(), answer.getSubmission().getId(), answer.getQuestion().getId(),
							answer.getSelectedOption().getId(), answer.getCorrect(), answer.getAwardedMarks()));

		} catch (Exception e) {

			e.printStackTrace();

			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		}
	}

	public record CodingAnswerResponse(Long answerId, Long submissionId, Long questionId, String codingEvaluationId) {
	}

	public record McqAnswerResponse(Long answerId, Long submissionId, Long questionId, Long selectedOptionId,
			Boolean correct, Double awardedMarks) {
	}

}