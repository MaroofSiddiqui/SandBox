package com.sandbox.assessment.repository;

import com.sandbox.assessment.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    List<Assessment> findByIsPublished(Boolean isPublished);
}