package com.sandbox.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/*
 * VERIFY OTP REQUEST
 *
 * Used when user enters the OTP.
 */
public class VerifyOtpRequest {

    // Registered email
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    // Six digit OTP
    @NotBlank(message = "OTP is required")
    private String otp;

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

}