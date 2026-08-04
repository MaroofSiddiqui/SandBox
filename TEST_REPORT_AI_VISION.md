# 🧪 Test Assurance Report: AI Proctoring Module

**Project Name:** Sandbox Assessment Platform  
**Repository Branch:** `feature/live-stream-and-mobile-block`  
**Module Tested:** Proctoring Module Controller (Frontend + Backend Integration)  
**Frontend Components:** `useProctoring.js`, `ProctorLiveGrid.jsx`, `faceDetectionService.js`  
**Backend Service:** Spring Boot Microservice (`ai-proctoring-service`)  
**Database:** MongoDB Atlas (`proctoring_db`)  
**Technologies Used:** Google MediaPipe Tasks Vision (BlazeFace WASM/WebGL), Agora RTC SDK (`agora-rtc-sdk-ng`), Spring Boot, MongoDB Atlas, Chrome DevTools, Postman v11  
**Overall Status:** ✅ ALL TESTS PASSED  
**Execution Date:** August 05, 2026  

---

# 1. Guardrail & Frontend Security Validation

The AI proctoring engine was tested under multiple candidate activity scenarios to validate browser security restrictions, AI-based face detection, screen monitoring, and violation handling mechanisms.

| Test Scenario | Trigger / Grace Period | Expected System Behaviour | Result |
|---------------|------------------------|---------------------------|--------|
| Entire Screen Share Enforcement | Immediate | Reject browser tab/window sharing and enforce monitor selection | ✅ PASSED |
| Mobile / Tablet Access Restriction | On Application Load | Block assessment access for non-desktop devices | ✅ PASSED |
| No Face Detected | 3 Seconds | Warning popup with backend violation log | ✅ PASSED |
| Multiple Faces Detected | 3 Seconds | Warning popup with backend violation log | ✅ PASSED |
| Window Blur / Tab Switch | 5 Seconds | Warning popup and 30-second PiP recording upload | ✅ PASSED |
| Alt + Tab / Ctrl + Tab / Alt + Esc | 5 Seconds | Warning popup and evidence recording | ✅ PASSED |
| Fullscreen Exit | Immediate | Warning popup, PiP recording, fullscreen re-entry prompt | ✅ PASSED |
| Copy / Paste / Context Menu | Immediate | Prevent action and log violation | ✅ PASSED |

---

# 2. Functional Verification

The following features were verified successfully during end-to-end testing:

- Enforced entire screen sharing using `navigator.mediaDevices.getDisplayMedia`.
- Browser tab or application window sharing is rejected automatically.
- AI face detection executed successfully using Google MediaPipe Tasks Vision.
- Integrated Agora WebRTC for live camera and microphone streaming.
- HR monitoring dashboard supports a maximum of 12 live candidate tiles per page.
- AI detection loop pauses while warning dialogs are displayed.
- Grace period resets automatically if the candidate returns within the allowed time.
- Frontend remains stable even if backend services are temporarily unavailable.
- Composite Picture-in-Picture (PiP) recordings are uploaded only during violations, significantly reducing bandwidth consumption.

---

# 3. Backend API Testing

## Test Case A – Security Violation Logging

### Endpoint

```http
POST /api/proctoring/log-violation
```

### Request Headers

```http
Content-Type: application/json
```

### Sample Request

```json
{
  "candidateId": "CANDIDATE_101",
  "examId": "EXAM_TEST_01",
  "violationType": "NO_FACE_DETECTED",
  "timestamp": "1785897120000",
  "details": "No face detected in webcam view for more than 3 seconds"
}
```

### Expected Result

| Metric | Result |
|--------|--------|
| HTTP Status | 200 OK |
| Response Latency | 28 ms |
| MongoDB Record Generated | Yes |
| Timestamp Stored | IST |
| Backend Logging | Successful |

**Status:** ✅ PASSED

---

## Test Case B – Evidence Video Upload

### Endpoint

```http
POST /api/proctoring/upload-evidence
```

### Headers

```http
Content-Type: multipart/form-data
```

### Request Parameters

| Field | Type |
|-------|------|
| candidateId | Text |
| examId | Text |
| violationType | Text |
| webcamVideo | WebM File |

### Sample Response

```json
{
  "id": "6a6bd65042520835c7c2d5cf",
  "candidateId": "CANDIDATE_101",
  "examId": "EXAM_TEST_01",
  "violationType": "FULLSCREEN_EXIT",
  "webcamVideoUrl": "/uploads/evidence/sample_video.webm",
  "createdAt": "2026-08-05T02:25:31.062",
  "createdAtIST": "2026-08-05 02:25:31 IST"
}
```

### Expected Result

| Metric | Result |
|--------|--------|
| HTTP Status | 200 OK |
| Response Time | 215 ms |
| Evidence Stored | Successful |
| File Location | `/uploads/evidence/` |

**Status:** ✅ PASSED

---

# 4. Multi-Developer Environment Configuration

Separate Spring Boot profiles were verified for independent developer environments.

| Profile | Server Port | Database |
|----------|------------|----------|
| `application-dev-msaxena.properties` | 8080 | `proctoring_db` |
| `application-dev-mohit.properties` | 8083 | `ai_proctoring_db` |

Common configuration:

- Spring Profiles supported
- MongoDB Atlas connectivity
- Judge0 configuration
- Gemini API configuration
- Maximum file upload size: **50 MB**

---

# 5. Test Summary

| Test Category | Status |
|--------------|--------|
| Frontend Guardrails | ✅ PASSED |
| AI Face Detection | ✅ PASSED |
| WebRTC Live Streaming | ✅ PASSED |
| Backend REST APIs | ✅ PASSED |
| MongoDB Persistence | ✅ PASSED |
| Evidence Upload | ✅ PASSED |
| Security Restrictions | ✅ PASSED |
| End-to-End Integration | ✅ PASSED |

---

# 6. Conclusion

The **AI Proctoring Module Controller** was successfully tested across frontend, backend, AI vision, WebRTC streaming, and MongoDB persistence layers. All configured guardrails operated as expected, API endpoints responded successfully, and evidence logging was validated without failures.

The module is stable, production-ready, and suitable for integration into the **Sandbox Assessment Platform**.

---

# 7. Verification & Sign-off

| Item | Status |
|------|--------|
| Project | Sandbox Assessment Platform |
| Module | Proctoring Module Controller |
| Tested By | **Manthan Saxena** |
| Test Type | Functional Testing, API Testing, Integration Testing |
| Overall Status | ✅ PASSED |
| Deployment Readiness | Ready for Pull Request (PR) Review and Merge |