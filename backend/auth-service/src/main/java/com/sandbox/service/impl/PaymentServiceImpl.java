package com.sandbox.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import com.sandbox.dto.CreatePaymentOrderRequest;
import com.sandbox.dto.PaymentOrderResponse;
import com.sandbox.dto.PaymentVerificationRequest;
import com.sandbox.entity.Organization;
import com.sandbox.entity.Payment;
import com.sandbox.entity.Subscription;
import com.sandbox.repository.OrganizationRepository;
import com.sandbox.repository.PaymentRepository;
import com.sandbox.repository.SubscriptionRepository;
import com.sandbox.service.PaymentService;

import lombok.RequiredArgsConstructor;

/*
 * PAYMENT SERVICE IMPLEMENTATION
 *
 * Purpose:
 * Contains the complete business logic for
 * subscription payments using Razorpay.
 *
 * Responsibilities:
 *
 * 1. Validate the selected subscription.
 * 2. Create Razorpay orders.
 * 3. Store payment records in the database.
 * 4. Verify Razorpay payment signatures.
 * 5. Prevent duplicate payment processing.
 * 6. Activate organization subscriptions after payment.
 * 7. Calculate subscription start and expiry dates.
 * 8. Retrieve payment history.
 * 9. Provide payment data for Admin monitoring.
 */
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    /*
     * Repository used for payment database operations.
     */
    private final PaymentRepository paymentRepository;

    /*
     * Repository used to retrieve subscription plans.
     */
    private final SubscriptionRepository subscriptionRepository;

    /*
     * Repository used to retrieve and update organizations.
     *
     * After successful payment verification,
     * the purchased subscription is assigned
     * to the corresponding organization.
     */
    private final OrganizationRepository organizationRepository;


    /*
     * Razorpay Key ID.
     *
     * Loaded from application.properties.
     */
    @Value("${razorpay.key.id}")
    private String razorpayKeyId;


    /*
     * Razorpay Secret Key.
     *
     * IMPORTANT:
     * This value must NEVER be returned to
     * the frontend.
     */
    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;


    /*
     * CREATE RAZORPAY ORDER
     *
     * Flow:
     *
     * organizationId
     *      +
     * subscriptionId
     *      ↓
     * Validate organization
     *      ↓
     * Find subscription
     *      ↓
     * Validate ACTIVE status
     *      ↓
     * Get price from database
     *      ↓
     * Convert rupees to paise
     *      ↓
     * Create Razorpay order
     *      ↓
     * Save payment as CREATED
     *      ↓
     * Return order information
     */
    @Override
    public PaymentOrderResponse createOrder(
            CreatePaymentOrderRequest request) {

        /*
         * Basic request validation.
         */
        if (request == null) {

            throw new IllegalArgumentException(
                    "Payment request cannot be empty"
            );
        }


        if (request.getOrganizationId() == null) {

            throw new IllegalArgumentException(
                    "Organization ID is required"
            );
        }


        if (request.getSubscriptionId() == null) {

            throw new IllegalArgumentException(
                    "Subscription ID is required"
            );
        }


        /*
         * Make sure the organization actually exists
         * before creating an external Razorpay order.
         *
         * This prevents unnecessary Razorpay orders
         * for invalid organization IDs.
         */
        if (!organizationRepository.existsById(
                request.getOrganizationId())) {

            throw new IllegalArgumentException(
                    "Organization not found"
            );
        }


        /*
         * Find the subscription selected by
         * the organization.
         */
        Subscription subscription =
                subscriptionRepository
                        .findById(
                                request.getSubscriptionId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Subscription plan not found"
                                )
                        );


        /*
         * Organizations cannot purchase an
         * inactive subscription.
         */
        if (!"ACTIVE".equalsIgnoreCase(
                subscription.getStatus())) {

            throw new IllegalStateException(
                    "Subscription plan is currently inactive"
            );
        }


        /*
         * Validate subscription price.
         */
        if (subscription.getPrice() == null ||
                subscription.getPrice()
                        .compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalStateException(
                    "Subscription price is invalid"
            );
        }


        try {

            /*
             * Create Razorpay client using credentials
             * stored securely on the backend.
             */
            RazorpayClient razorpayClient =
                    new RazorpayClient(
                            razorpayKeyId,
                            razorpayKeySecret
                    );


            /*
             * Razorpay expects INR amounts in paise.
             *
             * ₹1    = 100 paise
             * ₹999  = 99900 paise
             * ₹2999 = 299900 paise
             */
            long amountInPaise =
                    subscription
                            .getPrice()
                            .multiply(
                                    BigDecimal.valueOf(100)
                            )
                            .longValueExact();


            /*
             * Prepare Razorpay order request.
             */
            JSONObject orderRequest =
                    new JSONObject();


            orderRequest.put(
                    "amount",
                    amountInPaise
            );


            orderRequest.put(
                    "currency",
                    "INR"
            );


            /*
             * Receipt provides our own reference
             * for the Razorpay order.
             */
            orderRequest.put(
                    "receipt",
                    "sandbox_" + System.nanoTime()
            );


            /*
             * Create the actual Razorpay order.
             */
            Order razorpayOrder =
                    razorpayClient.orders.create(
                            orderRequest
                    );


            /*
             * Retrieve Razorpay-generated order ID.
             */
            String razorpayOrderId =
                    razorpayOrder.get("id");


            /*
             * Create local payment record.
             *
             * IMPORTANT:
             * The amount comes from the database
             * subscription and not from the frontend.
             */
            Payment payment =
                    Payment.builder()
                            .organizationId(
                                    request.getOrganizationId()
                            )
                            .subscriptionId(
                                    subscription.getId()
                            )
                            .amount(
                                    subscription.getPrice()
                            )
                            .currency("INR")
                            .razorpayOrderId(
                                    razorpayOrderId
                            )
                            .status("CREATED")
                            .build();


            /*
             * Store payment in MySQL.
             */
            Payment savedPayment =
                    paymentRepository.save(payment);


            /*
             * Return information required by
             * Razorpay Checkout.
             *
             * NEVER return razorpayKeySecret.
             */
            return PaymentOrderResponse
                    .builder()
                    .paymentId(
                            savedPayment.getId()
                    )
                    .razorpayOrderId(
                            razorpayOrderId
                    )
                    .amount(
                            subscription.getPrice()
                    )
                    .currency("INR")
                    .planName(
                            subscription.getPlanName()
                    )
                    .razorpayKey(
                            razorpayKeyId
                    )
                    .build();


        } catch (Exception exception) {

            /*
             * Do not expose Razorpay internals or
             * secret information to the client.
             */
            throw new RuntimeException(
                    "Unable to create Razorpay payment order",
                    exception
            );
        }
    }


    /*
     * VERIFY RAZORPAY PAYMENT
     *
     * Razorpay Checkout returns:
     *
     * razorpay_order_id
     * razorpay_payment_id
     * razorpay_signature
     *
     * Flow:
     *
     * Receive Razorpay response
     *          ↓
     * Find local payment
     *          ↓
     * Verify signature
     *          ↓
     * Mark payment SUCCESS
     *          ↓
     * Find organization
     *          ↓
     * Find purchased subscription
     *          ↓
     * Assign subscription
     *          ↓
     * Calculate expiry
     *          ↓
     * Save everything
     *
     * @Transactional ensures the successful payment
     * update and organization subscription activation
     * are processed as one database transaction.
     */
    @Override
    @Transactional
    public Payment verifyPayment(
            PaymentVerificationRequest request) {

        /*
         * Validate request.
         */
        if (request == null) {

            throw new IllegalArgumentException(
                    "Payment verification request cannot be empty"
            );
        }


        if (request.getRazorpayOrderId() == null ||
                request.getRazorpayOrderId().isBlank()) {

            throw new IllegalArgumentException(
                    "Razorpay order ID is required"
            );
        }


        if (request.getRazorpayPaymentId() == null ||
                request.getRazorpayPaymentId().isBlank()) {

            throw new IllegalArgumentException(
                    "Razorpay payment ID is required"
            );
        }


        if (request.getRazorpaySignature() == null ||
                request.getRazorpaySignature().isBlank()) {

            throw new IllegalArgumentException(
                    "Razorpay signature is required"
            );
        }


        /*
         * Find our local payment using
         * Razorpay order ID.
         */
        Payment payment =
                paymentRepository
                        .findByRazorpayOrderId(
                                request.getRazorpayOrderId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Payment order not found"
                                )
                        );


        /*
         * IDEMPOTENCY CHECK
         *
         * If the exact same successful payment
         * verification reaches us again, simply
         * return the existing payment.
         *
         * This prevents duplicate processing.
         */
        if ("SUCCESS".equalsIgnoreCase(
                payment.getStatus())) {

            if (request.getRazorpayPaymentId()
                    .equals(
                            payment.getRazorpayPaymentId()
                    )) {

                return payment;
            }


            throw new IllegalStateException(
                    "Payment order has already been processed"
            );
        }


        /*
         * Prevent the same Razorpay payment ID
         * from being attached to another local
         * payment record.
         */
        if (paymentRepository
                .existsByRazorpayPaymentId(
                        request.getRazorpayPaymentId()
                )) {

            throw new IllegalStateException(
                    "Razorpay payment has already been processed"
            );
        }


        try {

            /*
             * Razorpay requires these exact
             * property names.
             */
            JSONObject verificationAttributes =
                    new JSONObject();


            verificationAttributes.put(
                    "razorpay_order_id",
                    request.getRazorpayOrderId()
            );


            verificationAttributes.put(
                    "razorpay_payment_id",
                    request.getRazorpayPaymentId()
            );


            verificationAttributes.put(
                    "razorpay_signature",
                    request.getRazorpaySignature()
            );


            /*
             * Verify payment signature using
             * Razorpay SDK and our secret key.
             */
            boolean signatureValid =
                    Utils.verifyPaymentSignature(
                            verificationAttributes,
                            razorpayKeySecret
                    );


            /*
             * Reject forged or invalid payment data.
             */
            if (!signatureValid) {

                payment.setStatus("FAILED");

                paymentRepository.save(payment);


                throw new IllegalArgumentException(
                        "Payment signature verification failed"
                );
            }


            /*
             * Use the same timestamp for:
             *
             * payment paidAt
             * subscription start date
             *
             * This keeps our records consistent.
             */
            LocalDateTime now =
                    LocalDateTime.now();


            /*
             * Payment has been successfully
             * cryptographically verified.
             */
            payment.setRazorpayPaymentId(
                    request.getRazorpayPaymentId()
            );


            payment.setStatus("SUCCESS");


            payment.setPaidAt(now);


            /*
             * Find the organization that made
             * this payment.
             */
            Organization organization =
                    organizationRepository
                            .findById(
                                    payment.getOrganizationId()
                            )
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "Organization associated with payment not found"
                                    )
                            );


            /*
             * Find the subscription purchased
             * through this payment.
             */
            Subscription subscription =
                    subscriptionRepository
                            .findById(
                                    payment.getSubscriptionId()
                            )
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "Subscription associated with payment not found"
                                    )
                            );


            /*
             * Assign purchased subscription
             * to the organization.
             */
            organization.setSubscription(
                    subscription
            );


            /*
             * Subscription becomes active from
             * the successful payment time.
             */
            organization.setSubscriptionStartAt(
                    now
            );


            /*
             * Calculate subscription expiry.
             *
             * Example:
             *
             * Basic duration = 3 months
             *
             * Start:
             * 05 Aug 2026
             *
             * Expiry:
             * 05 Nov 2026
             */
            organization.setSubscriptionExpiresAt(
                    now.plusMonths(
                            subscription.getDurationMonths()
                    )
            );


            /*
             * Save updated organization.
             */
            organizationRepository.save(
                    organization
            );


            /*
             * Save verified payment.
             *
             * Because this method is transactional,
             * payment and organization changes belong
             * to the same database transaction.
             */
            return paymentRepository.save(
                    payment
            );


        } catch (IllegalArgumentException |
                 IllegalStateException exception) {

            /*
             * Preserve our own business and
             * validation exceptions.
             */
            throw exception;


        } catch (Exception exception) {

            /*
             * Convert unexpected Razorpay/internal
             * errors into an application exception.
             */
            throw new RuntimeException(
                    "Unable to verify Razorpay payment",
                    exception
            );
        }
    }


    /*
     * GET ALL PAYMENTS
     *
     * Used by Admin Payment Monitoring Dashboard.
     */
    @Override
    public List<Payment> getAllPayments() {

        return paymentRepository.findAll();
    }


    /*
     * GET ORGANIZATION PAYMENT HISTORY
     *
     * Newest payments are returned first because
     * that ordering is defined by the repository.
     */
    @Override
    public List<Payment> getPaymentsByOrganization(
            Long organizationId) {

        if (organizationId == null) {

            throw new IllegalArgumentException(
                    "Organization ID is required"
            );
        }


        return paymentRepository
                .findByOrganizationIdOrderByCreatedAtDesc(
                        organizationId
                );
    }


    /*
     * GET PAYMENTS BY STATUS
     *
     * Used by Admin to filter transactions.
     *
     * Supported:
     *
     * CREATED
     * SUCCESS
     * FAILED
     */
    @Override
    public List<Payment> getPaymentsByStatus(
            String status) {

        if (status == null ||
                status.isBlank()) {

            throw new IllegalArgumentException(
                    "Payment status is required"
            );
        }


        /*
         * Normalize status.
         *
         * success
         * Success
         * SUCCESS
         *
         * all become:
         *
         * SUCCESS
         */
        String normalizedStatus =
                status.trim().toUpperCase();


        /*
         * Reject unsupported status values.
         */
        if (!normalizedStatus.equals("CREATED") &&
                !normalizedStatus.equals("SUCCESS") &&
                !normalizedStatus.equals("FAILED")) {

            throw new IllegalArgumentException(
                    "Invalid payment status"
            );
        }


        return paymentRepository
                .findByStatusOrderByCreatedAtDesc(
                        normalizedStatus
                );
    }
}