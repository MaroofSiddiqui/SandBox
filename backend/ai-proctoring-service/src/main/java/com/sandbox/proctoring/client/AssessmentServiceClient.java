package com.sandbox.proctoring.client;

import com.sandbox.proctoring.client.dto.AssessmentQuestionDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AssessmentServiceClient {

	private final RestTemplate restTemplate = new RestTemplate();

	@Value("${assessment.service.url:http://localhost:8082}")
	private String assessmentServiceUrl;

	public AssessmentQuestionDto getQuestionById(Long questionId, String authorizationHeader) {

		String url = assessmentServiceUrl + "/question/" + questionId;

		HttpHeaders headers = new HttpHeaders();

		// Forward the same JWT received by AI-Proctoring Service
		if (authorizationHeader != null && !authorizationHeader.isBlank()) {
			headers.set(HttpHeaders.AUTHORIZATION, authorizationHeader);
		}

		HttpEntity<Void> entity = new HttpEntity<>(headers);

		ResponseEntity<AssessmentQuestionDto> response = restTemplate.exchange(url, HttpMethod.GET, entity,
				AssessmentQuestionDto.class);

		return response.getBody();
	}

	public boolean validateCandidateSubmission(Long assessmentId, Long candidateId, String authorizationHeader) {

		String url = assessmentServiceUrl + "/assessment-submission/validate/" + assessmentId + "/" + candidateId;

		HttpHeaders headers = new HttpHeaders();

		if (authorizationHeader != null && !authorizationHeader.isBlank()) {

			headers.set(HttpHeaders.AUTHORIZATION, authorizationHeader);
		}

		HttpEntity<Void> entity = new HttpEntity<>(headers);

		ResponseEntity<Boolean> response = restTemplate.exchange(url, HttpMethod.GET, entity, Boolean.class);

		return Boolean.TRUE.equals(response.getBody());
	}
}