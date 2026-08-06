package com.sandbox.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.sandbox.dto.CreatePaymentOrderRequest;
import com.sandbox.dto.PaymentOrderResponse;
import com.sandbox.dto.PaymentVerificationRequest;
import com.sandbox.entity.Payment;
import com.sandbox.entity.User;
import com.sandbox.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;


    /*
     * HR creates payment order for its OWN organization.
     *
     * organizationId is NOT accepted from frontend.
     */
    @PostMapping("/orders")
    public ResponseEntity<PaymentOrderResponse> createOrder(
            @RequestBody CreatePaymentOrderRequest request,
            Authentication authentication) {

        User currentUser =
                (User) authentication.getPrincipal();

        if (currentUser.getOrganization() == null) {
            throw new IllegalStateException(
                    "User is not associated with an organization"
            );
        }

        Long organizationId =
                currentUser.getOrganization().getId();

        PaymentOrderResponse response =
                paymentService.createOrder(
                        organizationId,
                        request
                );

        return ResponseEntity.ok(response);
    }


    @PostMapping("/verify")
    public ResponseEntity<Payment> verifyPayment(
            @RequestBody PaymentVerificationRequest request) {

        return ResponseEntity.ok(
                paymentService.verifyPayment(request)
        );
    }


    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {

        return ResponseEntity.ok(
                paymentService.getAllPayments()
        );
    }


    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<Payment>>
            getPaymentsByOrganization(
                    @PathVariable Long organizationId) {

        return ResponseEntity.ok(
                paymentService.getPaymentsByOrganization(
                        organizationId
                )
        );
    }


    @GetMapping("/status/{status}")
    public ResponseEntity<List<Payment>>
            getPaymentsByStatus(
                    @PathVariable String status) {

        return ResponseEntity.ok(
                paymentService.getPaymentsByStatus(status)
        );
    }
}