package com.sandbox.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
 * SUBSCRIPTION RESPONSE DTO
 *
 * Purpose:
 * Defines the data returned to the frontend
 * when subscription information is requested.
 *
 * This DTO ensures that only required fields
 * are exposed to the client.
 *
 * Endpoints:
 *
 * GET  /admin/subscriptions
 * POST /admin/subscriptions
 * PUT  /admin/subscriptions/{id}
 *
 * Example Response:
 *
 * {
 *     "id": 1,
 *     "planName": "Basic",
 *     "description": "Suitable for startups",
 *     "durationMonths": 1,
 *     "price": 999.00,
 *     "maxCandidates": 50,
 *     "status": "ACTIVE",
 *     "createdAt": "2026-08-05T10:30:15"
 * }
 */
public class SubscriptionResponse {

    /*
     * UNIQUE SUBSCRIPTION ID
     */
    private Long id;


    /*
     * PLAN NAME
     *
     * Example:
     *
     * Basic
     * Pro
     * Enterprise
     */
    private String planName;


    /*
     * PLAN DESCRIPTION
     */
    private String description;


    /*
     * VALIDITY PERIOD
     *
     * Unit:
     * Months
     */
    private Integer durationMonths;


    /*
     * SUBSCRIPTION PRICE
     */
    private BigDecimal price;


    /*
     * MAXIMUM NUMBER OF CANDIDATES
     */
    private Integer maxCandidates;


    /*
     * CURRENT STATUS
     *
     * ACTIVE
     * INACTIVE
     */
    private String status;


    /*
     * DATE & TIME WHEN THE PLAN
     * WAS CREATED.
     */
    private LocalDateTime createdAt;


    /*
     * PARAMETERIZED CONSTRUCTOR
     *
     * Used while converting a
     * Subscription entity into
     * SubscriptionResponse.
     */
    public SubscriptionResponse(
            Long id,
            String planName,
            String description,
            Integer durationMonths,
            BigDecimal price,
            Integer maxCandidates,
            String status,
            LocalDateTime createdAt) {

        this.id = id;
        this.planName = planName;
        this.description = description;
        this.durationMonths = durationMonths;
        this.price = price;
        this.maxCandidates = maxCandidates;
        this.status = status;
        this.createdAt = createdAt;
    }


    /*
     * GETTERS
     */

    public Long getId() {
        return id;
    }

    public String getPlanName() {
        return planName;
    }

    public String getDescription() {
        return description;
    }

    public Integer getDurationMonths() {
        return durationMonths;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getMaxCandidates() {
        return maxCandidates;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

}