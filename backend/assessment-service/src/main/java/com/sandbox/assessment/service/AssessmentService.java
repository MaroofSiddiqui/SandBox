package com.sandbox.assessment.service;

import com.sandbox.assessment.dto.AssessmentDto;
import java.util.List;

public interface AssessmentService {
    AssessmentDto createAssessment(AssessmentDto dto);
    AssessmentDto publishAssessment(Long assessmentId);
    List<AssessmentDto> getAllAssessments();
    AssessmentDto getAssessmentById(Long id);
}