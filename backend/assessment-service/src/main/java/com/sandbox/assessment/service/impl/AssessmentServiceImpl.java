package com.sandbox.assessment.service.impl;

import com.sandbox.assessment.dto.AssessmentDto;
import com.sandbox.assessment.entity.Assessment;
import com.sandbox.assessment.entity.AssessmentQuestion;
import com.sandbox.assessment.entity.Question;
import com.sandbox.assessment.repository.AssessmentRepository;
import com.sandbox.assessment.repository.QuestionRepository;
import com.sandbox.assessment.service.AssessmentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssessmentServiceImpl implements AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final QuestionRepository questionRepository;

    public AssessmentServiceImpl(AssessmentRepository assessmentRepository, QuestionRepository questionRepository) {
        this.assessmentRepository = assessmentRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public AssessmentDto createAssessment(AssessmentDto dto) {
        Assessment assessment = Assessment.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .durationInMinutes(dto.getDurationInMinutes())
                .passingMarks(dto.getPassingMarks())
                .negativeMarks(dto.getNegativeMarks())
                .isPublished(false)
                .build();

        if (dto.getQuestionIds() != null && !dto.getQuestionIds().isEmpty()) {
            List<Question> questions = questionRepository.findAllById(dto.getQuestionIds());
            List<AssessmentQuestion> assessmentQuestions = questions.stream().map(q -> 
                AssessmentQuestion.builder()
                        .assessment(assessment)
                        .question(q)
                        .build()
            ).collect(Collectors.toList());
            assessment.setAssessmentQuestions(assessmentQuestions);
        }

        Assessment saved = assessmentRepository.save(assessment);
        dto.setId(saved.getId());
        dto.setIsPublished(saved.getIsPublished());
        return dto;
    }

    @Override
    public AssessmentDto publishAssessment(Long assessmentId) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found with ID: " + assessmentId));
        
        assessment.setIsPublished(true);
        Assessment updated = assessmentRepository.save(assessment);
        
        return mapToDto(updated);
    }

    @Override
    public List<AssessmentDto> getAllAssessments() {
        return assessmentRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public AssessmentDto getAssessmentById(Long id) {
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assessment not found with ID: " + id));
        return mapToDto(assessment);
    }

    private AssessmentDto mapToDto(Assessment assessment) {
        List<Long> qIds = assessment.getAssessmentQuestions().stream()
                .map(aq -> aq.getQuestion().getId())
                .collect(Collectors.toList());

        return AssessmentDto.builder()
                .id(assessment.getId())
                .title(assessment.getTitle())
                .description(assessment.getDescription())
                .durationInMinutes(assessment.getDurationInMinutes())
                .passingMarks(assessment.getPassingMarks())
                .negativeMarks(assessment.getNegativeMarks())
                .isPublished(assessment.getIsPublished())
                .questionIds(qIds)
                .build();
    }
}