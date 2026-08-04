package com.sandbox.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sandbox.dto.CreatePaymentOrderRequest;
import com.sandbox.dto.PaymentOrderResponse;
import com.sandbox.dto.PaymentVerificationRequest;
import com.sandbox.entity.Payment;
import com.sandbox.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/*
 * PAYMENT CONTROLLER
 *
 * Purpose:
 * Exposes REST APIs for subscription payment operations.
 *
 * This controller acts as the HTTP/API layer between
 * the frontend and PaymentService.
 *
 * Responsibilities:
 *
 * 1. Create Razorpay payment orders.
 * 2. Verify completed Razorpay payments.
 * 3. Retrieve all payment records.
 * 4. Retrieve payment history for an organization.
 * 5. Filter payments based on payment status.
 *
 * Base URL:
 *
 * /admin/payments
 */
@RestController
@RequestMapping("/admin/payments")
@RequiredArgsConstructor
public class PaymentController {

    /*
     * PAYMENT SERVICE
     *
     * Contains all payment-related business logic.
     *
     * @RequiredArgsConstructor automatically creates
     * constructor injection for this final field.
     */
    private final PaymentService paymentService;


    /*
     * CREATE PAYMENT ORDER
     *
     * Endpoint:
     *
     * POST /admin/payments/orders
     *
     * Purpose:
     * Creates a Razorpay order for purchasing
     * a subscription plan.
     *
     * Important:
     * The frontend does NOT decide the payment amount.
     *
     * PaymentService retrieves the actual subscription
     * price from the database.
     */
    @PostMapping("/orders")
    public ResponseEntity<PaymentOrderResponse> createOrder(
            @Valid @RequestBody CreatePaymentOrderRequest request) {

        PaymentOrderResponse response =
                paymentService.createOrder(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    /*
     * VERIFY PAYMENT
     *
     * Endpoint:
     *
     * POST /admin/payments/verify
     *
     * Purpose:
     * Verifies the Razorpay payment signature after
     * the payment has been completed.
     *
     * If verification succeeds, PaymentService
     * changes the payment status to SUCCESS.
     */
    @PostMapping("/verify")
    public ResponseEntity<Payment> verifyPayment(
            @Valid @RequestBody PaymentVerificationRequest request) {

        Payment payment =
                paymentService.verifyPayment(request);

        return ResponseEntity.ok(payment);
    }


    /*
     * GET ALL PAYMENTS
     *
     * Endpoint:
     *
     * GET /admin/payments
     *
     * Purpose:
     * Returns all payment records.
     *
     * This endpoint can later be used by the
     * Admin Payment Monitoring dashboard.
     */
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {

        List<Payment> payments =
                paymentService.getAllPayments();

        return ResponseEntity.ok(payments);
    }


    /*
     * GET ORGANIZATION PAYMENT HISTORY
     *
     * Endpoint:
     *
     * GET /admin/payments/organization/{organizationId}
     *
     * Example:
     *
     * GET /admin/payments/organization/2
     *
     * Purpose:
     * Returns all payment records belonging
     * to a particular organization.
     */
    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<Payment>> getPaymentsByOrganization(
            @PathVariable Long organizationId) {

        List<Payment> payments =
                paymentService.getPaymentsByOrganization(
                        organizationId
                );

        return ResponseEntity.ok(payments);
    }


    /*
     * GET PAYMENTS BY STATUS
     *
     * Endpoint:
     *
     * GET /admin/payments/status/{status}
     *
     * Supported values:
     *
     * CREATED
     * SUCCESS
     * FAILED
     *
     * Examples:
     *
     * GET /admin/payments/status/CREATED
     *
     * GET /admin/payments/status/SUCCESS
     *
     * GET /admin/payments/status/FAILED
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(
            @PathVariable String status) {

        List<Payment> payments =
                paymentService.getPaymentsByStatus(status);

        return ResponseEntity.ok(payments);
    }
}