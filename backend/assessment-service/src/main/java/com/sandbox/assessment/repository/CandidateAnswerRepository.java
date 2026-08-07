package com.sandbox.assessment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sandbox.assessment.entity.CandidateAnswer;

@Repository
public interface CandidateAnswerRepository
        extends JpaRepository<CandidateAnswer, Long> {

    List<CandidateAnswer> findBySubmissionId(Long submissionId);

    Optional<CandidateAnswer>
        findBySubmissionIdAndQuestionId(
            Long submissionId,
            Long questionId
        );
}