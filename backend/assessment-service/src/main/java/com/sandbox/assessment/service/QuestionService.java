package com.sandbox.assessment.service;

import com.sandbox.assessment.dto.QuestionDto;
import java.util.List;

public interface QuestionService {
    QuestionDto createQuestion(QuestionDto dto);
    List<QuestionDto> getAllQuestions();
}