package com.sandbox.assessment.assignment.schedule.service;

import com.sandbox.assessment.assignment.schedule.dto.AssignmentScheduleRequestDto;
import com.sandbox.assessment.assignment.schedule.dto.AssignmentScheduleResponseDto;

import java.util.List;

public interface AssignmentScheduleService {

    /**
     * Schedule an assigned assessment.
     */
    AssignmentScheduleResponseDto scheduleAssignment(
            AssignmentScheduleRequestDto requestDto);

    /**
     * Get all schedules.
     */
    List<AssignmentScheduleResponseDto> getAllSchedules();

    /**
     * Get schedule by id.
     */
    AssignmentScheduleResponseDto getScheduleById(Long id);

    /**
     * Delete schedule.
     */
    void deleteSchedule(Long id);
}