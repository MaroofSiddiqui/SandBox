package com.sandbox.assessment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.sandbox.assessment.dto.FinishAssessmentResponse;
import com.sandbox.assessment.dto.StartAssessmentResponse;
import com.sandbox.assessment.service.AssessmentSubmissionService;

import io.jsonwebtoken.Claims;

@RestController
@RequestMapping("/assessment-submission")
public class AssessmentSubmissionController {

	private final AssessmentSubmissionService submissionService;

	public AssessmentSubmissionController(AssessmentSubmissionService submissionService) {

		this.submissionService = submissionService;
	}

	@PostMapping("/start/{assessmentId}")
	public ResponseEntity<StartAssessmentResponse> startAssessment(@PathVariable Long assessmentId,
			Authentication authentication) {

		/*
		 * JwtAuthenticationFilter already stores all JWT claims in authentication
		 * details.
		 */
		Claims claims = (Claims) authentication.getDetails();

		Number userId = claims.get("userId", Number.class);

		if (userId == null) {
			throw new RuntimeException("User ID not found in authentication token");
		}

		Long candidateId = userId.longValue();

		StartAssessmentResponse response = submissionService.startAssessment(assessmentId, candidateId);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/finish/{submissionId}")
	public ResponseEntity<FinishAssessmentResponse> finishAssessment(
	        @PathVariable Long submissionId,
	        @RequestHeader("Authorization") String authorizationHeader,
	        Authentication authentication) {

	    Claims claims = (Claims) authentication.getDetails();

	    Number userId = claims.get("userId", Number.class);

	    if (userId == null) {
	        throw new RuntimeException(
	                "User ID not found in authentication token"
	        );
	    }

	    Long candidateId = userId.longValue();

	    return ResponseEntity.ok(
	            submissionService.finishAssessment(
	                    submissionId,
	                    candidateId,
	                    authorizationHeader
	            )
	    );
	}
	
}