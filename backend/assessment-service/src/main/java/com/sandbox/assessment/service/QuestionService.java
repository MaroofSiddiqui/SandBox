package com.sandbox.assessment.service;

import com.sandbox.assessment.entity.Question;
import java.util.List;

public interface QuestionService {
    Question createQuestion(Question question);
    List<Question> getAllQuestions();
    Question getQuestionById(Long id);
}