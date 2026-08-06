package com.sandbox.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

/*
 * SUBSCRIPTION ENTITY
 *
 * Purpose:
 * Represents a subscription plan that organizations
 * can purchase to use the SandBox platform.
 *
 * Examples:
 *
 * Basic
 * Pro
 * Enterprise
 *
 * Each subscription defines:
 *
 * - Plan name
 * - Description
 * - Duration (months)
 * - Price
 * - Maximum candidates allowed
 * - Status (ACTIVE / INACTIVE)
 * - Creation timestamp
 *
 * Database Table:
 *
 * subscriptions
 */
@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    /*
     * PRIMARY KEY
     *
     * Database-generated unique identifier.
     *
     * Example:
     *
     * id = 1
     * id = 2
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    /*
     * PLAN NAME
     *
     * Examples:
     *
     * Basic
     * Pro
     * Enterprise
     *
     * Required field.
     */
    @Column(nullable = false)
    private String planName;


    /*
     * PLAN DESCRIPTION
     *
     * Describes the subscription plan.
     *
     * Example:
     *
     * "Suitable for startups"
     * "Best plan for medium companies"
     *
     * Maximum length:
     * 500 characters.
     */
    @Column(length = 500)
    private String description;


    /*
     * SUBSCRIPTION DURATION
     *
     * Represents how long the subscription
     * remains valid.
     *
     * Examples:
     *
     * 1
     * 3
     * 6
     * 12
     *
     * Unit:
     * Months
     */
    @Column(nullable = false)
    private Integer durationMonths;


    /*
     * SUBSCRIPTION PRICE
     *
     * Stored using BigDecimal because monetary
     * values should never use float or double.
     *
     * Examples:
     *
     * 999.00
     * 2999.00
     * 9999.00
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;


    /*
     * MAXIMUM CANDIDATES
     *
     * Defines how many candidates an organization
     * can manage under this subscription.
     *
     * Example:
     *
     * Basic       -> 50
     * Pro         -> 500
     * Enterprise  -> 999999
     */
    @Column(nullable = false)
    private Integer maxCandidates;


    /*
     * SUBSCRIPTION STATUS
     *
     * ACTIVE
     * INACTIVE
     *
     * New plans are ACTIVE by default.
     */
    @Column(nullable = false)
    private String status;


    /*
     * CREATION TIMESTAMP
     *
     * Automatically stores the date and time
     * when the subscription plan is created.
     *
     * Example:
     *
     * 2026-08-05T11:15:30
     */
    private LocalDateTime createdAt;


    /*
     * PRE-PERSIST CALLBACK
     *
     * Executed automatically by JPA before
     * inserting a new subscription record.
     *
     * Responsibilities:
     *
     * 1. Set the creation timestamp.
     * 2. Default the status to ACTIVE if
     *    it has not been explicitly provided.
     */
    @PrePersist
    public void prePersist() {

        /*
         * Store current date and time.
         */
        createdAt = LocalDateTime.now();

        /*
         * Assign default status.
         */
        if (status == null) {
            status = "ACTIVE";
        }

    }

}