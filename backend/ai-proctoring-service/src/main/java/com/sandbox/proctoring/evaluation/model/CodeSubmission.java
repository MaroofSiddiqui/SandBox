package com.sandbox.proctoring.evaluation.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Document(collection = "code_submissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeSubmission {

    @Id
    private String id;
    private String studentId;
    private String problemId;
    private String sourceCode;
    private int languageId; // e.g., 62 for Java, 71 for Python
    private String status;  // Pending, Accepted, Wrong Answer
    private String output;  
    private double score;
    private String questionId;
}