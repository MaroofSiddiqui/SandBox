package com.sandbox.assessment.controller;

import com.sandbox.assessment.dto.QuestionDto;
import com.sandbox.assessment.service.QuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/question")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping("/create")
    public ResponseEntity<QuestionDto> createQuestion(@RequestBody QuestionDto questionDto) {
        QuestionDto created = questionService.createQuestion(questionDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<QuestionDto>> getAllQuestions() {
        return ResponseEntity.ok(questionService.getAllQuestions());
    }
}