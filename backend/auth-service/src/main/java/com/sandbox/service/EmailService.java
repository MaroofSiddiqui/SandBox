package com.sandbox.service;

/*
 * EMAIL SERVICE
 *
 * Responsible for sending emails.
 *
 * Supported email types:
 * - OTP
 * - Email Verification
 * - Password Reset
 * - Future Notifications
 */
public interface EmailService {

    /*
     * Sends an HTML email.
     *
     * to -> Receiver email
     * subject -> Email subject
     * html -> HTML body
     */
    void sendEmail(
            String to,
            String subject,
            String html
    );

}