package com.sandbox.dto;

import java.time.LocalDateTime;

/*
 * USER RESPONSE DTO
 *
 * Purpose:
 * This DTO defines the user information that can be
 * safely returned from the backend to the client.
 *
 * Currently, it is used when SUPER_ADMIN creates an HR:
 *
 * POST /hrs
 *
 * Instead of returning the complete User entity,
 * we convert it into UserResponse.
 *
 * This prevents sensitive information such as
 * passwordHash from being exposed in the API response.
 */
public class UserResponse {

    /*
     * Unique database ID of the user.
     *
     * Example:
     * 2
     */
    private Long id;


    /*
     * User's full name.
     *
     * Example:
     * "Rahul Sharma"
     */
    private String name;


    /*
     * User's email address.
     *
     * Example:
     * "rahul.hr@acme.com"
     */
    private String email;


    /*
     * User's role as a simple String.
     *
     * Examples:
     * SUPER_ADMIN
     * HR
     * CANDIDATE
     *
     * Instead of returning the complete Role entity,
     * we return only the role name.
     */
    private String role;


    /*
     * ID of the organization associated with the user.
     *
     * Example:
     *
     * HR:
     * organizationId = 2
     *
     * Candidate:
     * organizationId = 2
     *
     * SUPER_ADMIN may have:
     * organizationId = null
     *
     * because SUPER_ADMIN is not tied to one organization.
     */
    private Long organizationId;


    /*
     * Current account status.
     *
     * Example:
     * ACTIVE
     * INACTIVE
     */
    private String status;


    /*
     * Date and time when the user account was created.
     *
     * This value originally comes from User.createdAt.
     */
    private LocalDateTime createdAt;


    /*
     * PARAMETERIZED CONSTRUCTOR
     *
     * Used to create a UserResponse containing
     * all safe user information.
     *
     * For example, HrController uses this after
     * HrService creates a new HR.
     */
    public UserResponse(
            Long id,
            String name,
            String email,
            String role,
            Long organizationId,
            String status,
            LocalDateTime createdAt) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.organizationId = organizationId;
        this.status = status;
        this.createdAt = createdAt;
    }


    /*
     * GETTERS
     *
     * These methods allow Spring/Jackson to read
     * the field values and convert this object into JSON.
     *
     * There are currently no setters because this DTO
     * is only being used as an output/response object.
     */

    public Long getId() {
        return id;
    }


    public String getName() {
        return name;
    }


    public String getEmail() {
        return email;
    }


    public String getRole() {
        return role;
    }


    public Long getOrganizationId() {
        return organizationId;
    }


    public String getStatus() {
        return status;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}