package com.sandbox.assessment.client;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AiEvaluationClient {

    private final RestClient restClient;

    public AiEvaluationClient() {

        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8083")
                .build();
    }

    public Double getEvaluationScore(
            String evaluationId,
            String authorizationHeader) {

        Map<?, ?> response = restClient.get()
                .uri("/api/evaluations/{id}/score", evaluationId)
                .header("Authorization", authorizationHeader)
                .retrieve()
                .body(Map.class);

        if (response == null || response.get("score") == null) {
            throw new RuntimeException(
                    "Score not found for coding evaluation: " + evaluationId
            );
        }

        Object score = response.get("score");

        if (score instanceof Number number) {
            return number.doubleValue();
        }

        return Double.parseDouble(score.toString());
    }
}