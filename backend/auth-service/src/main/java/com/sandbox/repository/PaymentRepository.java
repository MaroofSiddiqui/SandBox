package com.sandbox.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sandbox.entity.Payment;

/*
 * PAYMENT REPOSITORY
 *
 * Purpose:
 * Handles database operations for the Payment entity.
 *
 * JpaRepository already provides:
 *
 * save()
 * findById()
 * findAll()
 * deleteById()
 * count()
 *
 * Additional methods below are required for
 * Razorpay payment processing and payment history.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /*
     * FIND PAYMENT USING RAZORPAY ORDER ID
     *
     * When we create an order using Razorpay,
     * Razorpay returns an order ID.
     *
     * During payment verification we can use
     * this order ID to locate our payment record.
     */
    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);


    /*
     * FIND PAYMENT USING RAZORPAY PAYMENT ID
     *
     * Razorpay generates a payment ID after
     * the customer completes the payment.
     *
     * This method can be used for verification,
     * duplicate checking and payment lookup.
     */
    Optional<Payment> findByRazorpayPaymentId(String razorpayPaymentId);


    /*
     * GET PAYMENT HISTORY OF AN ORGANIZATION
     *
     * Returns all payments belonging to a
     * particular organization.
     *
     * Newest payments are returned first.
     */
    List<Payment> findByOrganizationIdOrderByCreatedAtDesc(
            Long organizationId
    );


    /*
     * GET PAYMENTS BY STATUS
     *
     * Useful for the Admin Payment Monitoring
     * Dashboard.
     *
     * Examples:
     *
     * SUCCESS
     * FAILED
     * CREATED
     */
    List<Payment> findByStatusOrderByCreatedAtDesc(
            String status
    );


    /*
     * CHECK WHETHER A RAZORPAY PAYMENT
     * HAS ALREADY BEEN RECORDED
     *
     * This helps prevent processing the same
     * Razorpay payment more than once.
     */
    boolean existsByRazorpayPaymentId(
            String razorpayPaymentId
    );
}