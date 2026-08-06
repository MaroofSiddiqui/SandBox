package com.sandbox.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/*
 * SUBSCRIPTION REQUEST DTO
 *
 * Purpose:
 * Defines the data required for creating
 * or updating a subscription plan.
 *
 * Endpoint:
 *
 * POST /admin/subscriptions
 * PUT  /admin/subscriptions/{id}
 *
 * Example Request:
 *
 * {
 *     "planName": "Basic",
 *     "description": "Suitable for startups",
 *     "durationMonths": 1,
 *     "price": 999.00,
 *     "maxCandidates": 50
 * }
 *
 * Validation annotations ensure that invalid
 * data is rejected before reaching the Service layer.
 */
public class SubscriptionRequest {

    /*
     * PLAN NAME
     *
     * Examples:
     *
     * Basic
     * Pro
     * Enterprise
     *
     * Cannot be null, empty, or only spaces.
     */
    @NotBlank(message = "Plan name is required")
    @Size(max = 100, message = "Plan name cannot exceed 100 characters")
    private String planName;


    /*
     * PLAN DESCRIPTION
     *
     * Brief description of the subscription.
     *
     * Example:
     *
     * "Suitable for startups"
     *
     * Maximum length:
     * 500 characters.
     */
    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;


    /*
     * DURATION
     *
     * Represents subscription validity
     * in months.
     *
     * Examples:
     *
     * 1
     * 3
     * 6
     * 12
     */
    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be greater than zero")
    private Integer durationMonths;


    /*
     * PRICE
     *
     * Monthly or package price.
     *
     * Example:
     *
     * 999.00
     * 2999.00
     */
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Price must be greater than zero")
    private BigDecimal price;


    /*
     * MAXIMUM CANDIDATES
     *
     * Number of candidates allowed
     * under this subscription.
     *
     * Example:
     *
     * 50
     * 500
     */
    @NotNull(message = "Maximum candidates is required")
    @Positive(message = "Maximum candidates must be greater than zero")
    private Integer maxCandidates;


    /*
     * NO-ARGUMENT CONSTRUCTOR
     *
     * Required by Jackson for converting
     * incoming JSON into a SubscriptionRequest.
     */
    public SubscriptionRequest() {
    }


    /*
     * GETTERS AND SETTERS
     */

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Integer getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(Integer durationMonths) {
        this.durationMonths = durationMonths;
    }


    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }


    public Integer getMaxCandidates() {
        return maxCandidates;
    }

    public void setMaxCandidates(Integer maxCandidates) {
        this.maxCandidates = maxCandidates;
    }

}