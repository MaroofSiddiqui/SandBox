package com.sandbox.proctoring.evaluation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sandbox.proctoring.evaluation.model.AiEvaluationResult;
import com.sandbox.proctoring.evaluation.model.Judge0Result;
import com.sandbox.proctoring.evaluation.model.Question;
import com.sandbox.proctoring.evaluation.model.TestCase;
import com.sandbox.proctoring.evaluation.repository.EvaluationRepository;
import com.sandbox.proctoring.evaluation.repository.QuestionRepository;
import com.sandbox.proctoring.evaluation.exception.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AiEvaluationService {

	@Autowired(required = false) private EvaluationRepository evaluationRepository;
	@Autowired(required = false) private Judge0Service judge0Service;
	@Autowired(required = false) private QuestionRepository questionRepository;
	@Autowired(required = false) private GeminiService geminiService;

    public AiEvaluationResult saveEvaluation(AiEvaluationResult evaluation) {
        return evaluationRepository.save(evaluation);
    }

    public List<AiEvaluationResult> getAllEvaluations() {
        return evaluationRepository.findAll();
    }

    public Optional<AiEvaluationResult> getEvaluationById(String id) {
        return evaluationRepository.findById(id);
    }

    public List<AiEvaluationResult> getEvaluationsByStudentId(String studentId) {
        return evaluationRepository.findByStudentId(studentId);
    }

    // ================= RUN (sirf visible test cases, real console jaisa) =================
    public List<TestCaseRunResult> runVisibleTestCases(String sourceCode, int languageId, String questionId) {
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));

        List<TestCase> visibleCases = question.getTestCases().stream()
            .filter(tc -> !tc.isHidden())
            .collect(Collectors.toList());

        if (visibleCases.isEmpty()) {
            throw new RuntimeException("No visible test cases found for this question!");
        }

        List<TestCaseRunResult> results = new ArrayList<>();
        for (int i = 0; i < visibleCases.size(); i++) {
            TestCase tc = visibleCases.get(i);
            Judge0Result judge0Result = judge0Service.runAgainstTestCase(
                sourceCode, languageId, tc.getInput(), tc.getExpectedOutput());

            results.add(new TestCaseRunResult(
                i + 1, tc.getInput(), tc.getExpectedOutput(),
                judge0Result.getActualOutput(), judge0Result.isPassed(),
                judge0Result.getErrorMessage()
            ));
        }
        return results;
    }

    // ================= SUBMIT (sab test cases + AI evaluation) =================
    public AiEvaluationResult processAndSaveEvaluationForQuestion(String sourceCode, int languageId, String questionId, String studentId) {
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));

        List<TestCase> testCases = question.getTestCases();
        if (testCases == null || testCases.isEmpty()) {
            throw new RuntimeException("No test cases found for this question!");
        }

        int passedCount = 0;
        int hiddenPassedCount = 0;
        int hiddenTotalCount = 0;
        StringBuilder executionLogs = new StringBuilder();

        for (int i = 0; i < testCases.size(); i++) {
            TestCase tc = testCases.get(i);
            Judge0Result judge0Result = judge0Service.runAgainstTestCase(
                sourceCode, languageId, tc.getInput(), tc.getExpectedOutput());

            boolean passed = judge0Result.isPassed();
            if (passed) passedCount++;

            if (tc.isHidden()) {
                hiddenTotalCount++;
                if (passed) hiddenPassedCount++;
            }

            executionLogs.append("Test Case ").append(i + 1).append(": ").append(passed ? "PASSED" : "FAILED").append("\n");
        }

        double correctnessScore = ((double) passedCount / testCases.size()) * 100;

        // ----- AI evaluation -----
        String geminiRawResponse;
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode payloadNode = mapper.createObjectNode();
            payloadNode.put("sourceCode", sourceCode);
            payloadNode.put("questionDescription", question.getDescription());
            payloadNode.put("testCaseResults", executionLogs.toString());
            String jsonPayload = mapper.writeValueAsString(payloadNode);
            geminiRawResponse = geminiService.analyzeCodeWithGemini(jsonPayload);
        } catch (Exception e) {
            geminiRawResponse = "{\"codeQualityScore\": 0.0, \"constructiveFeedback\": \"Error contacting AI\", \"bugsFound\": \"Unknown\", \"efficiencyComments\": \"N/A\"}";
        }

        double aiScore = 0.0;
        String constructiveFeedback = "No feedback generated.";
        String bugsFound = "None";
        String efficiencyComments = "N/A";

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String cleanedJson = geminiRawResponse.replaceAll("```json", "").replaceAll("```", "").trim();
            JsonNode rootNode = objectMapper.readTree(cleanedJson);
            aiScore = rootNode.path("codeQualityScore").asDouble(0.0);
            constructiveFeedback = rootNode.path("constructiveFeedback").asText("No feedback provided.");
            bugsFound = rootNode.path("bugsFound").asText("None");
            efficiencyComments = rootNode.path("efficiencyComments").asText("N/A");
        } catch (Exception e) {
            aiScore = 0.0;
            constructiveFeedback = "Raw AI Response: " + geminiRawResponse;
        }

        AiEvaluationResult evaluationResult = new AiEvaluationResult();
        evaluationResult.setId(UUID.randomUUID().toString());
        evaluationResult.setSourceCode(sourceCode);
        evaluationResult.setLanguageId(languageId);
        evaluationResult.setStdout(executionLogs.toString());
        evaluationResult.setTestsPassed(passedCount);
        evaluationResult.setTotalTests(testCases.size());
        evaluationResult.setCorrectnessScore(correctnessScore);   // ✅ test cases ka score
        evaluationResult.setCodeQualityScore(aiScore);            // ✅ AI ka alag score
        evaluationResult.setScore(correctnessScore);              // headline score = correctness
        evaluationResult.setEfficiencyComments(efficiencyComments);
        evaluationResult.setBugsFound(bugsFound);
        evaluationResult.setConstructiveFeedback(constructiveFeedback);
        evaluationResult.setHiddenTestsPassed(hiddenPassedCount);
        evaluationResult.setHiddenTotalTests(hiddenTotalCount);
        evaluationResult.setStudentId(studentId); 
        // studentId set karna hoga agar AiEvaluationResult model me field hai — file dekh ke confirm karunga

        return evaluationRepository.save(evaluationResult);
    }

    // Helper DTO — Run ke response ke liye
    public static class TestCaseRunResult {
        private final int testCaseNumber;
        private final String input;
        private final String expectedOutput;
        private final String actualOutput;
        private final boolean passed;
        private final String errorMessage;

        public TestCaseRunResult(int testCaseNumber, String input, String expectedOutput,
                                  String actualOutput, boolean passed, String errorMessage) {
            this.testCaseNumber = testCaseNumber;
            this.input = input;
            this.expectedOutput = expectedOutput;
            this.actualOutput = actualOutput;
            this.passed = passed;
            this.errorMessage = errorMessage;
        }
        public int getTestCaseNumber() { return testCaseNumber; }
        public String getInput() { return input; }
        public String getExpectedOutput() { return expectedOutput; }
        public String getActualOutput() { return actualOutput; }
        public boolean isPassed() { return passed; }
        public String getErrorMessage() { return errorMessage; }
    }
}