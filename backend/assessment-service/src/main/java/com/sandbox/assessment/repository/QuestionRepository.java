package com.sandbox.assessment.repository;

import com.sandbox.assessment.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByQuestionType(Question.QuestionType questionType);
    List<Question> findByCategory(String category);
}