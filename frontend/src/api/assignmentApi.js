import axiosInstance from "./axiosInstance";

// Member 4 API Client

// Assignment APIs (/api/assignments)
export const assignAssessment = (assignmentData) => 
  axiosInstance.post("/api/assignments", assignmentData);

export const getAllAssignments = () => 
  axiosInstance.get("/api/assignments");

export const getAssignmentById = (id) => 
  axiosInstance.get(`/api/assignments/${id}`);

export const startAssessmentSession = (id) => 
  axiosInstance.put(`/api/assignments/${id}/start`);

export const submitAssessmentSession = (id) => 
  axiosInstance.put(`/api/assignments/${id}/submit`);

export const deleteAssignment = (id) => 
  axiosInstance.delete(`/api/assignments/${id}`);

export const getAssignmentsByCandidate = (candidateId) => 
  axiosInstance.get(`/api/assignments/candidate/${candidateId}`);

export const getAssignmentsByAssessment = (assessmentId) => 
  axiosInstance.get(`/api/assignments/assessment/${assessmentId}`);

export const getAssignmentsByStatus = (status) => 
  axiosInstance.get(`/api/assignments/status/${status}`);

export const getAssignmentsByCandidateAndStatus = (candidateId, status) => 
  axiosInstance.get(`/api/assignments/candidate/${candidateId}/status/${status}`);

export const getAssignmentsPaged = (page = 0, size = 5, sortBy = "id") => 
  axiosInstance.get(`/api/assignments/page?page=${page}&size=${size}&sortBy=${sortBy}`);

// Analytics & Statistics APIs
export const getAssignmentDashboardAnalytics = () => 
  axiosInstance.get("/api/assignment/dashboard");

export const getAssignmentStatistics = () => 
  axiosInstance.get("/api/assignments/statistics");

// Assessment & Question APIs
export const getAssessmentDetails = (id) => 
  axiosInstance.get(`/assessment/${id}`);

export const getAllAssessmentsList = () => 
  axiosInstance.get("/assessment/all");

export const createAssessment = (dto) => 
  axiosInstance.post("/assessment/create", dto);

export const publishAssessment = (id) => 
  axiosInstance.put(`/assessment/${id}/publish`);

export const getAllQuestionsList = () => 
  axiosInstance.get("/question/all");

export const createQuestion = (dto) => 
  axiosInstance.post("/question/create", dto);
