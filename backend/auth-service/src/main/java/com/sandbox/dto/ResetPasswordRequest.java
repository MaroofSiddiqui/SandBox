package com.sandbox.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/*
 * RESET PASSWORD REQUEST
 *
 * Used after OTP verification.
 */
public class ResetPasswordRequest {

    // Registered email
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    // Verified OTP
    @NotBlank(message = "OTP is required")
    private String otp;

    // New password
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String newPassword;

    // Returns email
    public String getEmail() {
        return email;
    }

    // Sets email
    public void setEmail(String email) {
        this.email = email;
    }

    // Returns OTP
    public String getOtp() {
        return otp;
    }

    // Sets OTP
    public void setOtp(String otp) {
        this.otp = otp;
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