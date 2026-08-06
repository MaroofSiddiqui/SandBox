package com.sandbox.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sandbox.dto.SubscriptionRequest;
import com.sandbox.dto.SubscriptionResponse;
import com.sandbox.entity.Subscription;
import com.sandbox.service.SubscriptionService;

import jakarta.validation.Valid;

/*
 * SUBSCRIPTION CONTROLLER
 *
 * Purpose:
 * Handles HTTP requests related to subscription
 * plan management.
 *
 * Admin can:
 *
 * - Create subscription plans
 * - View all plans
 * - View one plan
 * - Update plans
 * - Activate / Deactivate plans
 *
 * Base URL:
 *
 * /admin/subscriptions
 */
@RestController
@RequestMapping("/admin/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;


    /*
     * CONSTRUCTOR DEPENDENCY INJECTION
     */
    public SubscriptionController(
            SubscriptionService subscriptionService) {

        this.subscriptionService = subscriptionService;
    }


    /*
     * CREATE SUBSCRIPTION
     *
     * POST /admin/subscriptions
     */
    @PostMapping
    public ResponseEntity<SubscriptionResponse> createSubscription(
            @Valid @RequestBody SubscriptionRequest request) {

        Subscription subscription =
                subscriptionService.createSubscription(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(subscription));
    }


    /*
     * GET ALL SUBSCRIPTIONS
     *
     * GET /admin/subscriptions
     */
    @GetMapping
    public ResponseEntity<List<SubscriptionResponse>>
            getAllSubscriptions() {

        List<SubscriptionResponse> response =
                subscriptionService
                        .getAllSubscriptions()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }


    /*
     * GET SUBSCRIPTION BY ID
     *
     * GET /admin/subscriptions/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionResponse>
            getSubscriptionById(
                    @PathVariable Long id) {

        Subscription subscription =
                subscriptionService
                        .getSubscriptionById(id);

        return ResponseEntity.ok(
                toResponse(subscription)
        );
    }


    /*
     * UPDATE SUBSCRIPTION
     *
     * PUT /admin/subscriptions/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionResponse>
            updateSubscription(
                    @PathVariable Long id,
                    @Valid @RequestBody
                    SubscriptionRequest request) {

        Subscription subscription =
                subscriptionService
                        .updateSubscription(id, request);

        return ResponseEntity.ok(
                toResponse(subscription)
        );
    }


    /*
     * UPDATE STATUS
     *
     * PATCH
     * /admin/subscriptions/{id}/status
     *
     * Request:
     *
     * {
     *     "status": "INACTIVE"
     * }
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<SubscriptionResponse>
            updateStatus(
                    @PathVariable Long id,
                    @RequestBody StatusRequest request) {

        Subscription subscription =
                subscriptionService
                        .updateSubscriptionStatus(
                                id,
                                request.getStatus()
                        );

        return ResponseEntity.ok(
                toResponse(subscription)
        );
    }


    /*
     * ENTITY -> RESPONSE DTO
     *
     * Keeps entity conversion in one place instead
     * of repeating the same constructor in every
     * endpoint.
     */
    private SubscriptionResponse toResponse(
            Subscription subscription) {

        return new SubscriptionResponse(
                subscription.getId(),
                subscription.getPlanName(),
                subscription.getDescription(),
                subscription.getDurationMonths(),
                subscription.getPrice(),
                subscription.getMaxCandidates(),
                subscription.getStatus(),
                subscription.getCreatedAt()
        );
    }


    /*
     * SMALL REQUEST DTO FOR STATUS UPDATE
     *
     * Used for:
     *
     * {
     *     "status": "ACTIVE"
     * }
     */
    public static class StatusRequest {

        private String status;

        public StatusRequest() {
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}