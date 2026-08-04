package com.sandbox.dto;

import lombok.*;

/*
 * CREATE PAYMENT ORDER REQUEST
 *
 * Purpose:
 * Receives the information required to start
 * the subscription purchase process.
 *
 * We do NOT accept price from the frontend.
 *
 * The backend will fetch the subscription using
 * subscriptionId and obtain the actual price
 * directly from the database.
 *
 * This prevents users from manipulating the
 * subscription price from the frontend.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentOrderRequest {

    /*
     * Organization purchasing the plan.
     */
    private Long organizationId;


    /*
     * Subscription plan selected by the organization.
     */
    private Long subscriptionId;
}