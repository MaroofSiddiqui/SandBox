import axios from "axios";

const assessmentApi = axios.create({
  baseURL: "http://localhost:8081",
  headers: { "Content-Type": "application/json" },
});

export const getAssessmentById = (id) => assessmentApi.get(`/assessment/${id}`);
export const getAllAssessments = () => assessmentApi.get("/assessment/all");
export const getAllQuestions = () => assessmentApi.get("/question/all");

export default assessmentApi;
