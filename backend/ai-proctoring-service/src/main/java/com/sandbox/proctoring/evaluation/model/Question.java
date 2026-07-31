package com.sandbox.proctoring.evaluation.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "questions")
public class Question {
    
    @Id
    private String id;
    private String title;
    private String description;
    private List<TestCase> testCases; // Wahi TestCase class jo humne pehle banayi thi

    // Constructors
    public Question() {}

    public Question(String title, List<TestCase> testCases) {
        this.title = title;
        this.testCases = testCases;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TestCase> testCases) {
        this.testCases = testCases;
    }
}