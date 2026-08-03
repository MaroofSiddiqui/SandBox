package com.sandbox.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/*
 * RESET PASSWORD REQUEST
 *
 * Used after OTP verification.
 *
 * The new password must satisfy the same
 * security requirements as registration.
 */
public class ResetPasswordRequest {

    // Registered email
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    /*
     * NEW PASSWORD
     *
     * Requirements:
     *
     * - Minimum 8 characters
     * - At least one uppercase letter
     * - At least one lowercase letter
     * - At least one number
     * - At least one special character
     * - No spaces
     */
    @NotBlank(message = "Password is required")
    @Size(
        min = 8,
        message = "Password must be at least 8 characters"
    )
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9\\s])\\S+$",
        message = "Password must contain uppercase, lowercase, number, special character and no spaces"
    )
    private String newPassword;


    // Returns email
    public String getEmail() {
        return email;
    }

    // Sets email
    public void setEmail(String email) {
        this.email = email;
    }

    // Returns new password
    public String getNewPassword() {
        return newPassword;
    }

    // Sets new password
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}