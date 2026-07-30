# 🧪 Test Assurance Report: AI Vision & Proctoring Timers

**Feature Branch:** `feature/ai-face-detection`  
**Module:** Frontend Proctoring Engine (`useProctoring.js` & `faceDetectionService.js`)  
**Engine:** Google MediaPipe Tasks Vision (BlazeFace WASM/WebGL)  
**Status:** ✅ ALL TESTS PASSED  

---

## 1. Configured Guardrail Timers

| Violation Event | Grace Period | Action Taken on Expiry |
| :--- | :--- | :--- |
| **No Face Detected** | 3 Seconds (90 frames) | Modal Warning + JSON Backend Log (No Video) |
| **Multiple Faces Detected** | 3 Seconds (90 frames) | Modal Warning + JSON Backend Log (No Video) |
| **Window Blur / Tab Switch** | 5 Seconds | Modal Warning + 30s Dual Video Buffer Recording |
| **Alt+Tab / Ctrl+Tab / Alt+Esc** | 5 Seconds | Modal Warning + 30s Dual Video Buffer Recording |
| **Fullscreen Exit** | 5 Seconds | Modal Warning + 30s Dual Video Buffer Recording |
| **Copy / Paste / Context Menu** | Instant (0s) | Action Blocked + JSON Backend Log (No Video) |

---

## 2. Key Safeguards & Optimizations
* **AI Loop Pause:** Frame scanning automatically pauses while a warning modal is active on screen to prevent console log spam and unnecessary re-renders.
* **Grace Period Recovery:** Returning to the exam window or re-entering fullscreen within the 5-second window clears the timer automatically without penalties.
* **Event-Specific Logging:** Video recordings explicitly output the triggering event name (e.g., `[TAB_SWITCH_OVER_5SEC]`) in the console.
* **Network Error Handling:** Frontend gracefully catches missing backend endpoints (e.g., when Spring Boot is offline) without crashing the candidate assessment UI.

---

## 3. Verification & Sign-off
* **Tested By:** Proctoring Module Developer
* **Status:** Verified locally and ready for Pull Request (PR) code review.