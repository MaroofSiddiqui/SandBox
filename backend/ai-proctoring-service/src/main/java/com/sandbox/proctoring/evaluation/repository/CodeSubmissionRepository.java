package com.sandbox.proctoring.evaluation.repository;

import com.sandbox.proctoring.evaluation.model.CodeSubmission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeSubmissionRepository extends MongoRepository<CodeSubmission, String> {
}