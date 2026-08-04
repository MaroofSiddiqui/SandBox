package com.sandbox.proctoring.violation;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentViolationSummaryRepository extends MongoRepository<StudentViolationSummary, String> {
    Optional<StudentViolationSummary> findByCandidateIdAndExamId(String candidateId, String examId);
}