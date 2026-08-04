package com.sandbox.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

/*
 * PAYMENT ENTITY
 *
 * Purpose:
 * Represents a payment made by an organization
 * while purchasing a subscription plan.
 *
 * Payment flow:
 *
 * Organization selects plan
 *        ↓
 * Razorpay order is created
 *        ↓
 * User completes payment
 *        ↓
 * Payment is verified
 *        ↓
 * Payment record is updated
 *
 * Database Table:
 *
 * payments
 */
@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    /*
     * PRIMARY KEY
     *
     * Database-generated unique identifier
     * for every payment record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    /*
     * ORGANIZATION ID
     *
     * Identifies the organization that is
     * purchasing the subscription.
     *
     * We store the ID instead of creating a JPA
     * relationship because organization data may
     * belong to another service/module.
     */
    @Column(nullable = false)
    private Long organizationId;


    /*
     * SUBSCRIPTION ID
     *
     * Identifies the subscription plan selected
     * by the organization.
     */
    @Column(nullable = false)
    private Long subscriptionId;


    /*
     * PAYMENT AMOUNT
     *
     * Amount paid for the selected subscription.
     *
     * BigDecimal is used because monetary values
     * should not be stored using float or double.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;


    /*
     * CURRENCY
     *
     * Currency used for the payment.
     *
     * Example:
     *
     * INR
     */
    @Column(nullable = false)
    private String currency;


    /*
     * RAZORPAY ORDER ID
     *
     * Order identifier returned by Razorpay
     * when a payment order is created.
     *
     * Example:
     *
     * order_Qabc123xyz
     */
    @Column(unique = true)
    private String razorpayOrderId;


    /*
     * RAZORPAY PAYMENT ID
     *
     * Payment identifier returned by Razorpay
     * after the customer completes payment.
     *
     * Example:
     *
     * pay_Qabc123xyz
     */
    @Column(unique = true)
    private String razorpayPaymentId;


    /*
     * PAYMENT STATUS
     *
     * Possible values:
     *
     * CREATED
     * SUCCESS
     * FAILED
     *
     * Initially the payment record is CREATED.
     */
    @Column(nullable = false)
    private String status;


    /*
     * PAYMENT TIMESTAMP
     *
     * Stores when the payment record was created.
     */
    private LocalDateTime createdAt;


    /*
     * PAYMENT COMPLETION TIMESTAMP
     *
     * Stores when the payment was successfully
     * completed and verified.
     *
     * Remains null until payment succeeds.
     */
    private LocalDateTime paidAt;


    /*
     * PRE-PERSIST CALLBACK
     *
     * Automatically executed before a new
     * payment record is inserted.
     *
     * Responsibilities:
     *
     * 1. Store creation timestamp.
     * 2. Set default currency to INR.
     * 3. Set default payment status to CREATED.
     */
    @PrePersist
    public void prePersist() {

        createdAt = LocalDateTime.now();

        if (currency == null) {
            currency = "INR";
        }

        if (status == null) {
            status = "CREATED";
        }
    }
}