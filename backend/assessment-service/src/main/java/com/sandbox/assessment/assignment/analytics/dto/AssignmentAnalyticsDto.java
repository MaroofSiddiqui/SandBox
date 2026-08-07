package com.sandbox.assessment.assignment.analytics.dto;

import lombok.*;

/**
 * Assignment Dashboard Statistics
 *
 * Member 4
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentAnalyticsDto {

    private Long totalAssignments;

    private Long assigned;

    private Long inProgress;

    private Long submitted;

    private Long evaluated;

}