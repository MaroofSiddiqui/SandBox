package com.sandbox.assessment.assignment.exception;

/**
 * ==========================================================
 * Member 4
 * Custom Exception
 *
 * Thrown when the same assessment is assigned
 * multiple times to the same candidate.
 * ==========================================================
 */
public class DuplicateAssignmentException extends RuntimeException {

    public DuplicateAssignmentException(String message) {
        super(message);
    }
}