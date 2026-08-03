package com.sandbox.proctoring.evaluation.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class GeminiService {

    // API key ab application.properties se aa rahi hai (gemini.api.key=...)
    // Add this line to application.properties:  gemini.api.key=YOUR_KEY_HERE
    @Value("${gemini.api.key}")
    private String apiKey;

    public String analyzeCodeWithGemini(String sourceCode, String problemDescription, String testResults) {
        try {
            // 100% PURE JAVA HTTP CALL - Spring Boot RestTemplate completely bypassed!
            // FIX: gemini-1.5-flash-latest is retired -> switched to a currently supported alias
            String urlString = "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-lite-latest:generateContent?key=" + apiKey;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Clean prompt telling AI exactly what to return
            String prompt = "You are an expert code reviewer. Analyze the following code.\n" +
                    "Problem: " + problemDescription + "\nCode:\n" + sourceCode + "\nTest Results:\n" + testResults + "\n\n" +
                    "Return ONLY a raw JSON object with exactly these keys: \"codeQualityScore\" (number), \"efficiencyComments\" (string), \"bugsFound\" (string), \"constructiveFeedback\" (string). Do not use markdown like ```json.";

            ObjectMapper mapper = new ObjectMapper();
            // Safely convert prompt to string for JSON payload
            String safePrompt = mapper.writeValueAsString(prompt);

            // Manual JSON building to avoid any complex mapping issues
            String jsonPayload = "{\n  \"contents\": [\n    {\n      \"parts\": [\n        {\n          \"text\": " + safePrompt + "\n        }\n      ]\n    }\n  ]\n}";

            // Send request
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get Response
            int responseCode = conn.getResponseCode();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (responseCode >= 200 && responseCode <= 299) ? conn.getInputStream() : conn.getErrorStream(), "utf-8"));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }

            // Parse result safely
            if (responseCode >= 200 && responseCode <= 299) {
                JsonNode rootNode = mapper.readTree(response.toString());
                return rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
            } else {
                return "{\"codeQualityScore\": 30.0, \"constructiveFeedback\": \"API Error: " + response.toString().replace("\"", "'") + "\"}";
            }

        } catch (Exception e) {
            return "{\"codeQualityScore\": 30.0, \"constructiveFeedback\": \"System Error: " + e.getMessage() + "\"}";
        }
    }
}