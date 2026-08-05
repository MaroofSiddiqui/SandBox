package com.sandbox.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*
 * CURRENT SUBSCRIPTION RESPONSE
 *
 * Returned to an HR when requesting the current
 * subscription of their organization.
 */
@Getter
@AllArgsConstructor
public class CurrentSubscriptionResponse {

    private Long organizationId;
    private String organizationName;

    private Long subscriptionId;
    private String planName;
    private String description;

    private BigDecimal price;
    private Integer durationMonths;
    private Integer maxCandidates;

    private LocalDateTime subscriptionStartAt;
    private LocalDateTime subscriptionExpiresAt;

    /*
     * Indicates whether the subscription is
     * currently valid.
     */
    private boolean active;
}