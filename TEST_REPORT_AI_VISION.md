# 🧪 Test Assurance Report: Full-Stack AI Vision, Guardrails & Backend API

**Project Name:** AI-Driven Online Proctoring Engine  
**Feature Branches:** `feature/ai-face-detection` & `feature/backend-proctoring-api`  
**Modules:** Frontend Proctoring Engine (`useProctoring.js` & `faceDetectionService.js`) | Spring Boot Microservice | Cloud MongoDB Atlas (`Cluster0`)  
**Engine & Tools:** Google MediaPipe Tasks Vision (BlazeFace WASM/WebGL), Chrome DevTools, Postman v11 API Suite  
**Overall Status:** ✅ ALL TESTS PASSED  
**Execution Date:** July 31, 2026  

---

# 1. Configured Guardrail Timers & Frontend Verification

All client-side OS listeners, MediaPipe AI vision frame loops, and off-screen composite Canvas PiP recordings were executed and verified in a live assessment session.

| Violation Event | Grace Period | Action Taken on Expiry | Test Result |
| :--- | :--- | :--- | :--- |
| **No Face Detected** | 3 Seconds (90 frames) | Modal Warning + JSON Backend Log (No Video) | **PASSED** |
| **Multiple Faces Detected** | 3 Seconds (90 frames) | Modal Warning + JSON Backend Log (No Video) | **PASSED** |
| **Window Blur / Tab Switch** | 5 Seconds | Modal Warning + 30s Canvas PiP Video Upload | **PASSED** |
| **Alt+Tab / Ctrl+Tab / Alt+Esc** | 5 Seconds | Modal Warning + 30s Canvas PiP Video Upload | **PASSED** |
| **Fullscreen Exit** | 5 Seconds | Modal Warning + 30s Canvas PiP Video Upload | **PASSED** |
| **Copy / Paste / Context Menu** | Instant (0s) | Action Blocked (`preventDefault`) + JSON Backend Log | **PASSED** |
| **Stream Authorization** | Instant (0s) | Rejects single tab/window; requires full screen monitor | **PASSED** |

---

# 2. Key Safeguards & Optimizations

- **AI Loop Pause:** Frame scanning automatically pauses while a warning modal is active on screen to prevent console log spam and unnecessary re-renders.
- **Grace Period Recovery:** Returning to the exam window or re-entering fullscreen within the 5-second grace window clears the timer automatically without penalties.
- **Network Error Handling:** Frontend gracefully catches missing backend endpoints (e.g., when Spring Boot is offline) without crashing the candidate assessment UI.
- **Bandwidth Optimization:** Real-time AI runs locally on the browser edge. Continuous heavy video streaming is replaced by 30-second composite PiP video uploads only upon security breaches, saving **>90%** server upload bandwidth.

---

# 3. Automated Backend API & Persistence Testing (Postman)

## 🧪 Test Case A: JSON Security Violation Audit Log

### Endpoint

```http
POST http://localhost:8080/api/proctoring/log-violation
```

### Headers

```http
Content-Type: application/json
```

### Request Payload

```json
{
  "violationType": "NO_FACE_DETECTED",
  "timestamp": "2026-07-31T03:52:00.000Z"
}
```

### Execution Metrics

| Metric | Result |
|--------|--------|
| HTTP Status | **200 OK** |
| Response Latency | **30 ms** |
| MongoDB Record ID Generated | `6a6bd33f42520835c7c2d5c8` |

### Postman Assertions

- ✅ Status code is **200 OK** → **PASSED**
- ✅ Returns generated MongoDB record ID and violation type → **PASSED**

---

## 🧪 Test Case B: Multipart Canvas PiP Video Evidence Upload

### Endpoint

```http
POST http://localhost:8080/api/proctoring/upload-evidence
```

### Headers

```http
Content-Type: multipart/form-data
```

### Request Form Data

| Key | Type | Value |
|-----|------|-------|
| `violationType` | Text | `FULLSCREEN_EXIT` |
| `webcamVideo` | File | Binary `.webm` Video |

### Returned JSON Payload

```json
{
  "id": "6a6bd65042520835c7c2d5cf",
  "candidateId": "TEMP_CANDIDATE",
  "examId": "TEMP_EXAM",
  "violationType": "FULLSCREEN_EXIT",
  "webcamVideoUrl": "/uploads/evidence/88bce61d_65a1_48cb_b5dd_fc1d6ed5cc86_webcam.webm",
  "createdAt": "2026-07-31T04:25:12.0626146"
}
```

### Execution Metrics

| Metric | Result |
|--------|--------|
| HTTP Status | **200 OK** |
| Response Latency | **227 ms** |
| File Storage Location | `uploads/evidence/` |

### Postman Assertions

- ✅ Status code is **200 OK** → **PASSED**
- ✅ Returns saved violation record with valid `webcamVideoUrl` → **PASSED**

---

# 4. Verification & Sign-off

| Item | Status |
|------|--------|
| **Tested By** | Full-Stack Proctoring Engineer |
| **Overall Status** | ✅ Verified locally across frontend edge AI and backend microservices |
| **Deployment Readiness** | Ready for Pull Request (PR) code review and merge into `main` |

---

