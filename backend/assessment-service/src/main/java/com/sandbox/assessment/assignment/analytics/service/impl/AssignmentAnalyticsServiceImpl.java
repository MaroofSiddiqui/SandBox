package com.sandbox.assessment.assignment.analytics.service.impl;

import com.sandbox.assessment.assignment.analytics.dto.AssignmentAnalyticsDto;
import com.sandbox.assessment.assignment.analytics.service.AssignmentAnalyticsService;
import com.sandbox.assessment.assignment.enums.AssignmentStatus;
import com.sandbox.assessment.assignment.repository.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Dashboard Statistics
 *
 * Member 4
 */
@Service
@RequiredArgsConstructor
public class AssignmentAnalyticsServiceImpl
        implements AssignmentAnalyticsService {

    private final AssignmentRepository assignmentRepository;

    @Override
    public AssignmentAnalyticsDto getDashboard() {

        return AssignmentAnalyticsDto.builder()

                .totalAssignments(
                        assignmentRepository.count())

                .assigned(
                        assignmentRepository.countByStatus(
                                AssignmentStatus.ASSIGNED))

                .inProgress(
                        assignmentRepository.countByStatus(
                                AssignmentStatus.IN_PROGRESS))

                .submitted(
                        assignmentRepository.countByStatus(
                                AssignmentStatus.SUBMITTED))

                .evaluated(
                        assignmentRepository.countByStatus(
                                AssignmentStatus.EVALUATED))

                .build();
    }

}