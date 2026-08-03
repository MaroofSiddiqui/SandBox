package com.sandbox.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/*
 * HR STATUS REQUEST
 *
 * Used to activate/deactivate an HR.
 *
 * Valid values:
 *
 * ACTIVE
 * INACTIVE
 */
public class HrStatusRequest {

    @NotBlank(message = "Status is required")

    @Pattern(
            regexp = "ACTIVE|INACTIVE",
            message = "Status must be ACTIVE or INACTIVE"
    )
    private String status;

    public HrStatusRequest() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}