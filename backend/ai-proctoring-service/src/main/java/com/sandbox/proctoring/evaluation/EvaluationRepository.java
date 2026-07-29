package com.sandbox.proctoring.evaluation;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// Repository interface for database operations on AiEvaluationResult
@Repository
public interface EvaluationRepository extends MongoRepository<AiEvaluationResult, String> {
    
    // Custom query method to find evaluation records by student ID
    List<AiEvaluationResult> findByStudentId(String studentId);
}
