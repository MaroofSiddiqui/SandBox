package com.sandbox.assessment.assignment.schedule.controller;

import com.sandbox.assessment.assignment.schedule.dto.AssignmentScheduleRequestDto;
import com.sandbox.assessment.assignment.schedule.dto.AssignmentScheduleResponseDto;
import com.sandbox.assessment.assignment.schedule.service.AssignmentScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ==========================================================
 * Member 4
 *
 * Assignment Schedule APIs
 * ==========================================================
 */
@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AssignmentScheduleController {

    private final AssignmentScheduleService service;

    /**
     * Create Schedule
     */
    @PostMapping
    public ResponseEntity<AssignmentScheduleResponseDto> scheduleAssignment(
            @RequestBody AssignmentScheduleRequestDto requestDto) {

        return new ResponseEntity<>(
                service.scheduleAssignment(requestDto),
                HttpStatus.CREATED);
    }

    /**
     * Get All Schedules
     */
    @GetMapping
    public ResponseEntity<List<AssignmentScheduleResponseDto>>
    getAllSchedules() {

        return ResponseEntity.ok(
                service.getAllSchedules());
    }

    /**
     * Get Schedule By Id
     */
    @GetMapping("/{id}")
    public ResponseEntity<AssignmentScheduleResponseDto>
    getSchedule(@PathVariable Long id) {

        return ResponseEntity.ok(
                service.getScheduleById(id));
    }

    /**
     * Delete Schedule
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSchedule(
            @PathVariable Long id) {

        service.deleteSchedule(id);

        return ResponseEntity.ok(
                "Schedule deleted successfully.");
    }
}