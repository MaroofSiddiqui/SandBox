package com.sandbox.service;

import com.sandbox.entity.User;

/*
 * EMAIL VERIFICATION SERVICE
 *
 * Handles the complete email verification workflow.
 *
 * Responsibilities:
 *
 * 1. Generate verification token
 * 2. Send verification email
 * 3. Verify verification token
 * 4. Resend verification email
 */
public interface EmailVerificationService {

    /*
     * Generates a verification token
     * and emails it to the user.
     */
    void sendVerificationEmail(User user);

    /*
     * Verifies the email using
     * the token received from email.
     */
    void verifyEmail(String token);

    /*
     * Generates a new verification email.
     */
    void resendVerificationEmail(String email);

}