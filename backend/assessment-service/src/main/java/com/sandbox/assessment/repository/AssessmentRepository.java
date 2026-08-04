package com.sandbox.assessment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sandbox.assessment.entity.Assessment;

public interface AssessmentRepository
        extends JpaRepository<Assessment, Long> {

    List<Assessment> findByTitleContainingIgnoreCase(String keyword);

}