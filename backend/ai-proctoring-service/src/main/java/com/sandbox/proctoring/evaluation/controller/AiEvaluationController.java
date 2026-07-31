package com.sandbox.proctoring.evaluation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sandbox.proctoring.evaluation.model.AiEvaluationResult;
import com.sandbox.proctoring.evaluation.model.CodeSubmission;
import com.sandbox.proctoring.evaluation.model.TestCase;
import com.sandbox.proctoring.evaluation.service.AiEvaluationService;
import com.sandbox.proctoring.evaluation.service.Judge0Service;

import java.util.List;
import java.util.Optional;

// REST Controller to handle HTTP requests for AI evaluations
@RestController
@RequestMapping("/api/evaluations")
public class AiEvaluationController {

    @Autowired
    private AiEvaluationService evaluationService;

    @Autowired
    private Judge0Service judge0Service;

    // POST endpoint to create/save a new evaluation result
    @PostMapping
    public ResponseEntity<AiEvaluationResult> createEvaluation(@RequestBody AiEvaluationResult evaluation) {
        AiEvaluationResult savedEvaluation = evaluationService.saveEvaluation(evaluation);
        return ResponseEntity.ok(savedEvaluation);
    }

    // GET endpoint to retrieve all evaluation results
    @GetMapping
    public ResponseEntity<List<AiEvaluationResult>> getAllEvaluations() {
        List<AiEvaluationResult> evaluations = evaluationService.getAllEvaluations();
        return ResponseEntity.ok(evaluations);
    }

    // GET endpoint to retrieve an evaluation result by its ID
    @GetMapping("/{id}")
    public ResponseEntity<AiEvaluationResult> getEvaluationById(@PathVariable String id) {
        Optional<AiEvaluationResult> evaluation = evaluationService.getEvaluationById(id);
        return evaluation.map(ResponseEntity::ok)
                         .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET endpoint to retrieve evaluations by student ID
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<AiEvaluationResult>> getEvaluationsByStudentId(@PathVariable String studentId) {
        List<AiEvaluationResult> evaluations = evaluationService.getEvaluationsByStudentId(studentId);
        return ResponseEntity.ok(evaluations);
    }
    
    // POST endpoint to submit code directly to Judge0 without saving
    @PostMapping("/submit-code")
    public ResponseEntity<String> submitCode(@RequestParam String sourceCode, @RequestParam int languageId) {
        String evaluationResult = judge0Service.submitCodeToJudge0(sourceCode, languageId);
        return ResponseEntity.ok(evaluationResult);
    }
    
 // POST endpoint to submit code, execute via Judge0 with test cases, and save into MongoDB
    @PostMapping("/submit-and-save")
    public AiEvaluationResult submitAndSaveCode(@RequestBody CodeSubmission request) {
        // Agar request mein test cases nahi aa rahe hain, toh aap default mock test cases bhej sakte hain
        List<TestCase> mockTestCases = List.of(
            new TestCase("5", "5"), // Example input and expected output
            new TestCase("10", "10")
        );
        
        return evaluationService.processAndSaveEvaluationWithTestCases(
            request.getSourceCode(), 
            request.getLanguageId(), 
            mockTestCases
        );
    }
}