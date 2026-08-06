package com.sandbox.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sandbox.dto.HrOrganizationResponse;
import com.sandbox.entity.Organization;
import com.sandbox.entity.Subscription;
import com.sandbox.entity.User;
import com.sandbox.repository.OrganizationRepository;

@RestController
@RequestMapping("/hr/organization")
public class HrOrganizationController {

    private final OrganizationRepository organizationRepository;


    /*
     * Constructor dependency injection.
     */
    public HrOrganizationController(
            OrganizationRepository organizationRepository) {

        this.organizationRepository =
                organizationRepository;
    }


    /*
     * GET CURRENT HR ORGANIZATION
     *
     * GET /hr/organization
     */
    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<HrOrganizationResponse>
            getCurrentOrganization(
                    Authentication authentication) {


        /*
         * Get authenticated HR from Spring Security.
         */
        User currentUser =
                (User) authentication.getPrincipal();


        /*
         * HR must belong to an organization.
         *
         * We only use the organization attached to the
         * authenticated user to obtain its ID.
         *
         * We do NOT use it for loading subscription data
         * because the authentication principal may contain
         * detached Hibernate entities.
         */
        if (currentUser.getOrganization() == null) {

            throw new IllegalStateException(
                    "HR account is not associated with an organization"
            );
        }


        Long organizationId =
                currentUser
                        .getOrganization()
                        .getId();


        /*
         * Reload the organization from the database.
         *
         * findByIdWithSubscription() uses:
         *
         * LEFT JOIN FETCH o.subscription
         *
         * Therefore both Organization and Subscription
         * are loaded in the same query.
         */
        Organization organization =
                organizationRepository
                        .findByIdWithSubscription(
                                organizationId
                        )
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Organization not found"
                                )
                        );


        /*
         * Subscription is now already initialized
         * by our JOIN FETCH query.
         */
        Subscription subscription =
                organization.getSubscription();


        /*
         * Determine whether subscription is active.
         */
        boolean subscriptionActive =

                subscription != null

                && organization
                        .getSubscriptionExpiresAt() != null

                && organization
                        .getSubscriptionExpiresAt()
                        .isAfter(
                                LocalDateTime.now()
                        );


        /*
         * Convert Entity data into DTO.
         *
         * We deliberately don't return Organization
         * directly because entities can contain lazy
         * Hibernate relationships.
         */
        HrOrganizationResponse response =
                HrOrganizationResponse
                        .builder()

                        .organizationId(
                                organization.getId()
                        )

                        .organizationName(
                                organization.getName()
                        )

                        .organizationStatus(
                                organization.getStatus()
                        )

                        .subscriptionId(
                                subscription != null
                                        ? subscription.getId()
                                        : null
                        )

                        .planName(
                                subscription != null
                                        ? subscription.getPlanName()
                                        : null
                        )

                        .subscriptionStartAt(
                                organization
                                        .getSubscriptionStartAt()
                        )

                        .subscriptionExpiresAt(
                                organization
                                        .getSubscriptionExpiresAt()
                        )

                        .subscriptionActive(
                                subscriptionActive
                        )

                        .build();


        return ResponseEntity.ok(response);
    }
}