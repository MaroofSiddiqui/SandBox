package com.sandbox.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/*
 * ORGANIZATION REQUEST
 *
 * Used when SUPER_ADMIN creates or updates an organization.
 *
 * The client is allowed to provide:
 * - name
 * - domain
 *
 * The client does NOT control:
 * - id
 * - status
 * - createdBy
 * - createdAt
 */
public class OrganizationRequest {

    @NotBlank(message = "Organization name is required.")
    @Size(
        max = 150,
        message = "Organization name cannot exceed 150 characters."
    )
    private String name;

    @NotBlank(message = "Organization domain is required.")
    @Size(
        max = 150,
        message = "Organization domain cannot exceed 150 characters."
    )
    @Pattern(
        regexp = "^(?!-)(?:[A-Za-z0-9-]{1,63}\\.)+[A-Za-z]{2,63}$",
        message = "Enter a valid organization domain."
    )
    private String domain;

    public OrganizationRequest() {
    }

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