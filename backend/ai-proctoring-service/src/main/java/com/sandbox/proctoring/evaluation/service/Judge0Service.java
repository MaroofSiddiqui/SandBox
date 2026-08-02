package com.sandbox.proctoring.evaluation.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class Judge0Service {

    // Free public Judge0 API endpoint (or your rapidapi endpoint)
    private static final String JUDGE0_URL = "https://ce.judge0.com/submissions?wait=true";

    public String submitCodeToJudge0(String sourceCode, int languageId) {
        RestTemplate restTemplate = new RestTemplate();

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Prepare request body for Judge0
        Map<String, Object> body = new HashMap<>();
        body.put("source_code", sourceCode);
        body.put("language_id", languageId); // e.g., 62 for Java, 71 for Python

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            // Call Judge0 API
            ResponseEntity<String> response = restTemplate.postForEntity(JUDGE0_URL, requestEntity, String.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Failed to connect to Judge0 API: " + e.getMessage() + "\"}";
        }
    }
}