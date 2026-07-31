package com.sandbox.service;

import com.sandbox.dto.ForgotPasswordRequest;
import com.sandbox.dto.ResetPasswordRequest;
import com.sandbox.dto.VerifyOtpRequest;

/*
 * PASSWORD RESET SERVICE
 *
 * Defines the password reset workflow.
 *
 * Implemented by:
 * PasswordResetServiceImpl
 */
public interface PasswordResetService {

    // Generates and emails an OTP
    void sendOtp(ForgotPasswordRequest request);

    // Validates the submitted OTP
    void verifyOtp(VerifyOtpRequest request);

    // Updates the user's password
    void resetPassword(ResetPasswordRequest request);

}