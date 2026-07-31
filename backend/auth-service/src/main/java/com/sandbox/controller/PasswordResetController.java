package com.sandbox.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sandbox.dto.ForgotPasswordRequest;
import com.sandbox.dto.ResetPasswordRequest;
import com.sandbox.dto.VerifyOtpRequest;
import com.sandbox.service.PasswordResetService;

import lombok.RequiredArgsConstructor;

/*
 * PASSWORD RESET CONTROLLER
 *
 * Exposes APIs required for:
 * - Send OTP
 * - Verify OTP
 * - Reset Password
 */
@RestController
@RequestMapping("/api/auth/password")
@RequiredArgsConstructor
public class PasswordResetController {

    // Handles forgot password business logic
    private final PasswordResetService passwordResetService;

    /*
     * STEP 1
     * Sends OTP to user's email.
     */
    @PostMapping("/forgot")
    public ResponseEntity<String> forgotPassword(
            @RequestBody ForgotPasswordRequest request) {

        passwordResetService.sendOtp(request);

        return ResponseEntity.ok(
                "OTP sent successfully."
        );
    }

    /*
     * STEP 2
     * Verifies OTP entered by user.
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(
            @RequestBody VerifyOtpRequest request) {

        passwordResetService.verifyOtp(request);

        return ResponseEntity.ok(
                "OTP verified successfully."
        );
    }

    /*
     * STEP 3
     * Changes user's password.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestBody ResetPasswordRequest request) {

        passwordResetService.resetPassword(request);

        return ResponseEntity.ok(
                "Password changed successfully."
        );
    }

}