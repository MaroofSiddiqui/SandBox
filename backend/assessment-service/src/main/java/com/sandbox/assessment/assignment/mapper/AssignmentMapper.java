package com.sandbox.assessment.assignment.mapper;

import com.sandbox.assessment.assignment.dto.AssignmentResponseDto;
import com.sandbox.assessment.assignment.entity.Assignment;

/**
 * Utility class responsible for converting Assignment Entity
 * into AssignmentResponseDto.
 *
 * Keeping mapping logic separate improves readability
 * and makes future maintenance easier.
 */
public final class AssignmentMapper {

    private AssignmentMapper() {
        // Prevent object creation
    }

    /**
     * Converts Assignment Entity into Response DTO.
     *
     * @param assignment Assignment entity.
     * @return AssignmentResponseDto
     */
    public static AssignmentResponseDto toResponseDto(Assignment assignment) {

        if (assignment == null) {
            return null;
        }

        return AssignmentResponseDto.builder()
                .assignmentId(assignment.getId())
                .assessmentId(assignment.getAssessmentId())
                .candidateId(assignment.getCandidateId())
                .status(assignment.getStatus())
                .assignedAt(assignment.getAssignedAt())
                .startedAt(assignment.getStartedAt())
                .submittedAt(assignment.getSubmittedAt())
                .build();
    }
}