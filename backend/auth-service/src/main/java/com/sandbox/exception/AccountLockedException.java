package com.sandbox.exception;

/*
 * ACCOUNT LOCKED EXCEPTION
 *
 * Thrown when a user attempts to log in while their
 * account is temporarily locked because of repeated
 * failed login attempts.
 *
 * Example:
 *
 * 5 incorrect passwords
 *        ↓
 * Account locked for 30 minutes
 *        ↓
 * Further login attempt
 *        ↓
 * AccountLockedException
 */
public class AccountLockedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AccountLockedException(String message) {
        super(message);
    }
}