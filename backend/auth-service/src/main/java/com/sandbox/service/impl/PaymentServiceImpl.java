package com.sandbox.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import com.sandbox.dto.CreatePaymentOrderRequest;
import com.sandbox.dto.PaymentOrderResponse;
import com.sandbox.dto.PaymentVerificationRequest;
import com.sandbox.entity.Payment;
import com.sandbox.entity.Subscription;
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
 * 6. Retrieve payment history.
 * 7. Provide payment data for Admin monitoring.
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
     * Razorpay Key ID.
     *
     * Loaded from:
     *
     * application.properties
     *
     * razorpay.key.id=${RAZORPAY_KEY_ID:}
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
     * subscriptionId
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
         * Find the subscription selected by the
         * organization.
         *
         * findById() is automatically available
         * because SubscriptionRepository extends
         * JpaRepository.
         */
        Subscription subscription = subscriptionRepository
                .findById(request.getSubscriptionId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Subscription plan not found"
                        )
                );


        /*
         * An organization must not be able to
         * purchase an inactive subscription.
         */
        if (!"ACTIVE".equalsIgnoreCase(subscription.getStatus())) {

            throw new IllegalStateException(
                    "Subscription plan is currently inactive"
            );
        }


        /*
         * Validate the subscription price.
         */
        if (subscription.getPrice() == null ||
                subscription.getPrice().compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalStateException(
                    "Subscription price is invalid"
            );
        }


        try {

            /*
             * Create Razorpay client using credentials
             * stored in environment variables.
             */
            RazorpayClient razorpayClient =
                    new RazorpayClient(
                            razorpayKeyId,
                            razorpayKeySecret
                    );


            /*
             * Razorpay expects the amount in the
             * smallest currency unit.
             *
             * For INR:
             *
             * ₹1     = 100 paise
             * ₹999   = 99900 paise
             * ₹2999  = 299900 paise
             *
             * We use BigDecimal instead of double
             * to avoid floating-point errors.
             */
            long amountInPaise = subscription
                    .getPrice()
                    .multiply(BigDecimal.valueOf(100))
                    .longValueExact();


            /*
             * Prepare Razorpay order request.
             */
            JSONObject orderRequest = new JSONObject();

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
             *
             * nanoTime helps make it unique.
             */
            orderRequest.put(
                    "receipt",
                    "sandbox_" + System.nanoTime()
            );


            /*
             * Create the actual order using
             * Razorpay API.
             */
            Order razorpayOrder =
                    razorpayClient.orders.create(orderRequest);


            /*
             * Retrieve Razorpay-generated order ID.
             */
            String razorpayOrderId =
                    razorpayOrder.get("id");


            /*
             * Create our own local payment record.
             *
             * Notice:
             *
             * Amount comes from Subscription entity,
             * NOT from the frontend.
             */
            Payment payment = Payment.builder()
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
             * Return only the information required
             * by Razorpay Checkout on the frontend.
             *
             * NEVER return razorpayKeySecret.
             */
            return PaymentOrderResponse.builder()
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
     * We verify the signature on the backend.
     *
     * Only after successful verification do we
     * mark the payment as SUCCESS.
     */
    @Override
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
         * Find our payment using the Razorpay
         * order ID.
         */
        Payment payment = paymentRepository
                .findByRazorpayOrderId(
                        request.getRazorpayOrderId()
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Payment order not found"
                        )
                );


        /*
         * If this order was already successfully
         * processed, don't process it again.
         */
        if ("SUCCESS".equalsIgnoreCase(payment.getStatus())) {

            /*
             * Same payment can safely receive the
             * existing result.
             */
            if (request.getRazorpayPaymentId()
                    .equals(payment.getRazorpayPaymentId())) {

                return payment;
            }

            throw new IllegalStateException(
                    "Payment order has already been processed"
            );
        }


        /*
         * Prevent one Razorpay payment ID from
         * being attached to multiple local records.
         */
        if (paymentRepository.existsByRazorpayPaymentId(
                request.getRazorpayPaymentId())) {

            throw new IllegalStateException(
                    "Razorpay payment has already been processed"
            );
        }


        try {

            /*
             * Razorpay requires these exact property
             * names for signature verification.
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
             * Verify signature using Razorpay SDK.
             *
             * Internally Razorpay verifies the
             * signature using our secret key.
             */
            boolean signatureValid =
                    Utils.verifyPaymentSignature(
                            verificationAttributes,
                            razorpayKeySecret
                    );


            /*
             * Reject forged/invalid payment data.
             */
            if (!signatureValid) {

                payment.setStatus("FAILED");

                paymentRepository.save(payment);

                throw new IllegalArgumentException(
                        "Payment signature verification failed"
                );
            }


            /*
             * Signature is valid.
             *
             * Store Razorpay payment ID and mark
             * transaction as SUCCESS.
             */
            payment.setRazorpayPaymentId(
                    request.getRazorpayPaymentId()
            );

            payment.setStatus("SUCCESS");

            payment.setPaidAt(
                    LocalDateTime.now()
            );


            /*
             * Persist verified payment.
             */
            return paymentRepository.save(payment);


        } catch (IllegalArgumentException |
                 IllegalStateException exception) {

            /*
             * Preserve our own validation errors.
             */
            throw exception;

        } catch (Exception exception) {

            throw new RuntimeException(
                    "Unable to verify Razorpay payment",
                    exception
            );
        }
    }


    /*
     * GET ALL PAYMENTS
     *
     * Used by the Admin Payment Monitoring
     * Dashboard.
     */
    @Override
    public List<Payment> getAllPayments() {

        return paymentRepository.findAll();
    }


    /*
     * GET ORGANIZATION PAYMENT HISTORY
     *
     * Newest payments are returned first because
     * that ordering is already defined in the
     * repository method.
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
     * Supported statuses:
     *
     * CREATED
     * SUCCESS
     * FAILED
     */
    @Override
    public List<Payment> getPaymentsByStatus(
            String status) {

        if (status == null || status.isBlank()) {

            throw new IllegalArgumentException(
                    "Payment status is required"
            );
        }


        /*
         * Normalize status so:
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
         * Reject unsupported values.
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