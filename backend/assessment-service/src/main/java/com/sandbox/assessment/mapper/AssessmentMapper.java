package com.sandbox.assessment.mapper;

import com.sandbox.assessment.dto.request.AssessmentRequest;
import com.sandbox.assessment.dto.response.AssessmentResponse;
import com.sandbox.assessment.entity.Assessment;

public class AssessmentMapper {

    // Request DTO -> Entity
    public static Assessment toEntity(AssessmentRequest request) {

        Assessment assessment = new Assessment();

        assessment.setTitle(request.getTitle());
        assessment.setDescription(request.getDescription());
        assessment.setDurationMinutes(request.getDurationMinutes());
        assessment.setTotalMarks(request.getTotalMarks());
        assessment.setPassingMarks(request.getPassingMarks());
        assessment.setStatus(request.getStatus());
        assessment.setCreatedBy(request.getCreatedBy());

        return assessment;
    }

    // Entity -> Response DTO
    public static AssessmentResponse toResponse(Assessment assessment) {

        AssessmentResponse response = new AssessmentResponse();

        response.setId(assessment.getId());
        response.setTitle(assessment.getTitle());
        response.setDescription(assessment.getDescription());
        response.setDurationMinutes(assessment.getDurationMinutes());
        response.setTotalMarks(assessment.getTotalMarks());
        response.setPassingMarks(assessment.getPassingMarks());
        response.setStatus(assessment.getStatus());
        response.setCreatedBy(assessment.getCreatedBy());
        response.setCreatedAt(assessment.getCreatedAt());
        response.setUpdatedAt(assessment.getUpdatedAt());

        return response;
    }

}