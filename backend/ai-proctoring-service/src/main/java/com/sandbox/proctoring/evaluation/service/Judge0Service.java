package com.sandbox.proctoring.evaluation.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandbox.proctoring.evaluation.model.Judge0Result;
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

    private static final String JUDGE0_URL = "https://ce.judge0.com/submissions?wait=true";

    // Test case ke against run karta hai — expected_output Judge0 ko hi bhej rahe hain,
    // isliye Judge0 khud "Accepted" / "Wrong Answer" verdict deta hai
    public Judge0Result runAgainstTestCase(String sourceCode, int languageId, String stdin, String expectedOutput) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("source_code", sourceCode);
        body.put("language_id", languageId);
        body.put("stdin", stdin);
        body.put("expected_output", expectedOutput);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(JUDGE0_URL, requestEntity, String.class);
            return parseJudge0Response(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return new Judge0Result(false, "", "Failed to connect to Judge0 API: " + e.getMessage());
        }
    }

    // Purana raw method — sirf plain "Run" ke liye jab test case na ho (fallback)
    public String submitCodeToJudge0(String sourceCode, int languageId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("source_code", sourceCode);
        body.put("language_id", languageId);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(JUDGE0_URL, requestEntity, String.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Failed to connect to Judge0 API: " + e.getMessage() + "\"}";
        }
    }

    private Judge0Result parseJudge0Response(String responseBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(responseBody);
            String statusDesc = node.path("status").path("description").asText("");
            String stdout = node.path("stdout").asText("");
            String stderr = node.path("stderr").asText("");
            String compileOutput = node.path("compile_output").asText("");

            boolean passed = "Accepted".equalsIgnoreCase(statusDesc);
            String errorMsg = !compileOutput.isEmpty() ? compileOutput : stderr;

            return new Judge0Result(passed, stdout, errorMsg);
        } catch (Exception e) {
            return new Judge0Result(false, "", "Failed to parse Judge0 response: " + e.getMessage());
        }
    }
}