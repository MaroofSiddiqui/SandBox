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
import com.sandbox.service.PaymentService;

/*
 * HR SUBSCRIPTION CONTROLLER
 *
 * Provides subscription/payment information for
 * the currently authenticated HR's organization.
 *
 * SECURITY:
 * Organization ID is never accepted from React.
 */
@RestController
@RequestMapping("/hr/subscription")
public class HrSubscriptionController {

    private final PaymentService paymentService;

    public HrSubscriptionController(
            PaymentService paymentService) {

        this.paymentService = paymentService;
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

        Organization organization =
                currentUser.getOrganization();

        if (organization == null) {

            throw new IllegalStateException(
                    "User is not associated with an organization"
            );
        }


        Subscription subscription =
                organization.getSubscription();


        /*
         * Organization has never purchased a plan.
         */
        if (subscription == null) {

            return ResponseEntity.noContent().build();
        }


        /*
         * Subscription is active only when its
         * expiry date is still in the future.
         */
        boolean active =
                organization.getSubscriptionExpiresAt() != null
                &&
                organization
                    .getSubscriptionExpiresAt()
                    .isAfter(LocalDateTime.now());


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
     * Again, organization ID comes from the
     * authenticated HR and not from frontend.
     */
    @GetMapping("/payments")
    public ResponseEntity<List<Payment>>
            getPaymentHistory(
                    Authentication authentication) {

        User currentUser =
                (User) authentication.getPrincipal();

        Organization organization =
                currentUser.getOrganization();

        if (organization == null) {

            throw new IllegalStateException(
                    "User is not associated with an organization"
            );
        }


        return ResponseEntity.ok(

                paymentService
                    .getPaymentsByOrganization(
                            organization.getId()
                    )
        );
    }
}