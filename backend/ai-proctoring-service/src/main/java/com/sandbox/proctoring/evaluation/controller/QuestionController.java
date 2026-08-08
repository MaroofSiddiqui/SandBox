package com.sandbox.proctoring.evaluation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sandbox.proctoring.evaluation.model.Question;
import com.sandbox.proctoring.evaluation.repository.QuestionRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "*")
public class QuestionController {

    @Autowired
    private QuestionRepository questionRepository;

    @GetMapping
    public ResponseEntity<List<Question>> getAllQuestions() {
        return ResponseEntity.ok(questionRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable String id) {
        Optional<Question> question = questionRepository.findById(id);
        return question.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Naya question add karne ke liye — dusra member isi se question daal sakega,
    // manual Atlas insert se _id / id field ka confusion nahi hoga
    @PostMapping
    public ResponseEntity<Question> createQuestion(@RequestBody Question question) {
        Question saved = questionRepository.save(question);
        return ResponseEntity.ok(saved);
    }
}

