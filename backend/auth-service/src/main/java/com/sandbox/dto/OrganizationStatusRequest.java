package com.sandbox.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/*
 * ORGANIZATION STATUS REQUEST DTO
 *
 * Purpose:
 * This DTO is used when SUPER_ADMIN wants to change
 * only the status of an organization.
 *
 * Endpoint:
 *
 * PATCH /organizations/{id}/status
 *
 * Example request:
 *
 * {
 *     "status": "INACTIVE"
 * }
 *
 * We use a separate DTO because this API only needs
 * the status field. The client does not need to send
 * organization name, domain, or other information.
 */
public class OrganizationStatusRequest {

    /*
     * ORGANIZATION STATUS
     *
     * @NotBlank ensures that status:
     *
     * - Is not null
     * - Is not empty ("")
     * - Is not only spaces ("   ")
     */
    @NotBlank(message = "Status is required")

    /*
     * @Pattern restricts the allowed values.
     *
     * regexp = "ACTIVE|INACTIVE"
     *
     * means the value must be exactly either:
     *
     * ACTIVE
     * or
     * INACTIVE
     *
     * Values such as:
     *
     * "DELETED"
     * "BLOCKED"
     * "active"
     *
     * will fail validation.
     *
     * If validation fails, our GlobalExceptionHandler
     * returns HTTP 400 Bad Request.
     */
    @Pattern(
        regexp = "ACTIVE|INACTIVE",
        message = "Status must be ACTIVE or INACTIVE"
    )
    private String status;


    /*
     * NO-ARGUMENT CONSTRUCTOR
     *
     * Jackson uses this constructor while converting
     * incoming JSON into OrganizationStatusRequest.
     *
     * JSON:
     *
     * {
     *     "status": "INACTIVE"
     * }
     *
     *          ↓
     *
     * OrganizationStatusRequest object
     */
    public OrganizationStatusRequest() {
    }


    /*
     * PARAMETERIZED CONSTRUCTOR
     *
     * Allows us to manually create the DTO by
     * directly providing a status.
     *
     * Example:
     *
     * new OrganizationStatusRequest("ACTIVE");
     */
    public OrganizationStatusRequest(String status) {
        this.status = status;
    }


    /*
     * GETTER
     *
     * Used to retrieve the requested status.
     *
     * OrganizationController uses:
     *
     * request.getStatus()
     *
     * and passes the value to OrganizationService.
     */
    public String getStatus() {
        return status;
    }


    /*
     * SETTER
     *
     * Allows Jackson/framework code to populate
     * the status field from incoming JSON.
     */
    public void setStatus(String status) {
        this.status = status;
    }
}