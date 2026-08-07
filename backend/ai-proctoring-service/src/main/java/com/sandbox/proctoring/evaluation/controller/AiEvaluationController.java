package com.sandbox.proctoring.evaluation.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sandbox.proctoring.evaluation.model.AiEvaluationResult;
import com.sandbox.proctoring.evaluation.model.CodeSubmission;
import com.sandbox.proctoring.evaluation.service.AiEvaluationService;
import com.sandbox.proctoring.evaluation.service.Judge0Service;
import com.sandbox.proctoring.evaluation.service.GeminiService;
import com.sandbox.proctoring.evaluation.dto.SubmissionResponse;
import org.springframework.security.core.Authentication;
import com.sandbox.proctoring.evaluation.dto.EvaluationScoreResponse;
import io.jsonwebtoken.Claims;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/evaluations")
@CrossOrigin(origins = "*")
public class AiEvaluationController {

	@Autowired(required = false)
	private AiEvaluationService evaluationService;
	@Autowired
	private Judge0Service judge0Service;
	@Autowired
	private GeminiService geminiService;

	@GetMapping
	public ResponseEntity<List<AiEvaluationResult>> getAllEvaluations() {
		return ResponseEntity.ok(evaluationService.getAllEvaluations());
	}

	@GetMapping("/{id}")
	public ResponseEntity<AiEvaluationResult> getEvaluationById(@PathVariable String id) {
		Optional<AiEvaluationResult> evaluation = evaluationService.getEvaluationById(id);
		return evaluation.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping("/student/{studentId}")
	public ResponseEntity<List<AiEvaluationResult>> getEvaluationsByStudentId(@PathVariable String studentId) {
		return ResponseEntity.ok(evaluationService.getEvaluationsByStudentId(studentId));
	}

	// ---- RUN: ab questionId leta hai, visible test cases ke against per-case
	// result deta hai ----
	@PostMapping("/run")
	public ResponseEntity<?> runCode(@RequestBody CodeSubmission request,
			@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

		try {

			List<AiEvaluationService.TestCaseRunResult> results = evaluationService.runVisibleTestCases(
					request.getSourceCode(), request.getLanguageId(), request.getQuestionId(), authorizationHeader);

			return ResponseEntity.ok(results);

		} catch (Exception e) {

			return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
		}
	}

	// ---- SUBMIT: sab test cases + AI evaluation, DB me save ----
	@PostMapping("/submit-and-save")
	public ResponseEntity<?> submitAndSaveCode(@RequestBody CodeSubmission request,
			@RequestHeader("Authorization") String authorizationHeader, Authentication authentication) {

		try {

			/*
			 * JWT claims were stored in authentication details by JwtAuthenticationFilter.
			 */
			Claims claims = (Claims) authentication.getDetails();

			Number userId = claims.get("userId", Number.class);

			if (userId == null) {
				throw new RuntimeException("User ID not found in authentication token");
			}

			/*
			 * Candidate identity comes from the verified JWT, NOT from the request body.
			 */
			String studentId = String.valueOf(userId.longValue());

			AiEvaluationResult result = evaluationService.processAndSaveEvaluationForQuestion(request.getSourceCode(),
					request.getLanguageId(), request.getQuestionId(), studentId, request.getSubmissionId(),
					authorizationHeader);

			SubmissionResponse response = new SubmissionResponse(true, "Code submitted successfully",
					result.getHiddenTestsPassed(), result.getHiddenTotalTests(), result.getId());

			return ResponseEntity.ok(response);

		} catch (Exception e) {

			return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping("/{id}/score")
	public ResponseEntity<?> getEvaluationScore(@PathVariable String id, Authentication authentication) {

		try {

			AiEvaluationResult evaluation = evaluationService.getEvaluationById(id)
					.orElseThrow(() -> new RuntimeException("Evaluation not found with ID: " + id));

			if (authentication == null || authentication.getDetails() == null) {

				return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
			}

			Claims claims = (Claims) authentication.getDetails();

			Number userId = claims.get("userId", Number.class);

			String role = claims.get("role", String.class);

			if (userId == null || role == null) {

				return ResponseEntity.status(401).body(Map.of("error", "Invalid authentication token"));
			}

			String authenticatedUserId = String.valueOf(userId.longValue());

			/*
			 * HR can view candidate evaluation scores.
			 */
			if ("HR".equals(role)) {

				return ResponseEntity.ok(new EvaluationScoreResponse(evaluation.getId(), evaluation.getStudentId(),
						evaluation.getSubmissionId(), evaluation.getScore()));
			}

			/*
			 * Candidate can view only their own score.
			 */
			if ("CANDIDATE".equals(role)) {

				if (!authenticatedUserId.equals(evaluation.getStudentId())) {

					return ResponseEntity.status(403)
							.body(Map.of("error", "You are not authorized to access this evaluation"));
				}

				return ResponseEntity.ok(new EvaluationScoreResponse(evaluation.getId(), evaluation.getStudentId(),
						evaluation.getSubmissionId(), evaluation.getScore()));
			}

			return ResponseEntity.status(403).body(Map.of("error", "Access denied"));

		} catch (Exception e) {

			return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
		}
	}

}
