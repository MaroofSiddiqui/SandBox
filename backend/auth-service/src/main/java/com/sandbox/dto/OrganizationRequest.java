package com.sandbox.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/*
 * ORGANIZATION REQUEST DTO
 *
 * Purpose:
 * This DTO represents the data sent by SUPER_ADMIN
 * when creating or updating an organization.
 *
 * It is currently used by:
 *
 * POST /organizations
 * -> Create a new organization
 *
 * PUT /organizations/{id}
 * -> Update an existing organization
 *
 * Example request:
 *
 * {
 *     "name": "Acme Technologies",
 *     "domain": "acme.com"
 * }
 *
 * Using a DTO prevents the client from directly sending
 * or modifying fields of the Organization entity that
 * should be controlled by the backend.
 *
 * For example:
 * - id
 * - status
 * - createdBy
 * - createdAt
 */
public class OrganizationRequest {

    /*
     * ORGANIZATION NAME
     *
     * @NotBlank ensures that the organization name:
     *
     * - Is not null
     * - Is not empty ("")
     * - Is not only spaces ("   ")
     *
     * @Size(max = 150) ensures the name does not exceed
     * the maximum length supported by the database column.
     */
    @NotBlank(message = "Organization name is required")
    @Size(
        max = 150,
        message = "Organization name must not exceed 150 characters"
    )
    private String name;


    /*
     * ORGANIZATION DOMAIN
     *
     * Example:
     *
     * acme.com
     * google.com
     *
     * @NotBlank makes the domain mandatory.
     *
     * @Size(max = 150) prevents excessively long
     * domain values.
     *
     * OrganizationService also normalizes the domain
     * using trim() and toLowerCase() before storing it.
     *
     * It also checks whether the domain already exists
     * because organization domains should be unique.
     */
    @NotBlank(message = "Domain is required")
    @Size(
        max = 150,
        message = "Domain must not exceed 150 characters"
    )
    private String domain;


    /*
     * NO-ARGUMENT CONSTRUCTOR
     *
     * Jackson uses this constructor when converting
     * incoming JSON into an OrganizationRequest object.
     *
     * JSON
     *   ↓
     * Jackson
     *   ↓
     * OrganizationRequest
     */
    public OrganizationRequest() {
    }


    /*
     * PARAMETERIZED CONSTRUCTOR
     *
     * Allows us to manually create an OrganizationRequest
     * by supplying name and domain.
     *
     * This can also be useful in unit tests.
     */
    public OrganizationRequest(
            String name,
            String domain) {

        this.name = name;
        this.domain = domain;
    }


    /*
     * GETTERS AND SETTERS
     *
     * Used by Jackson and other parts of the application
     * to read and modify the DTO fields.
     */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}