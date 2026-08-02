package com.sandbox.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/*
 * RESEND VERIFICATION REQUEST
 *
 * Used when the user requests
 * another verification email.
 *
 * Example:
 *
 * {
 *     "email":"john@gmail.com"
 * }
 */

public class ResendVerificationRequest {

    /*
     * User email address.
     */
    @NotBlank(message = "Email is required.")
    @Email(message = "Invalid email format.")
    private String email;

    public ResendVerificationRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}