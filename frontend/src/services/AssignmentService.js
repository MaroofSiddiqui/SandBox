import AssessmentApi from "../api/AssessmentApi";

const AssignmentService = {

    assignAssessment(data) {
        return AssessmentApi.post("/assignments", data);
    },

    getAllAssignments() {
        return AssessmentApi.get("/assignments");
    },

    getAssignmentById(id) {
        return AssessmentApi.get(`/assignments/${id}`);
    },

    getAssignmentsByCandidate(candidateId) {
        return AssessmentApi.get(`/assignments/candidate/${candidateId}`);
    },

    getAssignmentsByStatus(status) {
        return AssessmentApi.get(`/assignments/status/${status}`);
    },

    getDashboardAnalytics() {
        return AssessmentApi.get("/assignment/dashboard");
    },

    getStatistics() {
        return AssessmentApi.get("/assignments/statistics");
    },

    startAssessment(id) {
        return AssessmentApi.put(`/assignments/${id}/start`);
    },

    submitAssessment(id) {
        return AssessmentApi.put(`/assignments/${id}/submit`);
    },

    deleteAssignment(id) {
        return AssessmentApi.delete(`/assignments/${id}`);
    }
};

export default AssignmentService;