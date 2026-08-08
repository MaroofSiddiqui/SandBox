package com.sandbox.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sandbox.entity.Payment;
import com.sandbox.entity.User;
import com.sandbox.service.PaymentService;

import lombok.RequiredArgsConstructor;

/**
 * =========================================================
 * HR PAYMENT CONTROLLER
 * =========================================================
 *
 * Purpose:
 * Provides payment history for the organization
 * belonging to the currently authenticated HR.
 *
 * Endpoint:
 *
 * GET /hr/subscription/payments
 *
 * IMPORTANT SECURITY RULE:
 *
 * The organization ID is NEVER accepted from the frontend.
 *
 * Instead:
 *
 * JWT
 *   ↓
 * Authentication
 *   ↓
 * Current HR User
 *   ↓
 * User.organization
 *   ↓
 * organizationId
 *   ↓
 * PaymentService
 *   ↓
 * Only that organization's payments
 */
@RestController
@RequestMapping("/hr/subscription")
@RequiredArgsConstructor
public class HrPaymentController {

    private final PaymentService paymentService;


    /**
     * =========================================================
     * GET CURRENT HR ORGANIZATION PAYMENT HISTORY
     * =========================================================
     *
     * GET /hr/subscription/payments
     *
     * No organizationId is accepted from the frontend.
     *
     * Example frontend request:
     *
     * GET /hr/subscription/payments
     *
     * Authorization:
     *
     * Bearer <JWT>
     */
    @GetMapping("/payments")
    public ResponseEntity<List<Payment>> getPaymentHistory(
            Authentication authentication) {

        /*
         * Get the currently authenticated user.
         *
         * JwtAuthenticationFilter should have already
         * populated this Authentication object.
         */
        User currentUser =
                (User) authentication.getPrincipal();


        /*
         * HR must belong to an organization.
         *
         * A SUPER_ADMIN may have organization = null,
         * but an HR must always be associated with one.
         */
        if (currentUser.getOrganization() == null) {

            throw new IllegalStateException(
                    "HR account is not associated with an organization"
            );
        }


        /*
         * Get organization ID from the authenticated
         * user's organization.
         *
         * We DO NOT accept this value from React.
         */
        Long organizationId =
                currentUser
                        .getOrganization()
                        .getId();


        /*
         * Get payment history only for this organization.
         */
        List<Payment> payments =
                paymentService.getPaymentsByOrganization(
                        organizationId
                );


        /*
         * Return HTTP 200 OK.
         */
        return ResponseEntity.ok(payments);
    }
}