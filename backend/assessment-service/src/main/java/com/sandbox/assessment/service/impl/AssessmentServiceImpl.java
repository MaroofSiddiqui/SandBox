package com.sandbox.assessment.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sandbox.assessment.dto.request.AssessmentRequest;
import com.sandbox.assessment.dto.response.AssessmentResponse;
import com.sandbox.assessment.entity.Assessment;
import com.sandbox.assessment.exception.ResourceNotFoundException;
import com.sandbox.assessment.mapper.AssessmentMapper;
import com.sandbox.assessment.repository.AssessmentRepository;
import com.sandbox.assessment.service.AssessmentService;

@Service
public class AssessmentServiceImpl implements AssessmentService {

    private static final Logger logger =
            LoggerFactory.getLogger(AssessmentServiceImpl.class);

    private final AssessmentRepository assessmentRepository;

    public AssessmentServiceImpl(AssessmentRepository assessmentRepository) {
        this.assessmentRepository = assessmentRepository;
    }

    // =========================================
    // CREATE ASSESSMENT
    // =========================================
    @Override
    public AssessmentResponse createAssessment(AssessmentRequest request) {

        logger.info("Creating new assessment : {}", request.getTitle());

        Assessment assessment = AssessmentMapper.toEntity(request);

        Assessment savedAssessment = assessmentRepository.save(assessment);

        logger.info("Assessment created successfully with ID : {}",
                savedAssessment.getId());

        return AssessmentMapper.toResponse(savedAssessment);
    }

    // =========================================
    // GET ALL
    // =========================================
    @Override
    public List<AssessmentResponse> getAllAssessments() {

        logger.info("Fetching all assessments");

        return assessmentRepository.findAll()
                .stream()
                .map(AssessmentMapper::toResponse)
                .toList();
    }

    // =========================================
    // GET BY ID
    // =========================================
    @Override
    public AssessmentResponse getAssessmentById(Long id) {

        logger.info("Fetching assessment with ID : {}", id);

        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Assessment not found with ID : {}", id);
                    return new ResourceNotFoundException(
                            "Assessment not found with ID : " + id);
                });

        return AssessmentMapper.toResponse(assessment);
    }

    // =========================================
    // UPDATE
    // =========================================
    @Override
    public AssessmentResponse updateAssessment(Long id,
            AssessmentRequest request) {

        logger.info("Updating assessment with ID : {}", id);

        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Assessment not found with ID : {}", id);
                    return new ResourceNotFoundException(
                            "Assessment not found with ID : " + id);
                });

        assessment.setTitle(request.getTitle());
        assessment.setDescription(request.getDescription());
        assessment.setDurationMinutes(request.getDurationMinutes());
        assessment.setTotalMarks(request.getTotalMarks());
        assessment.setPassingMarks(request.getPassingMarks());
        assessment.setStatus(request.getStatus());
        assessment.setCreatedBy(request.getCreatedBy());

        Assessment updatedAssessment = assessmentRepository.save(assessment);

        logger.info("Assessment updated successfully with ID : {}",
                updatedAssessment.getId());

        return AssessmentMapper.toResponse(updatedAssessment);
    }

    // =========================================
    // DELETE
    // =========================================
    @Override
    public void deleteAssessment(Long id) {

        logger.info("Deleting assessment with ID : {}", id);

        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Assessment not found with ID : {}", id);
                    return new ResourceNotFoundException(
                            "Assessment not found with ID : " + id);
                });

        assessmentRepository.delete(assessment);

        logger.info("Assessment deleted successfully with ID : {}", id);
    }

    // =========================================
    // PAGINATION
    // =========================================
    @Override
    public Page<AssessmentResponse> getAllAssessments(Pageable pageable) {

        logger.info("Fetching assessments with pagination");

        return assessmentRepository.findAll(pageable)
                .map(AssessmentMapper::toResponse);
    }

    // =========================================
    // SEARCH
    // =========================================
    @Override
    public List<AssessmentResponse> searchAssessments(String keyword) {

        logger.info("Searching assessments with keyword : {}", keyword);

        return assessmentRepository.findByTitleContainingIgnoreCase(keyword)
                .stream()
                .map(AssessmentMapper::toResponse)
                .toList();
    }
}