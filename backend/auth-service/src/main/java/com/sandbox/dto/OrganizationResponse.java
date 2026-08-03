package com.sandbox.dto;

import java.time.LocalDateTime;

/*
 * ORGANIZATION RESPONSE
 *
 * Safe representation of Organization returned by the API.
 *
 * Returning DTOs instead of JPA entities prevents
 * accidental exposure of entity relationships/internal data.
 */
public class OrganizationResponse {

    private Long id;
    private String name;
    private String domain;
    private String status;
    private Long createdBy;
    private LocalDateTime createdAt;

    public OrganizationResponse() {
    }

    public OrganizationResponse(
            Long id,
            String name,
            String domain,
            String status,
            Long createdBy,
            LocalDateTime createdAt) {

        this.id = id;
        this.name = name;
        this.domain = domain;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

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

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}