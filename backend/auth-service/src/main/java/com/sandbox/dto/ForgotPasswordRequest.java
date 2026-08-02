package com.sandbox.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/*
 * FORGOT PASSWORD REQUEST
 *
 * Used when user requests an OTP.
 */
public class ForgotPasswordRequest {

    // Registered email address
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    // Returns email
    public String getEmail() {
        return email;
    }

    // Sets email
    public void setEmail(String email) {
        this.email = email;
    }

}