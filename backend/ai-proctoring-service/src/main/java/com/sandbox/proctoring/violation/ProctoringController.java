package com.sandbox.proctoring.violation;

import com.sandbox.proctoring.violation.dto.ViolationLogRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import io.jsonwebtoken.Claims;
import com.sandbox.proctoring.client.AssessmentServiceClient;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/proctoring")
@CrossOrigin(origins = "*")
public class ProctoringController {

	@Autowired
	private ProctoringService proctoringService;

	@Autowired
	private AssessmentServiceClient assessmentServiceClient;

	// checks user agent to reject mobile phone exam access on backend
	@GetMapping("/validate-device")
	public ResponseEntity<?> validateDevice(HttpServletRequest request) {
		String userAgent = request.getHeader("User-Agent");

		if (userAgent != null && isMobileUserAgent(userAgent)) {
			Map<String, Object> response = new HashMap<>();
			response.put("allowed", false);
			response.put("message", "Exams can only be taken on desktop computers.");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
		}

		Map<String, Object> response = new HashMap<>();
		response.put("allowed", true);
		return ResponseEntity.ok(response);
	}

	// helper to check if header contains mobile keywords
	private boolean isMobileUserAgent(String userAgent) {
		String ua = userAgent.toLowerCase();
		return ua.contains("android") || ua.contains("iphone") || ua.contains("ipad") || ua.contains("ipod")
				|| ua.contains("blackberry") || ua.contains("mobile");
	}

	// route for fast text logs using validated DTO
	@PostMapping("/log-violation")
	public ResponseEntity<?> logViolation(@Valid @RequestBody ViolationLogRequest request,
			Authentication authentication, HttpServletRequest httpRequest) {

		if (authentication == null || authentication.getDetails() == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Authentication required"));
		}

		Claims claims = (Claims) authentication.getDetails();

		Number userId = claims.get("userId", Number.class);

		if (userId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("error", "User ID not found in authentication token"));
		}

		boolean validSubmission = assessmentServiceClient.validateCandidateSubmission(
				Long.parseLong(request.getExamId()), userId.longValue(), httpRequest.getHeader("Authorization"));

		if (!validSubmission) {

			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(Map.of("error", "Candidate is not assigned to this assessment"));
		}

		/*
		 * Candidate identity MUST come from verified JWT. Never trust candidateId
		 * supplied by the frontend.
		 */
		String authenticatedCandidateId = String.valueOf(userId.longValue());

		request.setCandidateId(authenticatedCandidateId);

		ViolationRecord savedRecord = proctoringService.logViolation(request);

		return ResponseEntity.ok(savedRecord);
	}

	// route for uploading video evidence files with guardrail check
	@PostMapping("/upload-evidence")
	public ResponseEntity<?> uploadEvidence(
			@RequestParam(value = "webcamVideo", required = false) MultipartFile webcamVideo,
			@RequestParam(value = "screenVideo", required = false) MultipartFile screenVideo,
			@RequestParam("violationType") String violationType,
			@RequestParam(value = "candidateId", required = false) String candidateId,
			@RequestParam(value = "examId", required = false, defaultValue = "TEMP_EXAM") String examId,
			@RequestParam(value = "timestamp", required = false) String timestamp,
			@RequestHeader("Authorization") String authorizationHeader, Authentication authentication) {

		/*
		 * Candidate identity must come from the verified JWT.
		 */
		if (authentication == null || authentication.getDetails() == null) {

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Authentication required"));
		}

		Claims claims = (Claims) authentication.getDetails();

		Number userId = claims.get("userId", Number.class);

		if (userId == null) {

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("error", "User ID not found in authentication token"));
		}

		String authenticatedCandidateId = String.valueOf(userId.longValue());

		boolean validSubmission = assessmentServiceClient.validateCandidateSubmission(Long.parseLong(examId),
				userId.longValue(), authorizationHeader);

		if (!validSubmission) {

			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(Map.of("error", "Candidate is not assigned to this assessment"));
		}

		if ((webcamVideo == null || webcamVideo.isEmpty()) && (screenVideo == null || screenVideo.isEmpty())) {

			return ResponseEntity.badRequest()
					.body("Error: At least one video evidence file " + "(webcam or screen) must be provided.");
		}

		try {

			ViolationRecord savedRecord = proctoringService.saveEvidence(webcamVideo, screenVideo, violationType,

					// IMPORTANT:
					// Ignore candidateId supplied by browser.
					authenticatedCandidateId,

					examId, timestamp);

			return ResponseEntity.ok(savedRecord);

		} catch (Exception e) {

			return ResponseEntity.internalServerError().body("Error saving video evidence: " + e.getMessage());
		}
	}

	// exception handler for validation errors on this controller
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();

		ex.getBindingResult().getFieldErrors()
				.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
	}

}