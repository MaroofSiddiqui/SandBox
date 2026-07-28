package com.sandbox.exception;

/*
 * RESOURCE NOT FOUND EXCEPTION
 *
 * Purpose:
 * This is a custom exception used when the application
 * tries to find a resource that does not exist.
 *
 * Examples:
 *
 * - Candidate ID does not exist
 * - Organization ID does not exist
 *
 * Instead of using a generic exception, we use this
 * specific exception so GlobalExceptionHandler can
 * recognize it and return:
 *
 * HTTP 404 Not Found
 */
public class ResourceNotFoundException extends RuntimeException {

    /*
     * Constructor for the custom exception.
     *
     * It receives the specific error message that
     * describes which resource was not found.
     *
     * Examples:
     *
     * "Candidate not found"
     * "Organization not found"
     */
    public ResourceNotFoundException(String message) {

        /*
         * super(message) calls the constructor of
         * the parent RuntimeException class.
         *
         * This stores the supplied message inside
         * the exception.
         *
         * Later, GlobalExceptionHandler can retrieve it using:
         *
         * ex.getMessage()
         */
        super(message);
    }
}