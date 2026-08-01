package com.sandbox.assessment.dto;

import com.sandbox.assessment.entity.Question.DifficultyLevel;
import com.sandbox.assessment.entity.Question.QuestionType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDto {
    private Long id;
    private String title;
    private QuestionType questionType;
    private DifficultyLevel difficulty;
    private Double marks;
    private String category;
    
    // For MCQ questions
    private List<McqOptionDto> mcqOptions;

    // For Coding questions
    private String problemStatement;
    private String driverCode;
    private String sampleTestCasesJson;
    private String hiddenTestCasesJson;
    private Integer timeLimitInSeconds;
    private Integer memoryLimitInMb;
}