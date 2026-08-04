package com.sandbox.assessment.service;

import java.util.List;

import com.sandbox.assessment.dto.request.AssessmentRequest;
import com.sandbox.assessment.dto.response.AssessmentResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AssessmentService {

	AssessmentResponse createAssessment(AssessmentRequest request);

	List<AssessmentResponse> getAllAssessments();

	AssessmentResponse getAssessmentById(Long id);

	AssessmentResponse updateAssessment(Long id,
	                                    AssessmentRequest request);

	void deleteAssessment(Long id);

	Page<AssessmentResponse> getAllAssessments(Pageable pageable);

	List<AssessmentResponse> searchAssessments(String keyword);

}