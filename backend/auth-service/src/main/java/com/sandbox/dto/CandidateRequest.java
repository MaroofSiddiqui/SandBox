package com.sandbox.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/*
 * CANDIDATE REQUEST DTO
 *
 * Purpose:
 * This DTO defines the data that an HR must send
 * when creating a new candidate.
 *
 * Example request:
 *
 * POST /candidates
 *
 * {
 *     "name": "Aman Verma",
 *     "email": "aman.verma@acme.com",
 *     "password": "Candidate@123"
 * }
 *
 * DTO = Data Transfer Object
 *
 * We use a DTO instead of accepting the User entity directly
 * so that the client can provide only the fields that are
 * actually allowed during candidate creation.
 *
 * IMPORTANT:
 * There is NO role or organizationId field here.
 *
 * The backend automatically assigns:
 * role         -> CANDIDATE
 * organization -> Logged-in HR's organization
 *
 * This prevents an HR from choosing another role or creating
 * a candidate inside another organization.
 */
public class CandidateRequest {

    /*
     * CANDIDATE NAME
     *
     * @NotBlank rejects:
     * null
     * ""
     * "   "
     *
     * @Size ensures that the name cannot exceed
     * the database/application limit of 100 characters.
     */
    @NotBlank(message = "Name is required")
    @Size(
        max = 100,
        message = "Name must not exceed 100 characters"
    )
    private String name;


    /*
     * CANDIDATE EMAIL
     *
     * @NotBlank:
     * Email must be provided.
     *
     * @Email:
     * Checks whether the supplied value has a valid
     * email-like format.
     *
     * @Size:
     * Prevents emails longer than 150 characters.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(
        max = 150,
        message = "Email must not exceed 150 characters"
    )
    private String email;


    /*
     * CANDIDATE PASSWORD
     *
     * @NotBlank prevents empty, null, or whitespace-only passwords.
     *
     * @Size(min = 6) requires at least 6 characters.
     *
     * IMPORTANT:
     * This password is received temporarily as plain text here.
     * CandidateService passes it through PasswordEncoder before
     * saving the candidate.
     *
     * Therefore the database stores passwordHash,
     * NOT this plain-text password.
     */
    @NotBlank(message = "Password is required")
    @Size(
        min = 6,
        message = "Password must be at least 6 characters"
    )
    private String password;


    /*
     * NO-ARGUMENT CONSTRUCTOR
     *
     * Required by frameworks such as Jackson to create
     * this Java object while converting incoming JSON
     * into CandidateRequest.
     *
     * JSON
     *   ↓
     * Jackson
     *   ↓
     * CandidateRequest object
     */
    public CandidateRequest() {
    }


    /*
     * PARAMETERIZED CONSTRUCTOR
     *
     * Allows us to manually create CandidateRequest
     * objects by supplying all three values.
     *
     * This can also be useful during unit testing.
     */
    public CandidateRequest(
            String name,
            String email,
            String password) {

        this.name = name;
        this.email = email;
        this.password = password;
    }


    /*
     * GETTERS AND SETTERS
     *
     * Jackson and other parts of the application use these
     * methods to read and write DTO values.
     */

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