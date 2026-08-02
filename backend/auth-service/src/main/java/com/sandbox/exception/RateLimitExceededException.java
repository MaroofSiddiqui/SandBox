package com.sandbox.exception;

/*
 * RATE LIMIT EXCEEDED EXCEPTION
 *
 * Thrown when too many login requests
 * are received within the configured time window.
 */
public class RateLimitExceededException extends RuntimeException {

    public RateLimitExceededException(String message) {
        super(message);
    }
}