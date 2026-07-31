package com.sandbox.proctoring.evaluation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sandbox.proctoring.evaluation.model.AiEvaluationResult;
import com.sandbox.proctoring.evaluation.model.Question;
import com.sandbox.proctoring.evaluation.model.TestCase;
import com.sandbox.proctoring.evaluation.repository.EvaluationRepository;
import com.sandbox.proctoring.evaluation.repository.QuestionRepository;

import java.util.List;
import java.util.Optional;

// Service class containing the business logic for AI evaluation
@Service
public class AiEvaluationService {

    @Autowired
    private EvaluationRepository evaluationRepository;
    
    @Autowired
    private Judge0Service judge0Service; // Your existing Judge0 service

    @Autowired
    private QuestionRepository questionRepository;
    
    // Save a new evaluation result to the database
    public AiEvaluationResult saveEvaluation(AiEvaluationResult evaluation) {
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
        evaluationResult.setSourceCode(sourceCode);
        evaluationResult.setLanguageId(languageId);
        evaluationResult.setStdout(executionLogs.toString());

        // Calculate the score as the percentage of passed test cases.
        double score = testCases.isEmpty() ? 0.0 : ((double) passedCount / testCases.size()) * 100;
        evaluationResult.setScore(score);

        // Save the evaluation result to the database and return it.
        return evaluationRepository.save(evaluationResult);
    }

    // New Method: Fetch test cases from MongoDB using questionId and evaluate
    public AiEvaluationResult processAndSaveEvaluationForQuestion(String sourceCode, int languageId, String questionId) {
        // 1. Fetch question and its test cases from database
        Question question = questionRepository.findById(questionId).orElse(null);
        
        List<TestCase> testCases;
        if (question != null && question.getTestCases() != null && !question.getTestCases().isEmpty()) {
            testCases = question.getTestCases();
        } else {
            // Fallback mock test cases if question ID is not found in DB
            testCases = List.of(new TestCase("default_input", "default_output"));
        }

        int passedCount = 0;
        StringBuilder executionLogs = new StringBuilder();

        // 2. Loop through the test cases
        for (int i = 0; i < testCases.size(); i++) {
            TestCase tc = testCases.get(i);
            
            // Call Judge0 service
            String judge0Response = judge0Service.submitCodeToJudge0(sourceCode, languageId);
            
            // Evaluate pass/fail status
            if (judge0Response != null && judge0Response.contains("\"description\":\"Accepted\"")) {
                passedCount++;
                executionLogs.append("Test Case ").append(i + 1).append(": PASSED\n");
            } else {
                executionLogs.append("Test Case ").append(i + 1).append(": FAILED\n");
            }
        }

        // 3. Populate and save evaluation result
        AiEvaluationResult evaluationResult = new AiEvaluationResult();
        evaluationResult.setSourceCode(sourceCode);
        evaluationResult.setLanguageId(languageId);
        evaluationResult.setStdout(executionLogs.toString());
        
        double score = testCases.isEmpty() ? 0.0 : ((double) passedCount / testCases.size()) * 100;
        evaluationResult.setScore(score);

        return evaluationRepository.save(evaluationResult);
    }
}