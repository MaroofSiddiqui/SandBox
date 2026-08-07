import assessmentAxiosInstance from "./assessmentAxiosInstance";

/*
 * ============================================================
 * ASSESSMENT APIs
 * ============================================================
 *
 * Backend:
 * assessment-service
 * Port: 8082
 */


/*
 * CREATE ASSESSMENT
 *
 * POST /assessment/create
 */
export const createAssessment = async (assessmentData) => {

    const response = await assessmentAxiosInstance.post(
        "/assessment/create",
        assessmentData
    );

    return response.data;
};


/*
 * PUBLISH ASSESSMENT
 *
 * PUT /assessment/{id}/publish
 */
export const publishAssessment = async (assessmentId) => {

    const response = await assessmentAxiosInstance.put(
        `/assessment/${assessmentId}/publish`
    );

    return response.data;
};


/*
 * GET ALL ASSESSMENTS
 *
 * GET /assessment/all
 */
export const getAllAssessments = async () => {

    const response = await assessmentAxiosInstance.get(
        "/assessment/all"
    );

    return response.data;
};


/*
 * GET ASSESSMENT BY ID
 *
 * GET /assessment/{id}
 */
export const getAssessmentById = async (assessmentId) => {

    const response = await assessmentAxiosInstance.get(
        `/assessment/${assessmentId}`
    );

    return response.data;
};


/*
 * ============================================================
 * QUESTION APIs
 * ============================================================
 */


/*
 * CREATE QUESTION
 *
 * POST /question/create
 */
export const createQuestion = async (questionData) => {

    const response = await assessmentAxiosInstance.post(
        "/question/create",
        questionData
    );

    return response.data;
};


/*
 * GET ALL QUESTIONS
 *
 * GET /question/all
 */
export const getAllQuestions = async () => {

    const response = await assessmentAxiosInstance.get(
        "/question/all"
    );

    return response.data;
};