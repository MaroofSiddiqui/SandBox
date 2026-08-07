package com.sandbox.assessment.assignment.schedule.service.impl;

import com.sandbox.assessment.assignment.schedule.dto.AssignmentScheduleRequestDto;
import com.sandbox.assessment.assignment.schedule.dto.AssignmentScheduleResponseDto;
import com.sandbox.assessment.assignment.schedule.entity.AssignmentSchedule;
import com.sandbox.assessment.assignment.schedule.repository.AssignmentScheduleRepository;
import com.sandbox.assessment.assignment.schedule.service.AssignmentScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ==========================================================
 * Member 4
 *
 * Assignment Scheduling Service
 * ==========================================================
 */
@Service
@RequiredArgsConstructor
public class AssignmentScheduleServiceImpl
        implements AssignmentScheduleService {

    private final AssignmentScheduleRepository repository;

    @Override
    public AssignmentScheduleResponseDto scheduleAssignment(
            AssignmentScheduleRequestDto requestDto) {

        // Prevent duplicate schedule
        repository.findByAssignmentId(requestDto.getAssignmentId())
                .ifPresent(schedule -> {
                    throw new RuntimeException(
                            "Schedule already exists for this assignment.");
                });

        // Validate dates
        if (requestDto.getEndTime()
                .isBefore(requestDto.getStartTime())) {

            throw new RuntimeException(
                    "End time cannot be before start time.");
        }

        AssignmentSchedule schedule = AssignmentSchedule.builder()
                .assignmentId(requestDto.getAssignmentId())
                .startTime(requestDto.getStartTime())
                .endTime(requestDto.getEndTime())
                .maxAttempts(requestDto.getMaxAttempts())
                .active(true)
                .build();

        AssignmentSchedule saved = repository.save(schedule);

        return map(saved);
    }

    @Override
    public List<AssignmentScheduleResponseDto> getAllSchedules() {

        return repository.findAll()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    @Override
    public AssignmentScheduleResponseDto getScheduleById(Long id) {

        AssignmentSchedule schedule = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Schedule not found."));

        return map(schedule);
    }

    @Override
    public void deleteSchedule(Long id) {

        AssignmentSchedule schedule = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Schedule not found."));

        repository.delete(schedule);
    }

    /**
     * Entity → DTO
     */
    private AssignmentScheduleResponseDto map(
            AssignmentSchedule schedule) {

        return AssignmentScheduleResponseDto.builder()
                .id(schedule.getId())
                .assignmentId(schedule.getAssignmentId())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .maxAttempts(schedule.getMaxAttempts())
                .active(schedule.getActive())
                .createdAt(schedule.getCreatedAt())
                .build();
    }
}