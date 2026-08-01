package com.sandbox.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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

    // Plain password received only during registration
    @NotBlank(message = "Password is required.")
    @Size(min = 8, message = "Password must contain at least 8 characters.")
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