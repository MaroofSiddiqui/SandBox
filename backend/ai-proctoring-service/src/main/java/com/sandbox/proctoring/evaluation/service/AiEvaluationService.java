package com.sandbox.proctoring.evaluation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sandbox.proctoring.evaluation.model.AiEvaluationResult;
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
   
    public AiEvaluationResult processAndSaveEvaluation(String sourceCode, int languageId) {
        // 1. Call Judge0 API (which returns a String response or result summary)
        String judge0Response = judge0Service.submitCodeToJudge0(sourceCode, languageId);

        // 2. Create a new Evaluation Result object and map the values
        AiEvaluationResult evaluationResult = new AiEvaluationResult();
        evaluationResult.setSourceCode(sourceCode);
        evaluationResult.setLanguageId(languageId);
        evaluationResult.setStdout(judge0Response); // Storing the response in stdout or appropriate field
        
        // 3. Save into MongoDB database
        return evaluationRepository.save(evaluationResult);
    }
}