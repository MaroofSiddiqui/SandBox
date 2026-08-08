package com.sandbox.assessment.service;

import com.sandbox.assessment.dto.AssessmentDto;
import com.sandbox.assessment.entity.ExamAssignment;

import java.util.List;

public interface AssessmentService {

    AssessmentDto createAssessment(AssessmentDto dto);

    AssessmentDto publishAssessment(Long assessmentId);

    List<AssessmentDto> getAllAssessments();

    AssessmentDto getAssessmentById(Long id);

    void assignCandidates(Long assessmentId, List<Long> candidateIds);

    List<ExamAssignment> getCandidateAssignments(Long candidateId);
}