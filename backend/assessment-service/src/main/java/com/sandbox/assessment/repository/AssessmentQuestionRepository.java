package com.sandbox.assessment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sandbox.assessment.entity.AssessmentQuestion;

@Repository
public interface AssessmentQuestionRepository
        extends JpaRepository<AssessmentQuestion, Long> {

    boolean existsByAssessmentIdAndQuestionId(
            Long assessmentId,
            Long questionId);
}