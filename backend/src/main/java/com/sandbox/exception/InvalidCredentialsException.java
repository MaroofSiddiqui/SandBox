package com.sandbox.exception;

/*
 * INVALID CREDENTIALS EXCEPTION
 *
 * Purpose:
 * This is a custom exception used when a user's
 * login credentials are incorrect.
 *
 * Example situations:
 *
 * - Email does not exist
 * - Password is incorrect
 *
 * Instead of throwing a generic exception, we use
 * a specific exception so our GlobalExceptionHandler
 * can recognize authentication failures and return:
 *
 * HTTP 401 Unauthorized
 */
public class InvalidCredentialsException extends RuntimeException {

    /*
     * Constructor for the custom exception.
     *
     * It accepts an error message such as:
     *
     * "Invalid email or password"
     *
     * and passes that message to RuntimeException
     * using super(message).
     */
    public InvalidCredentialsException(String message) {

        /*
         * super(message) calls the constructor of
         * the parent class RuntimeException.
         *
         * This stores the message inside the exception,
         * which can later be retrieved using:
         *
         * ex.getMessage()
         *
         * GlobalExceptionHandler uses that message
         * in the API response.
         */
        super(message);
    }
}