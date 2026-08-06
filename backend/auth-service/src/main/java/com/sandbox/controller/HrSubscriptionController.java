package com.sandbox.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.sandbox.dto.CurrentSubscriptionResponse;
import com.sandbox.entity.Organization;
import com.sandbox.entity.Payment;
import com.sandbox.entity.Subscription;
import com.sandbox.entity.User;
import com.sandbox.repository.OrganizationRepository;
import com.sandbox.service.PaymentService;

@RestController
@RequestMapping("/hr/subscription")
public class HrSubscriptionController {

    private final PaymentService paymentService;
    private final OrganizationRepository organizationRepository;


    public HrSubscriptionController(
            PaymentService paymentService,
            OrganizationRepository organizationRepository) {

        this.paymentService = paymentService;
        this.organizationRepository = organizationRepository;
    }


    /*
     * GET CURRENT SUBSCRIPTION
     *
     * GET /hr/subscription/current
     */
    @GetMapping("/current")
    public ResponseEntity<CurrentSubscriptionResponse>
            getCurrentSubscription(
                    Authentication authentication) {


        User currentUser =
                (User) authentication.getPrincipal();


        /*
         * User must belong to an organization.
         */
        if (currentUser.getOrganization() == null) {

            throw new IllegalStateException(
                    "User is not associated with an organization"
            );
        }


        Long organizationId =
                currentUser.getOrganization().getId();


        /*
         * Reload organization together with subscription.
         *
         * This avoids LazyInitializationException because
         * the Organization stored in the JWT principal may
         * not have its Subscription initialized.
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


        Subscription subscription =
                organization.getSubscription();


        /*
         * Organization has never purchased a plan.
         */
        if (subscription == null) {

            return ResponseEntity
                    .noContent()
                    .build();
        }


        /*
         * Subscription is active only when:
         *
         * 1. Organization is ACTIVE
         * 2. Subscription plan is ACTIVE
         * 3. Expiry exists
         * 4. Expiry is still in the future
         */
        boolean active =

                "ACTIVE".equalsIgnoreCase(
                        organization.getStatus()
                )

                && "ACTIVE".equalsIgnoreCase(
                        subscription.getStatus()
                )

                && organization
                        .getSubscriptionExpiresAt() != null

                && organization
                        .getSubscriptionExpiresAt()
                        .isAfter(
                                LocalDateTime.now()
                        );


        CurrentSubscriptionResponse response =
                new CurrentSubscriptionResponse(

                        organization.getId(),

                        organization.getName(),

                        subscription.getId(),

                        subscription.getPlanName(),

                        subscription.getDescription(),

                        subscription.getPrice(),

                        subscription.getDurationMonths(),

                        subscription.getMaxCandidates(),

                        organization.getSubscriptionStartAt(),

                        organization.getSubscriptionExpiresAt(),

                        active
                );


        return ResponseEntity.ok(response);
    }


    /*
     * GET PAYMENT HISTORY
     *
     * GET /hr/subscription/payments
     *
     * Organization ID comes from authenticated HR.
     */
    @GetMapping("/payments")
    public ResponseEntity<List<Payment>>
            getPaymentHistory(
                    Authentication authentication) {


        User currentUser =
                (User) authentication.getPrincipal();


        if (currentUser.getOrganization() == null) {

            throw new IllegalStateException(
                    "User is not associated with an organization"
            );
        }


        Long organizationId =
                currentUser
                        .getOrganization()
                        .getId();


        return ResponseEntity.ok(

                paymentService
                        .getPaymentsByOrganization(
                                organizationId
                        )
        );
    }
}