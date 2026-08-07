package com.sandbox.assessment.assignment.exception;

/**
 * ==========================================================
 * Member 4
 * Custom Exception
 *
 * Thrown when an assignment is not found.
 * ==========================================================
 */
public class AssignmentNotFoundException extends RuntimeException {

    public AssignmentNotFoundException(String message) {
        super(message);
    }
}