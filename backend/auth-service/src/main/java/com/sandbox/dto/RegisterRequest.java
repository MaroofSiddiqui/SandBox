package com.sandbox.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/*
 * REGISTER REQUEST
 *
 * Contains information submitted during
 * public user registration.
 *
 * Security:
 * - Role is NOT accepted from the client.
 * - Organization is NOT accepted from the client.
 * - Backend decides the default role.
 */
public class RegisterRequest {

    // User's full name
    @NotBlank(message = "Name is required.")
    @Size(max = 100, message = "Name cannot exceed 100 characters.")
    private String name;

    // Unique email used for login
    @NotBlank(message = "Email is required.")
    @Email(message = "Invalid email format.")
    private String email;

    /*
     * PASSWORD
     *
     * Password requirements:
     *
     * - Minimum 8 characters
     * - At least one uppercase letter
     * - At least one lowercase letter
     * - At least one number
     * - At least one special character
     * - No whitespace
     */
    @NotBlank(message = "Password is required.")
    @Size(
        min = 8,
        max = 100,
        message = "Password must be between 8 and 100 characters."
    )
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9\\s])\\S+$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, one number, one special character, and no spaces."
    )
    private String password;


    public RegisterRequest() {
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }
}