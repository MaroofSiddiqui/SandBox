package com.sandbox.dto;

import java.math.BigDecimal;

import lombok.*;

/*
 * PAYMENT ORDER RESPONSE
 *
 * Purpose:
 * Returned to the frontend after the backend
 * successfully creates a Razorpay order.
 *
 * The frontend will use this information to
 * launch Razorpay Checkout.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentOrderResponse {

    /*
     * Internal SandBox payment record ID.
     */
    private Long paymentId;


    /*
     * Razorpay-generated order ID.
     *
     * Example:
     *
     * order_Qabc123xyz
     */
    private String razorpayOrderId;


    /*
     * Subscription amount.
     */
    private BigDecimal amount;


    /*
     * Currency used for the transaction.
     *
     * Example:
     *
     * INR
     */
    private String currency;


    /*
     * Subscription plan being purchased.
     */
    private String planName;


    /*
     * Razorpay public key.
     *
     * This key can safely be provided to
     * Razorpay Checkout on the frontend.
     *
     * The Razorpay SECRET must NEVER be
     * returned to the frontend.
     */
    private String razorpayKey;
}