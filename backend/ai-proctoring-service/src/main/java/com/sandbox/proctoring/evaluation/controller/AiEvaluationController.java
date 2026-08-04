package com.sandbox.proctoring.evaluation.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sandbox.proctoring.evaluation.model.AiEvaluationResult;
import com.sandbox.proctoring.evaluation.model.CodeSubmission;
import com.sandbox.proctoring.evaluation.service.AiEvaluationService;
import com.sandbox.proctoring.evaluation.service.Judge0Service;
import com.sandbox.proctoring.evaluation.service.GeminiService;
import com.sandbox.proctoring.evaluation.dto.SubmissionResponse;


import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/evaluations")
@CrossOrigin(origins = "*")
public class AiEvaluationController {

    @Autowired private AiEvaluationService evaluationService;
    @Autowired private Judge0Service judge0Service;
    @Autowired private GeminiService geminiService;

    @GetMapping
    public ResponseEntity<List<AiEvaluationResult>> getAllEvaluations() {
        return ResponseEntity.ok(evaluationService.getAllEvaluations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AiEvaluationResult> getEvaluationById(@PathVariable String id) {
        Optional<AiEvaluationResult> evaluation = evaluationService.getEvaluationById(id);
        return evaluation.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<AiEvaluationResult>> getEvaluationsByStudentId(@PathVariable String studentId) {
        return ResponseEntity.ok(evaluationService.getEvaluationsByStudentId(studentId));
    }

    // ---- RUN: ab questionId leta hai, visible test cases ke against per-case result deta hai ----
    @PostMapping("/run")
    public ResponseEntity<?> runCode(@RequestBody CodeSubmission request) {
        try {
            List<AiEvaluationService.TestCaseRunResult> results = evaluationService.runVisibleTestCases(
                request.getSourceCode(), request.getLanguageId(), request.getQuestionId());
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    
   

    // ---- SUBMIT: sab test cases + AI evaluation, DB me save ----
    @PostMapping("/submit-and-save")
    public ResponseEntity<?> submitAndSaveCode(@RequestBody CodeSubmission request) {
        try {
            AiEvaluationResult result = evaluationService.processAndSaveEvaluationForQuestion(
                request.getSourceCode(), request.getLanguageId(),
                request.getQuestionId(), request.getStudentId());
            SubmissionResponse response = new SubmissionResponse(
                    true,
                    "Code submitted successfully",
                    result.getHiddenTestsPassed(),
                    result.getHiddenTotalTests()
                );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
