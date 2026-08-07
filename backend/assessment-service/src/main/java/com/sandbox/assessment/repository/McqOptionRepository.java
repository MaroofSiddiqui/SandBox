package com.sandbox.assessment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sandbox.assessment.entity.McqOption;

@Repository
public interface McqOptionRepository
        extends JpaRepository<McqOption, Long> {

}