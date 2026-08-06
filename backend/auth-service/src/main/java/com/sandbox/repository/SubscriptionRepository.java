package com.sandbox.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sandbox.entity.Subscription;

/*
 * SUBSCRIPTION REPOSITORY
 *
 * Purpose:
 * Provides database access methods for the
 * Subscription entity.
 *
 * Since it extends JpaRepository, Spring Data JPA
 * automatically provides common CRUD operations.
 *
 * Examples:
 *
 * save(subscription)
 * findById(id)
 * findAll()
 * deleteById(id)
 *
 * Additional custom methods are declared here
 * whenever required.
 */
public interface SubscriptionRepository
        extends JpaRepository<Subscription, Long> {

    /*
     * FIND SUBSCRIPTION BY PLAN NAME
     *
     * Used to:
     *
     * - Prevent duplicate subscription plans.
     * - Retrieve a specific plan by its name.
     *
     * Example:
     *
     * findByPlanName("Basic")
     */
    Optional<Subscription> findByPlanName(String planName);



    /*
     * CHECK WHETHER A PLAN NAME ALREADY EXISTS
     *
     * Used before creating a new subscription.
     *
     * Example:
     *
     * if (existsByPlanName("Basic")) {
     *     throw ...
     * }
     */
    boolean existsByPlanName(String planName);

}