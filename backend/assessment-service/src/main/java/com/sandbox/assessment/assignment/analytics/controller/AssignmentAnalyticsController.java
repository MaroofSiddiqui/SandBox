package com.sandbox.assessment.assignment.analytics.controller;

import com.sandbox.assessment.assignment.analytics.dto.AssignmentAnalyticsDto;
import com.sandbox.assessment.assignment.analytics.service.AssignmentAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Assignment Dashboard APIs
 *
 * Member 4
 */
@RestController
@RequestMapping("/api/assignment/dashboard")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AssignmentAnalyticsController {

    private final AssignmentAnalyticsService analyticsService;

    @GetMapping
    public AssignmentAnalyticsDto dashboard(){

        return analyticsService.getDashboard();

    }

}