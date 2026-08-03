import axios from 'axios';

// Base URL pointing to your Spring Boot Backend
const API_BASE_URL = 'http://localhost:8080/api/proctoring';

/**
 * Uploads dual video evidence buffers (webcam and screen) along with violation 
 * metadata to the Spring Boot backend upon a proctoring policy breach.
 *
 * @param {Object} params - Upload parameters.
 * @param {Blob} params.webcamBlob - The 30-second recorded webcam video Blob (.webm).
 * @param {Blob} params.screenBlob - The 30-second recorded screen video Blob (.webm).
 * @param {string} params.violationType - Classification code of the violation.
 * @param {string} [params.candidateId="CANDIDATE_TEMP_ID"] - Unique identifier for the candidate.
 * @param {string} [params.examId="EXAM_TEMP_ID"] - Unique identifier for the active assessment.
 * @returns {Promise<Object>} Backend API response data.
 */
export const uploadViolationEvidence = async ({
  webcamBlob,
  screenBlob,
  violationType,
  candidateId = 'CANDIDATE_TEMP_ID',
  examId = 'EXAM_TEMP_ID'
}) => {
  try {
    const formData = new FormData();

    // 1. Append recorded video Blobs as multipart files
    if (webcamBlob) {
      formData.append('webcamVideo', webcamBlob, `webcam_${Date.now()}.webm`);
    }

    if (screenBlob) {
      formData.append('screenVideo', screenBlob, `screen_${Date.now()}.webm`);
    }

    // 2. Append violation metadata fields
    formData.append('violationType', violationType);
    formData.append('candidateId', candidateId);
    formData.append('examId', examId);
    formData.append('timestamp', new Date().toISOString());

    // 3. Dispatch POST request to Spring Boot backend
    const response = await axios.post(`${API_BASE_URL}/upload-evidence`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    console.log('[Proctoring API]: Evidence uploaded successfully:', response.data);
    return response.data;

  } catch (error) {
    console.error('[Proctoring API]: Error uploading violation evidence:', error);
    throw error;
  }
};

/**
 * Logs a lightweight JSON violation report to the backend (No video files attached).
 * Used for AI face detection events and copy/paste attempts.
 *
 * @param {Object} params - Report parameters.
 * @param {string} params.violationType - Classification code of the violation.
 * @param {string} [params.timestamp] - ISO timestamp of the breach event.
 * @param {string} [params.candidateId="CANDIDATE_TEMP_ID"] - Unique identifier for the candidate.
 * @param {string} [params.examId="EXAM_TEMP_ID"] - Unique identifier for the active assessment.
 * @returns {Promise<Object>} Backend API response data.
 */
export const logViolationEvent = async ({
  violationType,
  timestamp = new Date().toISOString(),
  candidateId = 'CANDIDATE_TEMP_ID',
  examId = 'EXAM_TEMP_ID'
}) => {
  try {
    const response = await axios.post(`${API_BASE_URL}/log-violation`, {
      violationType,
      candidateId,
      examId,
      timestamp,
    });

    console.log('[Proctoring API]: JSON Violation report logged successfully:', response.data);
    return response.data;

  } catch (error) {
    // Graceful warning log so local testing without backend server running won't crash the UI
    console.warn('[Proctoring API]: Backend offline or unreachable for JSON violation report:', error.message);
  }
};