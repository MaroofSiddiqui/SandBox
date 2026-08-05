package com.sandbox.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sandbox.entity.Organization;

/*
 * ORGANIZATION REPOSITORY
 *
 * Handles database operations for Organization.
 */
public interface OrganizationRepository
        extends JpaRepository<Organization, Long> {


    /*
     * Find organization by domain.
     */
    Optional<Organization> findByDomain(String domain);


    /*
     * Check whether a domain already exists.
     */
    boolean existsByDomain(String domain);


    /*
     * FIND ORGANIZATION WITH SUBSCRIPTION
     *
     * Normally the subscription relationship may be LAZY.
     *
     * This query explicitly loads:
     *
     * Organization
     *      +
     * Subscription
     *
     * in the same database query.
     *
     * LEFT JOIN FETCH is important because an organization
     * may not have purchased a subscription yet.
     *
     * In that situation:
     *
     * organization != null
     * subscription = null
     *
     * and the organization can still be returned.
     */
    @Query("""
            SELECT o
            FROM Organization o
            LEFT JOIN FETCH o.subscription
            WHERE o.id = :id
            """)
    Optional<Organization> findByIdWithSubscription(
            @Param("id") Long id
    );
}