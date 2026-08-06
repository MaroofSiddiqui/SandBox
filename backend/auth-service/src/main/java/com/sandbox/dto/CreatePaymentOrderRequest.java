package com.sandbox.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePaymentOrderRequest {

    /*
     * The organization ID is deliberately NOT accepted
     * from the frontend.
     *
     * The backend obtains the organization from the
     * authenticated HR user.
     */
    private Long subscriptionId;
}