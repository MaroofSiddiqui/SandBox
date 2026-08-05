package com.sandbox.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HrOrganizationResponse {

    private Long organizationId;

    private String organizationName;

    private String organizationStatus;

    private Long subscriptionId;

    private String planName;

    private Integer maxCandidates;

    private LocalDateTime subscriptionStartAt;

    private LocalDateTime subscriptionExpiresAt;

    private boolean subscriptionActive;
}