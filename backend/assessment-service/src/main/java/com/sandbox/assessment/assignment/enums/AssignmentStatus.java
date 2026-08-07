package com.sandbox.assessment.assignment.enums;

/**
 * Represents the lifecycle of an assessment assignment.
 *
 * Flow:
 * ASSIGNED
 *      ↓
 * IN_PROGRESS
 *      ↓
 * SUBMITTED
 *      ↓
 * EVALUATED
 */
public enum AssignmentStatus {

    ASSIGNED,

    IN_PROGRESS,

    SUBMITTED,

    EVALUATED

}