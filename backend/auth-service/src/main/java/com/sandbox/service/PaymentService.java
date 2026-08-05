package com.sandbox.service;

import java.util.List;

import com.sandbox.dto.CreatePaymentOrderRequest;
import com.sandbox.dto.PaymentOrderResponse;
import com.sandbox.dto.PaymentVerificationRequest;
import com.sandbox.entity.Payment;

/*
 * PAYMENT SERVICE
 *
 * Defines business operations related to
 * subscription payments.
 */
public interface PaymentService {

    /*
     * CREATE PAYMENT ORDER
     *
     * organizationId is NOT received from frontend.
     *
     * PaymentController obtains it from the
     * authenticated HR user and passes it here.
     *
     * The request contains only subscriptionId.
     */
    PaymentOrderResponse createOrder(
            Long organizationId,
            CreatePaymentOrderRequest request
    );


    /*
     * VERIFY PAYMENT
     *
     * Verifies Razorpay payment signature and,
     * after successful verification, activates
     * the organization's purchased subscription.
     */
    Payment verifyPayment(
            PaymentVerificationRequest request
    );


    /*
     * GET ALL PAYMENTS
     *
     * Used by SUPER_ADMIN for payment monitoring.
     */
    List<Payment> getAllPayments();


    /*
     * GET ORGANIZATION PAYMENT HISTORY
     */
    List<Payment> getPaymentsByOrganization(
            Long organizationId
    );


    /*
     * GET PAYMENTS BY STATUS
     *
     * CREATED
     * SUCCESS
     * FAILED
     */
    List<Payment> getPaymentsByStatus(
            String status
    );
}