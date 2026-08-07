package com.sandbox.assessment.assignment.statistics.service.impl;

import com.sandbox.assessment.assignment.enums.AssignmentStatus;
import com.sandbox.assessment.assignment.repository.AssignmentRepository;
import com.sandbox.assessment.assignment.statistics.dto.AssignmentStatisticsDto;
import com.sandbox.assessment.assignment.statistics.service.AssignmentStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * ==========================================================
 * Member 4
 * Assignment Statistics Service Implementation
 *
 * Provides dashboard statistics related to assignments.
 *
 * Independent module.
 * ==========================================================
 */

@Service
@RequiredArgsConstructor
public class AssignmentStatisticsServiceImpl
        implements AssignmentStatisticsService {

    private final AssignmentRepository assignmentRepository;

    /**
     * Returns overall assignment statistics.
     */
    @Override
    public AssignmentStatisticsDto getStatistics() {

        long totalAssignments = assignmentRepository.count();

        long assigned =
                assignmentRepository.countByStatus(
                        AssignmentStatus.ASSIGNED);

        long inProgress =
                assignmentRepository.countByStatus(
                        AssignmentStatus.IN_PROGRESS);

        long submitted =
                assignmentRepository.countByStatus(
                        AssignmentStatus.SUBMITTED);

        long evaluated =
                assignmentRepository.countByStatus(
                        AssignmentStatus.EVALUATED);

        return AssignmentStatisticsDto.builder()
                .totalAssignments(totalAssignments)
                .assigned(assigned)
                .inProgress(inProgress)
                .submitted(submitted)
                .evaluated(evaluated)
                .build();
    }
}