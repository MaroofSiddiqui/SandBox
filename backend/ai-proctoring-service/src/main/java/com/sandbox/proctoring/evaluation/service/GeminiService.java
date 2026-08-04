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

    @Value("${gemini.api.key}")
    private String apiKey;

    public String analyzeCodeWithGemini(String assessmentJsonPayload) {
        try {
        	String urlString = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash-lite:generateContent?key=" + apiKey;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // ==========================================
            // JSON AWARE PROMPT ENGINEERING
            // ==========================================
            String prompt = "You are a fair, expert technical interviewer. I am providing you with a JSON payload containing a coding problem, test cases, and a candidate's code submission.\n\n" +
                    "### ASSESSMENT DATA (JSON) ###\n" +
                    assessmentJsonPayload + "\n\n" +
                    "### EVALUATION & SCORING RULES (Score out of 100) ###\n" +
                    "1. ANALYZE EXECUTION: Mentally dry-run the candidate's code against the provided test cases (both visible and hidden). Check for runtime errors (like index out of bounds) or incorrect logic.\n" +
                    "2. ANTI-CHEAT: If the code is completely irrelevant or just hardcodes answers to pass test cases, score = 0.\n" +
                    "3. LOGIC & STEP MARKING: Focus on the algorithmic approach. If the logic is generally in the right direction but fails edge cases (e.g., fails on empty arrays or small inputs), award partial marks (e.g., 50-80) based on the core logic's quality.\n" +
                    "4. EFFICIENCY: Evaluate time and space complexity based on the given constraints.\n\n" +
                    "Return ONLY a raw JSON object with exactly these keys: \"codeQualityScore\" (number 0-100), \"efficiencyComments\" (string), \"bugsFound\" (string), \"constructiveFeedback\" (string). Do not use markdown formatting like ```json.";

            System.out.println("Sending Prompt to Gemini...");
            
            ObjectMapper mapper = new ObjectMapper();
            String safePrompt = mapper.writeValueAsString(prompt);
            
            String jsonPayload = "{\n  \"contents\": [\n    {\n      \"parts\": [\n        {\n          \"text\": " + safePrompt + "\n        }\n      ]\n    }\n  ]\n}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (responseCode >= 200 && responseCode <= 299) ? conn.getInputStream() : conn.getErrorStream(), "utf-8"));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }

            if (responseCode >= 200 && responseCode <= 299) {
                JsonNode rootNode = mapper.readTree(response.toString());
                return rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
            } else {
                return "{\"codeQualityScore\": 0.0, \"constructiveFeedback\": \"API Error: " + response.toString().replace("\"", "'") + "\"}";
            }

        } catch (Exception e) {
            return "{\"codeQualityScore\": 0.0, \"constructiveFeedback\": \"System Error: " + e.getMessage() + "\"}";
        }
    }
}