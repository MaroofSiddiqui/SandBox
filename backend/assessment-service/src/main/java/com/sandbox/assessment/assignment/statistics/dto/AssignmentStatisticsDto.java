package com.sandbox.assessment.assignment.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dashboard statistics for assignments.
 *
 * Member 4
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentStatisticsDto {

    private long totalAssignments;

    private long assigned;

    private long inProgress;

    private long submitted;

    private long evaluated;
}