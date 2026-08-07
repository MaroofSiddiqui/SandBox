package com.sandbox.assessment.assignment.statistics.controller;

import com.sandbox.assessment.assignment.statistics.dto.AssignmentStatisticsDto;
import com.sandbox.assessment.assignment.statistics.service.AssignmentStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Assignment Dashboard APIs
 *
 * Member 4
 */
@RestController
@RequestMapping("/api/assignments/statistics")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AssignmentStatisticsController {

    private final AssignmentStatisticsService statisticsService;

    @GetMapping
    public AssignmentStatisticsDto getStatistics() {

        return statisticsService.getStatistics();

    }

}