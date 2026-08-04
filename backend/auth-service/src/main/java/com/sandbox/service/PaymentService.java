package com.sandbox.service;

import java.util.List;

import com.sandbox.dto.CreatePaymentOrderRequest;
import com.sandbox.dto.PaymentOrderResponse;
import com.sandbox.dto.PaymentVerificationRequest;
import com.sandbox.entity.Payment;

/*
 * PAYMENT SERVICE
 *
 * Purpose:
 * Defines all business operations related
 * to subscription payments.
 *
 * Responsibilities:
 *
 * 1. Create Razorpay payment orders.
 * 2. Verify completed Razorpay payments.
 * 3. Retrieve all payments for Admin monitoring.
 * 4. Retrieve payment history of an organization.
 * 5. Filter payments based on payment status.
 */
public interface PaymentService {

    /*
     * CREATE PAYMENT ORDER
     *
     * Creates a Razorpay order for the selected
     * subscription plan.
     *
     * The subscription price is fetched from
     * the database instead of trusting an amount
     * supplied by the frontend.
     */
    PaymentOrderResponse createOrder(
            CreatePaymentOrderRequest request
    );


    /*
     * VERIFY PAYMENT
     *
     * Verifies the Razorpay signature received
     * after payment completion.
     *
     * If verification succeeds, the corresponding
     * payment record is marked SUCCESS.
     */
    Payment verifyPayment(
            PaymentVerificationRequest request
    );


    /*
     * GET ALL PAYMENTS
     *
     * Used by the Admin Payment Monitoring
     * Dashboard.
     */
    List<Payment> getAllPayments();


    /*
     * GET ORGANIZATION PAYMENT HISTORY
     *
     * Returns payments belonging to a particular
     * organization, newest first.
     */
    List<Payment> getPaymentsByOrganization(
            Long organizationId
    );


    /*
     * GET PAYMENTS BY STATUS
     *
     * Examples:
     *
     * CREATED
     * SUCCESS
     * FAILED
     */
    List<Payment> getPaymentsByStatus(
            String status
    );
}