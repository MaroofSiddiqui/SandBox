package com.sandbox.proctoring.evaluation.repository;

import com.sandbox.proctoring.evaluation.model.Question;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends MongoRepository<Question, String> {
}
