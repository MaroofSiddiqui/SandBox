import { useEffect, useRef, useState, useCallback } from 'react';
import { uploadViolationEvidence } from '../api/proctoringService';

/**
 * Custom React Hook: useProctoring
 *
 * Enforces automated security guardrails and real-time monitoring during online assessments:
 * 1. Stream Validation: Mandates entire screen sharing ("monitor") and blocks single window/tab shares.
 * 2. Active Track Monitoring: Immediately locks the assessment if screen sharing is stopped by candidate.
 * 3. Breach Event Handlers: Intercepts Alt+Tab OS application switching, copy/paste attempts, 
 *    fullscreen exits, and applies a 5-second grace timer to general window blur/tab switches.
 * 4. Dual Video Evidence Buffers: Silently records 30-second .webm clips of both webcam and screen 
 *    when a security violation occurs, then automatically dispatches them to the backend API.
 *
 * @returns {Object} Hook state and control methods:
 * @returns {Object} return.warning - Modal visibility state and message string.
 * @returns {Function} return.closeWarning - Modal dismissal handler that re-requests fullscreen.
 * @returns {string|null} return.screenShareError - Active error status blocking the assessment.
 * @returns {Function} return.requestMediaStreams - Stream initialization and recovery method.
 * @returns {MediaStream|null} return.webcamStream - Active webcam stream for UI preview rendering.
 */
export const useProctoring = () => {
  // State for security warning overlay
  const [warning, setWarning] = useState({ isOpen: false, text: '', violationType: '' });
  
  // State for blocking screen share error status ('ENTIRE_SCREEN_REQUIRED' | 'SCREEN_SHARE_STOPPED' | 'SCREEN_SHARE_DENIED')
  const [screenShareError, setScreenShareError] = useState(null);
  
  // Active Media Stream states
  const [webcamStream, setWebcamStream] = useState(null);
  const [screenStream, setScreenStream] = useState(null);

  // Concurrency & Synchronization Refs
  const isRecordingRef = useRef(false);
  const isInitializingRef = useRef(false);
  const webcamRecorderRef = useRef(null);
  const screenRecorderRef = useRef(null);
  const awayTimerRef = useRef(null);
  const isAltPressedRef = useRef(false);

  // Persistent Stream References (Prevents stale closures in event handlers)
  const webcamStreamRef = useRef(null);
  const screenStreamRef = useRef(null);

  /**
   * Triggers a silent 30-second dual webcam and screen video buffer recording upon violation,
   * and dispatches the recorded Blobs to the backend API via uploadViolationEvidence.
   *
   * @param {string} eventType - The classification key of the violation (logged for audit trail).
   */
  const trigger30SecRecording = useCallback((eventType) => {
    console.warn(`[INTERNAL AUDIT LOG]: Violation -> ${eventType} at ${new Date().toISOString()}`);

    // Concurrency Lock: Do not start another recording session if one is active
    if (isRecordingRef.current) {
      console.log("[Proctoring Engine]: Dual video buffer actively recording. Event logged silently.");
      return;
    }

    const currentWebcam = webcamStreamRef.current;
    const currentScreen = screenStreamRef.current;

    if (!currentWebcam || !currentScreen) {
      console.error("[Proctoring Engine]: Active webcam or screen stream unavailable for video capture.");
      return;
    }

    try {
      isRecordingRef.current = true;

      // Fallback MIME type configuration for cross-browser support
      let options = { mimeType: 'video/webm' };
      if (!MediaRecorder.isTypeSupported('video/webm')) {
        options = { mimeType: '' };
      }

      // Temporary blob holders to synchronize upload once both streams finish
      let recordedWebcamBlob = null;
      let recordedScreenBlob = null;

      // Helper to dispatch upload once both media buffers finalize
      const checkAndUpload = () => {
        if (recordedWebcamBlob && recordedScreenBlob) {
          console.log("[Proctoring Engine]: Both video buffers finalized. Initiating backend evidence upload...");
          
          uploadViolationEvidence({
            webcamBlob: recordedWebcamBlob,
            screenBlob: recordedScreenBlob,
            violationType: eventType
          })
            .catch((err) => {
              console.error("[Proctoring Engine]: Background evidence upload failed:", err);
            })
            .finally(() => {
              isRecordingRef.current = false; // Release recording lock after upload attempt
            });
        }
      };

      // 1. Initialize Webcam MediaRecorder
      const webcamChunks = [];
      const webcamRecorder = new MediaRecorder(currentWebcam, options);
      webcamRecorderRef.current = webcamRecorder;

      webcamRecorder.ondataavailable = (e) => {
        if (e.data && e.data.size > 0) webcamChunks.push(e.data);
      };

      webcamRecorder.onstop = () => {
        recordedWebcamBlob = new Blob(webcamChunks, { type: 'video/webm' });
        console.log("[Proctoring Engine]: Silent 30s WEBCAM Evidence Captured. Size:", recordedWebcamBlob.size, "bytes");
        checkAndUpload();
      };

      // 2. Initialize Screen MediaRecorder
      const screenChunks = [];
      const screenRecorder = new MediaRecorder(currentScreen, options);
      screenRecorderRef.current = screenRecorder;

      screenRecorder.ondataavailable = (e) => {
        if (e.data && e.data.size > 0) screenChunks.push(e.data);
      };

      screenRecorder.onstop = () => {
        recordedScreenBlob = new Blob(screenChunks, { type: 'video/webm' });
        console.log("[Proctoring Engine]: Silent 30s SCREEN Evidence Captured. Size:", recordedScreenBlob.size, "bytes");
        checkAndUpload();
      };

      // Start dual video recording
      webcamRecorder.start();
      screenRecorder.start();
      console.log("[Proctoring Engine]: Started 30-second dual webcam & screen recording buffer...");

      // Automatically terminate recording after 30 seconds
      setTimeout(() => {
        if (webcamRecorder.state !== 'inactive') webcamRecorder.stop();
        if (screenRecorder.state !== 'inactive') screenRecorder.stop();
      }, 30000);

    } catch (error) {
      console.error("[Proctoring Engine]: Error initializing MediaRecorders:", error);
      isRecordingRef.current = false;
    }
  }, []);

  /**
   * Displays immediate warning modal for high-severity violations (e.g., Alt+Tab, Fullscreen Exit).
   *
   * @param {string} violationType - Internal classification identifier.
   */
  const triggerImmediateViolation = useCallback((violationType) => {
    // Cancel any pending grace period timers
    if (awayTimerRef.current) {
      clearTimeout(awayTimerRef.current);
      awayTimerRef.current = null;
    }

    setWarning({
      isOpen: true,
      text: 'Violation event triggered. An assessment policy breach has been detected and logged.',
      violationType: violationType
    });
    trigger30SecRecording(violationType);
  }, [trigger30SecRecording]);

  /**
   * Starts a 5-second grace period timer for low-severity focus loss or tab switching.
   *
   * @param {string} violationType - Internal classification identifier.
   */
  const startAwayTimer = useCallback((violationType) => {
    if (awayTimerRef.current) return;

    console.log("[Proctoring Engine]: Focus lost. Starting 5-second grace period timer...");

    awayTimerRef.current = setTimeout(() => {
      setWarning({
        isOpen: true,
        text: 'Violation event triggered. An assessment policy breach has been detected and logged.',
        violationType: violationType
      });
      trigger30SecRecording(violationType);
      awayTimerRef.current = null;
    }, 5000);
  }, [trigger30SecRecording]);

  /**
   * Clears the grace period timer if candidate returns within 5 seconds.
   */
  const clearAwayTimer = useCallback(() => {
    if (awayTimerRef.current) {
      clearTimeout(awayTimerRef.current);
      awayTimerRef.current = null;
      console.log("[Proctoring Engine]: Candidate returned within 5 seconds. Grace period timer cleared.");
    }
  }, []);

  /**
   * Requests, validates, and initializes dual Webcam and Screen MediaStreams.
   */
  const requestMediaStreams = useCallback(async () => {
    if (isInitializingRef.current) return;
    isInitializingRef.current = true;

    try {
      // Stop existing screen tracks if re-initializing
      if (screenStreamRef.current) {
        screenStreamRef.current.getTracks().forEach((track) => track.stop());
      }

      // Initialize Webcam Stream if inactive
      let userWebcamStream = webcamStreamRef.current;
      if (!userWebcamStream || !userWebcamStream.active) {
        userWebcamStream = await navigator.mediaDevices.getUserMedia({
          video: { width: 640, height: 480 },
          audio: false
        });
      }

      // Initialize Screen Capture Stream
      const userScreenStream = await navigator.mediaDevices.getDisplayMedia({
        video: { displaySurface: 'monitor' }, // Directs browser UI toward entire screen option
        audio: false
      });

      const videoTrack = userScreenStream.getVideoTracks()[0];
      const settings = videoTrack.getSettings();

      // Guardrail 1: Strictly reject single application window or browser tab selections
      if (settings.displaySurface && settings.displaySurface !== 'monitor') {
        videoTrack.stop();
        setScreenShareError('ENTIRE_SCREEN_REQUIRED');
        setScreenStream(null);
        screenStreamRef.current = null;
        isInitializingRef.current = false;
        return;
      }

      // Guardrail 2: Direct track termination listener ('Stop sharing' browser bar action)
      videoTrack.onended = () => {
        console.warn('[Proctoring Engine]: Screen sharing track ended by candidate. Locking exam.');
        setScreenStream(null);
        screenStreamRef.current = null;
        setScreenShareError('SCREEN_SHARE_STOPPED');
      };

      // Synchronize states and persistent refs
      setWebcamStream(userWebcamStream);
      setScreenStream(userScreenStream);
      webcamStreamRef.current = userWebcamStream;
      screenStreamRef.current = userScreenStream;
      setScreenShareError(null);
      
      console.log('[Proctoring Engine]: Dual streams successfully verified and active.');

    } catch (err) {
      console.error('[Proctoring Engine]: Stream permission denied or prompt dismissed:', err);
      setScreenShareError('SCREEN_SHARE_DENIED');
      setScreenStream(null);
      screenStreamRef.current = null;
    } finally {
      isInitializingRef.current = false;
    }
  }, []);

  /**
   * Component Mount Effect: Prompts initial stream request and provides teardown cleanup.
   */
  useEffect(() => {
    let isMounted = true;

    const init = async () => {
      if (isMounted) {
        await requestMediaStreams();
      }
    };

    init();

    return () => {
      isMounted = false;
      if (webcamStreamRef.current) {
        webcamStreamRef.current.getTracks().forEach((track) => track.stop());
      }
      if (screenStreamRef.current) {
        screenStreamRef.current.getTracks().forEach((track) => track.stop());
      }
    };
  }, [requestMediaStreams]);

  /**
   * System-Level Security Event Listeners Registration
   */
  useEffect(() => {
    // Keydown tracker for OS-level Alt+Tab interception
    const handleKeyDown = (e) => {
      if (e.key === 'Alt' || e.key === 'Meta') {
        isAltPressedRef.current = true;
      }

      if (e.altKey && (e.key === 'Tab' || e.key === 'Escape')) {
        e.preventDefault();
        triggerImmediateViolation('ALT_TAB_KEY_COMBINATION');
      }
    };

    const handleKeyUp = (e) => {
      if (e.key === 'Alt' || e.key === 'Meta') {
        isAltPressedRef.current = false;
      }
    };

    // Window focus/blur handlers
    const handleWindowBlur = () => {
      if (isAltPressedRef.current) {
        triggerImmediateViolation('ALT_TAB_SWITCH');
        isAltPressedRef.current = false;
      } else {
        startAwayTimer('WINDOW_BLUR_OVER_5SEC');
      }
    };

    const handleWindowFocus = () => {
      isAltPressedRef.current = false;
      clearAwayTimer();
    };

    // Tab visibility handler
    const handleVisibilityChange = () => {
      if (document.hidden) {
        if (isAltPressedRef.current) {
          triggerImmediateViolation('ALT_TAB_SWITCH');
          isAltPressedRef.current = false;
        } else {
          startAwayTimer('TAB_SWITCH_OVER_5SEC');
        }
      } else {
        clearAwayTimer();
      }
    };

    // Fullscreen change handler
    const handleFullscreenChange = () => {
      if (!document.fullscreenElement) {
        triggerImmediateViolation('FULLSCREEN_EXIT');
      }
    };

    // Clipboard and context menu restrictions
    const handleCopyPaste = (e) => {
      e.preventDefault();
      triggerImmediateViolation('COPY_PASTE_ATTEMPT');
    };

    // Attach global listeners
    window.addEventListener('keydown', handleKeyDown);
    window.addEventListener('keyup', handleKeyUp);
    window.addEventListener('blur', handleWindowBlur);
    window.addEventListener('focus', handleWindowFocus);
    document.addEventListener('visibilitychange', handleVisibilityChange);
    document.addEventListener('fullscreenchange', handleFullscreenChange);
    document.addEventListener('copy', handleCopyPaste);
    document.addEventListener('paste', handleCopyPaste);
    document.addEventListener('contextmenu', handleCopyPaste);

    // Detach listeners on unmount
    return () => {
      window.removeEventListener('keydown', handleKeyDown);
      window.removeEventListener('keyup', handleKeyUp);
      window.removeEventListener('blur', handleWindowBlur);
      window.removeEventListener('focus', handleWindowFocus);
      document.removeEventListener('visibilitychange', handleVisibilityChange);
      document.removeEventListener('fullscreenchange', handleFullscreenChange);
      document.removeEventListener('copy', handleCopyPaste);
      document.removeEventListener('paste', handleCopyPaste);
      document.removeEventListener('contextmenu', handleCopyPaste);
      if (awayTimerRef.current) clearTimeout(awayTimerRef.current);
    };
  }, [triggerImmediateViolation, startAwayTimer, clearAwayTimer]);

  /**
   * Closes warning modal and re-enforces Fullscreen mode.
   */
  const closeWarning = () => {
    setWarning({ isOpen: false, text: '', violationType: '' });

    if (!document.fullscreenElement && document.documentElement.requestFullscreen) {
      document.documentElement.requestFullscreen().catch((err) => {
        console.error("[Proctoring Engine]: Error re-entering fullscreen mode:", err);
      });
    }
  };

  return { warning, closeWarning, screenShareError, requestMediaStreams, webcamStream };
};