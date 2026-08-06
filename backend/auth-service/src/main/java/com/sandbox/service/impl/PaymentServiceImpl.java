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

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final SubscriptionRepository subscriptionRepository;

    private final OrganizationRepository organizationRepository;


    @Value("${razorpay.key.id}")
    private String razorpayKeyId;


    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;


    /*
     * =========================================================
     * CREATE RAZORPAY ORDER
     * =========================================================
     *
     * IMPORTANT SECURITY CHANGE:
     *
     * organizationId no longer comes from the frontend.
     *
     * It is obtained by PaymentController from the
     * authenticated HR user's organization.
     *
     * Frontend sends only:
     *
     * {
     *     "subscriptionId": 1
     * }
     */
    @Override
    public PaymentOrderResponse createOrder(
            Long organizationId,
            CreatePaymentOrderRequest request) {


        /*
         * Validate request.
         */
        if (request == null) {

            throw new IllegalArgumentException(
                    "Payment request cannot be empty"
            );
        }


        /*
         * organizationId was obtained from the
         * authenticated user by the controller.
         */
        if (organizationId == null) {

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
         * Validate organization before creating
         * an external Razorpay order.
         */
        if (!organizationRepository.existsById(
                organizationId)) {

            throw new IllegalArgumentException(
                    "Organization not found"
            );
        }


        /*
         * Find selected subscription.
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
         * Inactive plans cannot be purchased.
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
             * Create Razorpay client.
             */
            RazorpayClient razorpayClient =
                    new RazorpayClient(
                            razorpayKeyId,
                            razorpayKeySecret
                    );


            /*
             * Convert rupees to paise.
             *
             * Example:
             *
             * ₹1299
             *   ↓
             * 129900 paise
             */
            long amountInPaise =
                    subscription
                            .getPrice()
                            .multiply(
                                    BigDecimal.valueOf(100)
                            )
                            .longValueExact();


            /*
             * Prepare Razorpay order.
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


            orderRequest.put(
                    "receipt",
                    "sandbox_" + System.nanoTime()
            );


            /*
             * Create order on Razorpay.
             */
            Order razorpayOrder =
                    razorpayClient.orders.create(
                            orderRequest
                    );


            /*
             * Razorpay-generated order ID.
             */
            String razorpayOrderId =
                    razorpayOrder.get("id");


            /*
             * Create local payment record.
             *
             * SECURITY:
             *
             * organizationId comes from the
             * authenticated backend user.
             *
             * It does NOT come from React.
             */
            Payment payment =
                    Payment.builder()
                            .organizationId(
                                    organizationId
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
             * Store payment.
             */
            Payment savedPayment =
                    paymentRepository.save(payment);


            /*
             * Return Razorpay Checkout information.
             *
             * Secret key is NEVER returned.
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

            throw new RuntimeException(
                    "Unable to create Razorpay payment order",
                    exception
            );
        }
    }


    /*
     * =========================================================
     * VERIFY RAZORPAY PAYMENT
     * =========================================================
     */
    @Override
    @Transactional
    public Payment verifyPayment(
            PaymentVerificationRequest request) {


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
         * Find local payment using Razorpay order ID.
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
         * =====================================================
         * IDEMPOTENCY
         * =====================================================
         *
         * Same successful verification can safely
         * return the existing payment.
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
         * Prevent same Razorpay payment ID from
         * being used for another payment.
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
             * Prepare Razorpay verification attributes.
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
             * Cryptographically verify payment.
             */
            boolean signatureValid =
                    Utils.verifyPaymentSignature(
                            verificationAttributes,
                            razorpayKeySecret
                    );


            /*
             * Invalid signature.
             */
            if (!signatureValid) {

                payment.setStatus("FAILED");

                paymentRepository.save(payment);


                throw new IllegalArgumentException(
                        "Payment signature verification failed"
                );
            }


            /*
             * Same timestamp is used for payment
             * and subscription activation.
             */
            LocalDateTime now =
                    LocalDateTime.now();


            /*
             * Mark payment successful.
             */
            payment.setRazorpayPaymentId(
                    request.getRazorpayPaymentId()
            );


            payment.setStatus("SUCCESS");


            payment.setPaidAt(now);


            /*
             * Find organization associated with
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
             * Find purchased subscription.
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
             * =====================================================
             * ACTIVATE ORGANIZATION SUBSCRIPTION
             * =====================================================
             */


            /*
             * Assign purchased plan.
             */
            organization.setSubscription(
                    subscription
            );


            /*
             * Subscription starts when payment
             * is successfully verified.
             */
            organization.setSubscriptionStartAt(
                    now
            );


            /*
             * Calculate expiry using plan duration.
             *
             * Example:
             *
             * Basic = 3 months
             *
             * Aug 05
             *   ↓
             * Nov 05
             */
            organization.setSubscriptionExpiresAt(
                    now.plusMonths(
                            subscription.getDurationMonths()
                    )
            );


            /*
             * Update organization.
             */
            organizationRepository.save(
                    organization
            );


            /*
             * Save successful payment.
             *
             * @Transactional ensures payment and
             * organization update belong to the
             * same DB transaction.
             */
            return paymentRepository.save(
                    payment
            );


        } catch (IllegalArgumentException |
                 IllegalStateException exception) {

            throw exception;


        } catch (Exception exception) {

            throw new RuntimeException(
                    "Unable to verify Razorpay payment",
                    exception
            );
        }
    }


    /*
     * =========================================================
     * GET ALL PAYMENTS
     * =========================================================
     */
    @Override
    public List<Payment> getAllPayments() {

        return paymentRepository.findAll();
    }


    /*
     * =========================================================
     * GET ORGANIZATION PAYMENT HISTORY
     * =========================================================
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
     * =========================================================
     * GET PAYMENTS BY STATUS
     * =========================================================
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


        String normalizedStatus =
                status.trim().toUpperCase();


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