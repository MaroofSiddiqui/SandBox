package com.sandbox.proctoring.evaluation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandbox.proctoring.evaluation.model.AiEvaluationResult;
import com.sandbox.proctoring.evaluation.model.Question;
import com.sandbox.proctoring.evaluation.model.TestCase;
import com.sandbox.proctoring.evaluation.repository.EvaluationRepository;
import com.sandbox.proctoring.evaluation.repository.QuestionRepository;
import com.sandbox.proctoring.evaluation.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID; // Added for safe ID generation

// Service class containing the business logic for AI evaluation
@Service
public class AiEvaluationService {

    @Autowired
    private EvaluationRepository evaluationRepository;
    
    @Autowired
    private Judge0Service judge0Service; // Your existing Judge0 service

    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private GeminiService geminiService;
    
    // Save a new evaluation result to the database
    public AiEvaluationResult saveEvaluation(AiEvaluationResult evaluation) {
        if (evaluation.getId() == null || evaluation.getId().isEmpty()) {
            evaluation.setId(UUID.randomUUID().toString());
        }
        return evaluationRepository.save(evaluation);
    }

    // Retrieve all evaluation results
    public List<AiEvaluationResult> getAllEvaluations() {
        return evaluationRepository.findAll();
    }

    // Find a specific evaluation result by its ID
    public Optional<AiEvaluationResult> getEvaluationById(String id) {
        return evaluationRepository.findById(id);
    }

    // Find evaluation results by student ID
    public List<AiEvaluationResult> getEvaluationsByStudentId(String studentId) {
        return evaluationRepository.findByStudentId(studentId);
    }
   
    // Method to execute the submitted code against multiple test cases (Direct List)
    public AiEvaluationResult processAndSaveEvaluationWithTestCases(String sourceCode, int languageId, List<TestCase> testCases) {
        int passedCount = 0;
        StringBuilder executionLogs = new StringBuilder();

        for (int i = 0; i < testCases.size(); i++) {
            TestCase tc = testCases.get(i);

            // Submit the source code to Judge0 for execution.
            String judge0Response = judge0Service.submitCodeToJudge0(sourceCode, languageId);

            // Check whether the execution status is "Accepted".
            if (judge0Response.contains("\"description\":\"Accepted\"")) {
                passedCount++;
                executionLogs.append("Test Case ").append(i + 1).append(": PASSED\n");
            } else {
                executionLogs.append("Test Case ").append(i + 1).append(": FAILED\n");
            }
        }

        // Create and populate the evaluation result object.
        AiEvaluationResult evaluationResult = new AiEvaluationResult();
        evaluationResult.setId(UUID.randomUUID().toString()); // Safe ID assignment
        evaluationResult.setSourceCode(sourceCode);
        evaluationResult.setLanguageId(languageId);
        evaluationResult.setStdout(executionLogs.toString());

        // Calculate the score as the percentage of passed test cases.
        double score = testCases.isEmpty() ? 0.0 : ((double) passedCount / testCases.size()) * 100;
        evaluationResult.setScore(score);

        // Save the evaluation result to the database and return it.
        return evaluationRepository.save(evaluationResult);
    }

    // Main business logic to fetch test cases, execute code, call Gemini AI, and save results
    public AiEvaluationResult processAndSaveEvaluationForQuestion(String sourceCode, int languageId, String questionId) {
        
        // Step 1: Fetch question details from MongoDB, throw an exception if not found
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));
        
        List<TestCase> testCases = question.getTestCases();
        if (testCases == null || testCases.isEmpty()) {
            throw new RuntimeException("No test cases found for this question!");
        }

        int passedCount = 0;
        StringBuilder executionLogs = new StringBuilder();

        // Step 2: Loop through the test cases and execute the code via Judge0
        for (int i = 0; i < testCases.size(); i++) {
            TestCase tc = testCases.get(i);
            
            String judge0Response = judge0Service.submitCodeToJudge0(sourceCode, languageId);
            
            // Check if the execution result was accepted
            if (judge0Response != null && judge0Response.contains("\"description\":\"Accepted\"")) {
                passedCount++;
                executionLogs.append("Test Case ").append(i + 1).append(": PASSED\n");
            } else {
                executionLogs.append("Test Case ").append(i + 1).append(": FAILED\n");
            }
        }

        // Step 3: Call the Gemini API to get an AI-powered code review and feedback
        String geminiRawResponse = geminiService.analyzeCodeWithGemini(
            sourceCode, 
            question.getDescription(), 
            executionLogs.toString()
        );

        // Default fallback values
        double aiScore = 0.0;
        String constructiveFeedback = "No feedback generated.";
        String bugsFound = "None";
        String efficiencyComments = "N/A";

        // Step 4: Parse Gemini's JSON response using Jackson ObjectMapper
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            
            // Clean markdown code blocks if Gemini returns them (e.g., ```json ... ```)
            String cleanedJson = geminiRawResponse.replaceAll("```json", "").replaceAll("```", "").trim();
            
            JsonNode rootNode = objectMapper.readTree(cleanedJson);
            
            aiScore = rootNode.path("codeQualityScore").asDouble(0.0);
            constructiveFeedback = rootNode.path("constructiveFeedback").asText("No feedback provided.");
            bugsFound = rootNode.path("bugsFound").asText("None");
            efficiencyComments = rootNode.path("efficiencyComments").asText("N/A");

        } catch (Exception e) {
            // Fallback if JSON parsing fails
            aiScore = 30.0; // Effort/logic partial marks
            constructiveFeedback = "Raw AI Response: " + geminiRawResponse;
        }

        // Step 5: Populate evaluation result with parsed AI analysis
        AiEvaluationResult evaluationResult = new AiEvaluationResult();
        evaluationResult.setId(UUID.randomUUID().toString()); // Safe ID assignment to prevent null error
        evaluationResult.setSourceCode(sourceCode);
        evaluationResult.setLanguageId(languageId);
        evaluationResult.setStdout(executionLogs.toString());
        evaluationResult.setScore(aiScore);                 // AI-evaluated score (marks)
        evaluationResult.setCodeQualityScore(aiScore);
        evaluationResult.setEfficiencyComments(efficiencyComments);
        evaluationResult.setBugsFound(bugsFound);
        evaluationResult.setConstructiveFeedback(constructiveFeedback);

        // Step 6: Save and return to database
        return evaluationRepository.save(evaluationResult);
    }
}