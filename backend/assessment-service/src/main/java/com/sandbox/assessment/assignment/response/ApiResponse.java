package com.sandbox.assessment.assignment.response;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Standard API Response
 *
 * Member 4 Module
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    /**
     * Indicates whether the request succeeded.
     */
    private boolean success;

    /**
     * Response message.
     */
    private String message;

    /**
     * Payload data.
     */
    private T data;

    /**
     * Response timestamp.
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}