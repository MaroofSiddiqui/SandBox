package com.sandbox.assessment.service.impl;

import com.sandbox.assessment.dto.McqOptionDto;
import com.sandbox.assessment.dto.QuestionDto;
import com.sandbox.assessment.entity.CodingQuestion;
import com.sandbox.assessment.entity.McqOption;
import com.sandbox.assessment.entity.Question;
import com.sandbox.assessment.repository.QuestionRepository;
import com.sandbox.assessment.service.QuestionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public QuestionDto createQuestion(QuestionDto dto) {
        Question question = Question.builder()
                .title(dto.getTitle())
                .questionType(dto.getQuestionType())
                .difficulty(dto.getDifficulty())
                .marks(dto.getMarks())
                .category(dto.getCategory())
                .build();

        if (dto.getQuestionType() == Question.QuestionType.MCQ && dto.getMcqOptions() != null) {
            List<McqOption> options = dto.getMcqOptions().stream().map(oDto -> 
                McqOption.builder()
                        .optionText(oDto.getOptionText())
                        .isCorrect(oDto.getIsCorrect())
                        .question(question)
                        .build()
            ).collect(Collectors.toList());
            question.setMcqOptions(options);
        }

        if (dto.getQuestionType() == Question.QuestionType.CODING) {
            CodingQuestion codingQuestion = CodingQuestion.builder()
                    .problemStatement(dto.getProblemStatement())
                    .driverCode(dto.getDriverCode())
                    .sampleTestCasesJson(dto.getSampleTestCasesJson())
                    .hiddenTestCasesJson(dto.getHiddenTestCasesJson())
                    .timeLimitInSeconds(dto.getTimeLimitInSeconds())
                    .memoryLimitInMb(dto.getMemoryLimitInMb())
                    .question(question)
                    .build();
            question.setCodingQuestion(codingQuestion);
        }

        Question saved = questionRepository.save(question);
        dto.setId(saved.getId());
        return dto;
    }

    @Override
    public List<QuestionDto> getAllQuestions() {
        return questionRepository.findAll().stream().map(q -> {
            QuestionDto.QuestionDtoBuilder builder = QuestionDto.builder()
                    .id(q.getId())
                    .title(q.getTitle())
                    .questionType(q.getQuestionType())
                    .difficulty(q.getDifficulty())
                    .marks(q.getMarks())
                    .category(q.getCategory());

            if (q.getMcqOptions() != null) {
                builder.mcqOptions(q.getMcqOptions().stream().map(o -> 
                    new McqOptionDto(o.getId(), o.getOptionText(), o.getIsCorrect())
                ).collect(Collectors.toList()));
            }

            if (q.getCodingQuestion() != null) {
                builder.problemStatement(q.getCodingQuestion().getProblemStatement())
                       .driverCode(q.getCodingQuestion().getDriverCode())
                       .sampleTestCasesJson(q.getCodingQuestion().getSampleTestCasesJson())
                       .hiddenTestCasesJson(q.getCodingQuestion().getHiddenTestCasesJson())
                       .timeLimitInSeconds(q.getCodingQuestion().getTimeLimitInSeconds())
                       .memoryLimitInMb(q.getCodingQuestion().getMemoryLimitInMb());
            }

            return builder.build();
        }).collect(Collectors.toList());
    }
}