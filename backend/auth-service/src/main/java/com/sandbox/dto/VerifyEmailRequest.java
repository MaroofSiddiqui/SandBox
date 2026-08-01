package com.sandbox.dto;

import jakarta.validation.constraints.NotBlank;

/*
 * VERIFY EMAIL REQUEST
 *
 * Used when the frontend sends the email
 * verification token to the backend.
 *
 * Example:
 *
 * {
 *     "token":"ab34cd..."
 * }
 */

public class VerifyEmailRequest {

    /*
     * Verification token received
     * from the email link.
     */
    @NotBlank(message = "Verification token is required.")
    private String token;

    public VerifyEmailRequest() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}