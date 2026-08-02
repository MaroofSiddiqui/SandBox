package com.sandbox.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sandbox.dto.ResendVerificationRequest;
import com.sandbox.service.EmailVerificationService;

import jakarta.validation.Valid;

/*
 * EMAIL VERIFICATION CONTROLLER
 *
 * Exposes APIs for:
 * - Verifying email using verification link
 * - Resending verification email
 */
@RestController
@RequestMapping("/api/auth/email")
public class EmailVerificationController {

    // Handles email verification business logic
    private final EmailVerificationService emailVerificationService;

    /*
     * Constructor Injection
     */
    public EmailVerificationController(
            EmailVerificationService emailVerificationService) {

        this.emailVerificationService = emailVerificationService;
    }

    /*
     * VERIFY EMAIL
     *
     * Called when the user clicks the verification link
     * received through email.
     *
     * Example:
     *
     * GET /api/auth/email/verify?token=abc123
     */
    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(
            @RequestParam String token) {

        emailVerificationService.verifyEmail(token);

        return ResponseEntity.ok(
                "Email verified successfully."
        );
    }

    /*
     * RESEND VERIFICATION EMAIL
     *
     * Generates a new verification token and
     * sends a fresh verification email.
     */
    @PostMapping("/resend")
    public ResponseEntity<String> resendVerificationEmail(
            @Valid @RequestBody ResendVerificationRequest request) {

        emailVerificationService.resendVerificationEmail(
                request.getEmail()
        );

        return ResponseEntity.ok(
                "Verification email sent successfully."
        );
    }
}