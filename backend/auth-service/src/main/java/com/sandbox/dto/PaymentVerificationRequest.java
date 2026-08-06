package com.sandbox.dto;

import lombok.*;

/*
 * PAYMENT VERIFICATION REQUEST
 *
 * Purpose:
 * Receives payment information returned by
 * Razorpay Checkout after a successful payment.
 *
 * These three values are required for
 * server-side Razorpay signature verification.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentVerificationRequest {

    /*
     * Razorpay order ID originally generated
     * when the payment order was created.
     */
    private String razorpayOrderId;


    /*
     * Razorpay payment ID generated after
     * payment completion.
     */
    private String razorpayPaymentId;


    /*
     * Razorpay signature.
     *
     * The backend will verify this signature
     * using the Razorpay secret key.
     *
     * We must never simply trust the frontend
     * and mark a payment SUCCESS.
     */
    private String razorpaySignature;
}