package com.sandbox.proctoring.evaluation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sandbox.proctoring.evaluation.model.AiEvaluationResult;
import com.sandbox.proctoring.evaluation.model.TestCase;
import com.sandbox.proctoring.evaluation.repository.EvaluationRepository;

import java.util.List;
import java.util.Optional;

// Service class containing the business logic for AI evaluation
@Service
public class AiEvaluationService {

    @Autowired
    private EvaluationRepository evaluationRepository;
    
    @Autowired
    private Judge0Service judge0Service; // Your existing Judge0 service

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
   
 // Method to execute the submitted code against multiple test cases
    public AiEvaluationResult processAndSaveEvaluationWithTestCases(String sourceCode, int languageId, List<TestCase> testCases) {
        int passedCount = 0;
        StringBuilder executionLogs = new StringBuilder();

        for (int i = 0; i < testCases.size(); i++) {
            TestCase tc = testCases.get(i);

            // Submit the source code to Judge0 for execution.
            // This calls the existing service method that returns the Judge0 response as a JSON string.
            String judge0Response = judge0Service.submitCodeToJudge0(sourceCode, languageId);

            // Check whether the execution status is "Accepted".
            // Optionally, the response can be parsed using Jackson/Gson to compare
            // the actual output (stdout) with the expected output of the test case.
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
}