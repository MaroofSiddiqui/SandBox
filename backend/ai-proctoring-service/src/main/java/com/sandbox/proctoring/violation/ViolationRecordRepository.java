package com.sandbox.proctoring.violation;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViolationRecordRepository extends MongoRepository<ViolationRecord, String> {
    
    // auto generates db search query by method name
    List<ViolationRecord> findByCandidateIdAndExamId(String candidateId, String examId);
}