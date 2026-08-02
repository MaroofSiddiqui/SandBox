# 🧪 AI Code Evaluation- Complete Test Report & API Documentation

**Project Module:** AI Code Submission & Evaluation Service  
**Developer & Tester:** Mohit Chourasia  
**Date:** July 30, 2026  
**Overall Status:** Passed ✅

---

## 1. Overview
This document serves as the comprehensive project report, containing the API documentation as well as the test execution results for the AI Proctoring Service (integrating Spring Boot, Judge0, and MongoDB).

---

## 2. API Endpoints Reference

Base URL: `http://localhost:8083`

### A. Code Submission & Evaluation
* **URL:** `/api/evaluations/submit-code`
* **Method:** `POST`
* **Content Type:** `x-www-form-urlencoded`
* **Parameters:**
  * `sourceCode` (String): Complete source code.
  * `languageId` (Integer): Programming language ID (e.g., `62` for Java).

### B. Evaluation Records Management
* **Get All Evaluations:** `GET /api/evaluations`
* **Get Evaluation By ID:** `GET /api/evaluations/{id}`
* **Get Evaluations By Student ID:** `GET /api/evaluations/student/{studentId}`
* **Create Evaluation Record:** `POST /api/evaluations` *(Content-Type: application/json)*

---

## 3. API & Integration Testing Results

### Test Case 1: Successful Java Code Submission & Execution
* **Endpoint:** `POST /api/evaluations/submit-code`
* **Request Body Payload:**
  * `languageId`: `62`
  * `sourceCode`: 
    ```java
    public class Main {
        public static void main(String[] args) {
            System.out.println("Hello World");
        }
    }
    ```
* **Expected Result:** Status `200 OK` with Judge0 compilation success and standard output.
* **Actual Result Response:** 
  ```json
  {
    "stdout": "Hello World\n",
    "time": "0.049",
    "memory": 16236,
    "stderr": null,
    "token": "ea292bb3-7388-497d-bbe5-ccf91f349f66",
    "compile_output": null,
    "message": null,
    "status": { "id": 3, "description": "Accepted" }
  }