package com.sandbox.assessment.assignment.service.impl;

import com.sandbox.assessment.assignment.dto.AssignmentRequestDto;
import com.sandbox.assessment.assignment.dto.AssignmentResponseDto;
import com.sandbox.assessment.assignment.entity.Assignment;
import com.sandbox.assessment.assignment.enums.AssignmentStatus;
import com.sandbox.assessment.assignment.exception.AssignmentNotFoundException;
import com.sandbox.assessment.assignment.exception.DuplicateAssignmentException;
import com.sandbox.assessment.assignment.repository.AssignmentRepository;
import com.sandbox.assessment.assignment.service.AssignmentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;

    /**
     * Assign Assessment
     */
    @Override
    public AssignmentResponseDto assignAssessment(AssignmentRequestDto requestDto) {

        log.info("Assigning Assessment {} to Candidate {}",
                requestDto.getAssessmentId(),
                requestDto.getCandidateId());

        if (assignmentRepository.existsByAssessmentIdAndCandidateId(
                requestDto.getAssessmentId(),
                requestDto.getCandidateId())) {

            log.warn("Duplicate assignment detected. Assessment={} Candidate={}",
                    requestDto.getAssessmentId(),
                    requestDto.getCandidateId());

            throw new DuplicateAssignmentException(
                    "Candidate is already assigned to this assessment.");
        }

        Assignment assignment = Assignment.builder()
                .assessmentId(requestDto.getAssessmentId())
                .candidateId(requestDto.getCandidateId())
                .status(AssignmentStatus.ASSIGNED)
                .build();

        Assignment saved = assignmentRepository.save(assignment);

        log.info("Assignment created successfully with ID {}", saved.getId());

        return mapToResponseDto(saved);
    }

    /**
     * Get All Assignments
     */
    @Override
    public List<AssignmentResponseDto> getAllAssignments() {

        log.info("Fetching all assignments");

        return assignmentRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Get Assignment By ID
     */
    @Override
    public AssignmentResponseDto getAssignmentById(Long assignmentId) {

        log.info("Fetching assignment {}", assignmentId);

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> {

                    log.error("Assignment not found {}", assignmentId);

                    return new AssignmentNotFoundException(
                            "Assignment not found with ID : " + assignmentId);
                });

        return mapToResponseDto(assignment);
    }

    /**
     * Start Assessment
     */
    @Override
    public AssignmentResponseDto startAssessment(Long assignmentId) {

        log.info("Starting assessment {}", assignmentId);

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> {

                    log.error("Assignment not found {}", assignmentId);

                    return new AssignmentNotFoundException(
                            "Assignment not found with ID : " + assignmentId);
                });

        assignment.setStatus(AssignmentStatus.IN_PROGRESS);
        assignment.setStartedAt(LocalDateTime.now());

        Assignment updated = assignmentRepository.save(assignment);

        log.info("Assessment {} started successfully", assignmentId);

        return mapToResponseDto(updated);
    }

    /**
     * Submit Assessment
     */
    @Override
    public AssignmentResponseDto submitAssessment(Long assignmentId) {

        log.info("Submitting assessment {}", assignmentId);

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> {

                    log.error("Assignment not found {}", assignmentId);

                    return new AssignmentNotFoundException(
                            "Assignment not found with ID : " + assignmentId);
                });

        assignment.setStatus(AssignmentStatus.SUBMITTED);
        assignment.setSubmittedAt(LocalDateTime.now());

        Assignment updated = assignmentRepository.save(assignment);

        log.info("Assessment {} submitted successfully", assignmentId);

        return mapToResponseDto(updated);
    }

    /**
     * Delete Assignment
     */
    @Override
    public void deleteAssignment(Long assignmentId) {

        log.info("Deleting assignment {}", assignmentId);

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> {

                    log.error("Assignment not found {}", assignmentId);

                    return new AssignmentNotFoundException(
                            "Assignment not found with ID : " + assignmentId);
                });

        assignmentRepository.delete(assignment);

        log.info("Assignment {} deleted successfully", assignmentId);
    }

    /**
     * Convert Entity -> DTO
     */
    private AssignmentResponseDto mapToResponseDto(Assignment assignment) {

        return AssignmentResponseDto.builder()
                .assignmentId(assignment.getId())
                .assessmentId(assignment.getAssessmentId())
                .candidateId(assignment.getCandidateId())
                .status(assignment.getStatus())
                .assignedAt(assignment.getAssignedAt())
                .startedAt(assignment.getStartedAt())
                .submittedAt(assignment.getSubmittedAt())
                .build();
    }

	@Override
	public List<AssignmentResponseDto> getAssignmentsByCandidate(Long candidateId) {
		log.info("Fetching assignments for candidate {}", candidateId);
		return assignmentRepository.findByCandidateId(candidateId)
				.stream()
				.map(this::mapToResponseDto)
				.collect(Collectors.toList());
	}

	@Override
	public List<AssignmentResponseDto> getAssignmentsByAssessment(Long assessmentId) {
		log.info("Fetching assignments for assessment {}", assessmentId);
		return assignmentRepository.findByAssessmentId(assessmentId)
				.stream()
				.map(this::mapToResponseDto)
				.collect(Collectors.toList());
	}

	@Override
	public Page<AssignmentResponseDto> getAssignments(int page, int size, String sortBy) {
		log.info("Fetching assignments page={} size={} sortBy={}", page, size, sortBy);
		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortBy));
		return assignmentRepository.findAll(pageRequest).map(this::mapToResponseDto);
	}

	@Override
	public List<AssignmentResponseDto> getAssignmentsByStatus(AssignmentStatus status) {
		log.info("Fetching assignments with status {}", status);
		return assignmentRepository.findByStatus(status)
				.stream()
				.map(this::mapToResponseDto)
				.collect(Collectors.toList());
	}

	@Override
	public List<AssignmentResponseDto> getAssignmentsByCandidateAndStatus(Long candidateId, AssignmentStatus status) {
		log.info("Fetching assignments for candidate {} with status {}", candidateId, status);
		return assignmentRepository.findByCandidateIdAndStatus(candidateId, status)
				.stream()
				.map(this::mapToResponseDto)
				.collect(Collectors.toList());
	}
}