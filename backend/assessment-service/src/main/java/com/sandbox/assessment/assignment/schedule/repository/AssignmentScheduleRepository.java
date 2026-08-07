package com.sandbox.assessment.assignment.schedule.repository;

import com.sandbox.assessment.assignment.schedule.entity.AssignmentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Assignment Scheduling.
 */
@Repository
public interface AssignmentScheduleRepository
        extends JpaRepository<AssignmentSchedule, Long> {

    /**
     * Returns schedule using assignment id.
     */
    Optional<AssignmentSchedule> findByAssignmentId(Long assignmentId);
}