package com.sandbox.dto;

import java.time.LocalDateTime;

/*
 * CANDIDATE RESPONSE DTO
 *
 * Purpose:
 * This DTO defines the candidate information that is safely
 * returned from the backend to the client/frontend.
 *
 * It is used for responses from APIs such as:
 *
 * POST /candidates
 * GET  /candidates
 * GET  /candidates/{id}
 *
 * Instead of returning the complete User entity, we return
 * only the candidate information that the client needs.
 *
 * IMPORTANT:
 * Sensitive information such as passwordHash is NOT included.
 */
public class CandidateResponse {

    /*
     * Unique database ID of the candidate.
     *
     * Example:
     * 3
     */
    private Long id;


    /*
     * Candidate's full name.
     *
     * Example:
     * "Aman Verma"
     */
    private String name;


    /*
     * Candidate's email address.
     *
     * Example:
     * "aman.verma@acme.com"
     */
    private String email;


    /*
     * Role assigned to this user.
     *
     * For users returned through this DTO,
     * this should normally be:
     *
     * "CANDIDATE"
     */
    private String role;


    /*
     * ID of the organization to which
     * this candidate belongs.
     *
     * Example:
     *
     * organizationId = 2
     *
     * This allows the frontend to know the candidate's
     * organization without returning the complete
     * Organization entity.
     */
    private Long organizationId;


    /*
     * Current account status of the candidate.
     *
     * Example:
     *
     * ACTIVE
     * INACTIVE
     */
    private String status;


    /*
     * Date and time when the candidate account
     * was originally created.
     *
     * This value comes from User.createdAt.
     */
    private LocalDateTime createdAt;


    /*
     * NO-ARGUMENT CONSTRUCTOR
     *
     * Allows frameworks such as Jackson to create
     * CandidateResponse objects when required.
     */
    public CandidateResponse() {
    }


    /*
     * PARAMETERIZED CONSTRUCTOR
     *
     * Used to conveniently create a CandidateResponse
     * containing all safe candidate information.
     *
     * CandidateService currently uses this constructor
     * inside its toResponse() method.
     */
    public CandidateResponse(
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
     * GETTERS AND SETTERS
     *
     * Getters allow Spring/Jackson to read these values
     * and convert the object into JSON.
     *
     * Setters allow the values to be modified when needed.
     */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}