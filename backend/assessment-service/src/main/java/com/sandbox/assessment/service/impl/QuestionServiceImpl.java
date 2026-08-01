package com.sandbox.assessment.service.impl;

import com.sandbox.assessment.entity.Question;
import com.sandbox.assessment.repository.QuestionRepository;
import com.sandbox.assessment.service.QuestionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public Question createQuestion(Question question) {
        // Link MCQ options to parent question if present
        if (question.getMcqOptions() != null) {
            question.getMcqOptions().forEach(opt -> opt.setQuestion(question));
        }
        // Link Coding details to parent question if present
        if (question.getCodingQuestion() != null) {
            question.getCodingQuestion().setQuestion(question);
        }
        return questionRepository.save(question);
    }

    @Override
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @Override
    public Question getQuestionById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found with ID: " + id));
    }
}