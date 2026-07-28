package com.sandbox.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/*
 * CREATE HR REQUEST DTO
 *
 * Purpose:
 * This DTO defines the data that SUPER_ADMIN must send
 * when creating a new HR user.
 *
 * Endpoint:
 * POST /hrs
 *
 * Example request:
 *
 * {
 *     "name": "Rahul Sharma",
 *     "email": "rahul.hr@acme.com",
 *     "password": "Hr@12345",
 *     "organizationId": 2
 * }
 *
 * The DTO also contains validation rules to reject
 * invalid request data before it reaches HrService.
 */
public class CreateHrRequest {

    /*
     * HR NAME
     *
     * @NotBlank ensures that the name:
     *
     * - Is not null
     * - Is not empty ("")
     * - Is not only spaces ("   ")
     */
    @NotBlank(message = "Name is required")
    private String name;


    /*
     * HR EMAIL
     *
     * @NotBlank:
     * Email must be provided.
     *
     * @Email:
     * Checks whether the supplied value has
     * a valid email format.
     *
     * HrService additionally checks whether this
     * email already exists in the database.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;


    /*
     * HR PASSWORD
     *
     * @NotBlank:
     * Password cannot be null, empty, or only spaces.
     *
     * @Size(min = 8):
     * Requires the password to contain at least 8 characters.
     *
     * IMPORTANT:
     * This plain-text password is NOT stored directly
     * in the database.
     *
     * HrService uses PasswordEncoder to convert it into
     * a BCrypt hash before saving the HR.
     */
    @NotBlank(message = "Password is required")
    @Size(
        min = 8,
        message = "Password must be at least 8 characters"
    )
    private String password;


    /*
     * ORGANIZATION ID
     *
     * Specifies which organization this HR should belong to.
     *
     * Example:
     *
     * organizationId = 2
     *
     * HrService uses this ID to find the Organization entity
     * from the database and assign it to the new HR.
     *
     * @NotNull ensures that SUPER_ADMIN must select/provide
     * an organization when creating an HR.
     */
    @NotNull(message = "Organization ID is required")
    private Long organizationId;


    /*
     * NO-ARGUMENT CONSTRUCTOR
     *
     * Jackson uses this constructor while converting
     * incoming JSON into a CreateHrRequest object.
     *
     * JSON
     *   ↓
     * Jackson
     *   ↓
     * CreateHrRequest
     */
    public CreateHrRequest() {
    }


    /*
     * GETTERS AND SETTERS
     *
     * These methods allow the application/framework
     * to read and modify the DTO fields.
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


    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}