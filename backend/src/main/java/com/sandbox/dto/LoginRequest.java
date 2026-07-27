package com.sandbox.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/*
 * LOGIN REQUEST DTO
 *
 * Purpose:
 * This DTO represents the data sent by a user
 * when trying to log in to the application.
 *
 * Endpoint:
 * POST /auth/login
 *
 * Example request:
 *
 * {
 *     "email": "rahul.hr@acme.com",
 *     "password": "Hr@12345"
 * }
 *
 * This DTO is used only for receiving and validating
 * login credentials.
 *
 * It does NOT perform authentication itself.
 * The actual authentication logic is handled by AuthService.
 */
public class LoginRequest {

    /*
     * USER EMAIL
     *
     * The email is currently used as the username/
     * unique identifier during authentication.
     *
     * @NotBlank ensures the email:
     *
     * - Is not null
     * - Is not empty ("")
     * - Is not only spaces ("   ")
     *
     * @Email checks whether the supplied value
     * has a valid email format.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;


    /*
     * USER PASSWORD
     *
     * @NotBlank ensures that a password must be supplied.
     *
     * The password received here is plain text temporarily.
     *
     * IMPORTANT:
     * We do NOT compare this password directly with the
     * password_hash stored in the database.
     *
     * AuthService uses PasswordEncoder.matches():
     *
     * entered password
     *       ↓
     * PasswordEncoder.matches()
     *       ↓
     * BCrypt hash stored in database
     *
     * BCrypt determines whether they match.
     */
    @NotBlank(message = "Password is required")
    private String password;


    /*
     * NO-ARGUMENT CONSTRUCTOR
     *
     * Jackson uses this constructor when converting
     * incoming JSON into a LoginRequest object.
     *
     * JSON
     *   ↓
     * Jackson
     *   ↓
     * LoginRequest
     */
    public LoginRequest() {
    }


    /*
     * GETTERS AND SETTERS
     *
     * These methods allow Jackson and other application
     * components to access and populate the DTO fields.
     */

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