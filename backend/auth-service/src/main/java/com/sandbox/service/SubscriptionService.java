package com.sandbox.service;

import java.util.List;

import com.sandbox.dto.SubscriptionRequest;
import com.sandbox.entity.Subscription;

/*
 * SUBSCRIPTION SERVICE
 *
 * Purpose:
 * Defines the business operations related
 * to Subscription management.
 *
 * This interface acts as a contract between
 * the Controller and the Service Implementation.
 *
 * Flow:
 *
 * SubscriptionController
 *          ↓
 * SubscriptionService
 *          ↓
 * SubscriptionServiceImpl
 *          ↓
 * SubscriptionRepository
 *
 * Responsibilities:
 *
 * - Create Subscription Plan
 * - Update Subscription Plan
 * - View All Subscription Plans
 * - View Single Subscription Plan
 * - Activate / Deactivate Subscription
 */
public interface SubscriptionService {

    /*
     * CREATE SUBSCRIPTION
     *
     * Creates a new subscription plan.
     *
     * Example:
     *
     * Basic
     * Pro
     * Enterprise
     *
     * Returns:
     *
     * Newly created Subscription entity.
     */
    Subscription createSubscription(
            SubscriptionRequest request
    );


    /*
     * UPDATE SUBSCRIPTION
     *
     * Updates an existing subscription plan.
     *
     * Parameters:
     *
     * id
     * -> Subscription ID
     *
     * request
     * -> Updated subscription details
     *
     * Returns:
     *
     * Updated Subscription entity.
     */
    Subscription updateSubscription(
            Long id,
            SubscriptionRequest request
    );


    /*
     * GET ALL SUBSCRIPTIONS
     *
     * Retrieves all subscription plans.
     *
     * Example:
     *
     * Basic
     * Pro
     * Enterprise
     */
    List<Subscription> getAllSubscriptions();


    /*
     * GET SUBSCRIPTION BY ID
     *
     * Returns one subscription plan
     * using its unique ID.
     */
    Subscription getSubscriptionById(
            Long id
    );


    /*
     * UPDATE SUBSCRIPTION STATUS
     *
     * Used to activate or deactivate
     * a subscription plan.
     *
     * Example:
     *
     * ACTIVE
     * INACTIVE
     *
     * Returns:
     *
     * Updated Subscription entity.
     */
    Subscription updateSubscriptionStatus(
            Long id,
            String status
    );

}