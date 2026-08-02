package com.sandbox.mail;

/**
 * Represents every email template supported by the application.
 */
public enum EmailTemplateType {

    // New account verification.
    EMAIL_VERIFICATION,

    // Forgot password OTP.
    PASSWORD_RESET_OTP,

    // Password changed successfully.
    PASSWORD_CHANGED,

    // Welcome email after successful verification.
    WELCOME,

    // Security alert after multiple failed logins.
    SECURITY_ALERT

}