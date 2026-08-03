package com.sandbox.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sandbox.dto.ForgotPasswordRequest;
import com.sandbox.dto.ResetPasswordRequest;
import com.sandbox.dto.VerifyOtpRequest;
import com.sandbox.service.PasswordResetService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/*
 * PASSWORD RESET CONTROLLER
 *
 * Exposes APIs required for:
 *
 * 1. Send OTP
 * 2. Verify OTP
 * 3. Reset Password
 */
@RestController
@RequestMapping("/api/auth/password")
@RequiredArgsConstructor
public class PasswordResetController {

    // Handles password reset business logic
    private final PasswordResetService passwordResetService;


    /*
     * STEP 1
     * SEND OTP
     *
     * POST:
     * /api/auth/password/forgot
     */
    @PostMapping("/forgot")
    public ResponseEntity<String> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        passwordResetService.sendOtp(request);

        return ResponseEntity.ok(
                "OTP sent successfully."
        );
    }


    /*
     * STEP 2
     * VERIFY OTP
     *
     * POST:
     * /api/auth/password/verify-otp
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {

        passwordResetService.verifyOtp(request);

        return ResponseEntity.ok(
                "OTP verified successfully."
        );
    }


    /*
     * STEP 3
     * RESET PASSWORD
     *
     * POST:
     * /api/auth/password/reset-password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        passwordResetService.resetPassword(request);

        return ResponseEntity.ok(
                "Password changed successfully."
        );
    }
}