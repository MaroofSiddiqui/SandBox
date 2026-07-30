import { useEffect, useRef, useState, useCallback } from 'react';
import { uploadViolationEvidence, logViolationEvent } from '../api/proctoringService';
import { initializeFaceDetector, detectFacesInFrame } from '../utils/faceDetectionService';

/**
 * Custom React Hook: useProctoring
 *
 * Enforces security guardrails and real-time monitoring during online assessments:
 * 1. Stream Validation: Mandates entire screen sharing ("monitor") and blocks single window/tab shares.
 * 2. Active Track Monitoring: Immediately locks the assessment if screen sharing is stopped.
 * 3. Breach Event Handlers: Intercepts Alt+Tab, Ctrl+Tab OS application switching, copy/paste attempts, 
 *    and fullscreen exits (applies a uniform 5-second grace period across all focus/navigation loss events).
 * 4. AI Vision Engine: Uses Google MediaPipe to monitor candidate presence with a 3-second grace period.
 *    Logs JSON violation reports directly to backend without recording video files.
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
  const isModifierPressedRef = useRef(false);

  // AI Vision Detection Timers (3-second continuous grace timers)
  const noFaceTimerRef = useRef(0);
  const multiFaceTimerRef = useRef(0);
  const aiAnimationFrameRef = useRef(null);

  // Persistent Stream References (Prevents stale closures in event handlers)
  const webcamStreamRef = useRef(null);
  const screenStreamRef = useRef(null);

  /**
   * Triggers a silent 30-second dual webcam and screen video buffer recording ONLY for 
   * OS application switching / focus breach events after the 5-second grace period expires.
   *
   * @param {string} eventType - The classification key of the violation.
   */
  const trigger30SecRecording = useCallback((eventType) => {
    console.warn(`[INTERNAL AUDIT LOG]: Recording Triggered -> ${eventType} at ${new Date().toISOString()}`);

    // Concurrency Lock: Do not start another recording session if one is actively recording
    if (isRecordingRef.current) {
      console.log(`[Proctoring Engine]: Dual video buffer actively recording for prior event. Event ${eventType} logged silently.`);
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

      let options = { mimeType: 'video/webm' };
      if (!MediaRecorder.isTypeSupported('video/webm')) {
        options = { mimeType: '' };
      }

      let recordedWebcamBlob = null;
      let recordedScreenBlob = null;

      const checkAndUpload = () => {
        if (recordedWebcamBlob && recordedScreenBlob) {
          console.log(`[Proctoring Engine]: Dual video buffers finalized for event: [${eventType}]. Dispatching video evidence to backend...`);
          
          uploadViolationEvidence({
            webcamBlob: recordedWebcamBlob,
            screenBlob: recordedScreenBlob,
            violationType: eventType
          })
            .catch((err) => {
              console.warn(`[Proctoring Engine]: Background evidence upload failed for event [${eventType}] (Backend offline):`, err.message);
            })
            .finally(() => {
              isRecordingRef.current = false;
            });
        }
      };

      // 1. Webcam MediaRecorder
      const webcamChunks = [];
      const webcamRecorder = new MediaRecorder(currentWebcam, options);
      webcamRecorderRef.current = webcamRecorder;

      webcamRecorder.ondataavailable = (e) => {
        if (e.data && e.data.size > 0) webcamChunks.push(e.data);
      };

      webcamRecorder.onstop = () => {
        recordedWebcamBlob = new Blob(webcamChunks, { type: 'video/webm' });
        checkAndUpload();
      };

      // 2. Screen MediaRecorder
      const screenChunks = [];
      const screenRecorder = new MediaRecorder(currentScreen, options);
      screenRecorderRef.current = screenRecorder;

      screenRecorder.ondataavailable = (e) => {
        if (e.data && e.data.size > 0) screenChunks.push(e.data);
      };

      screenRecorder.onstop = () => {
        recordedScreenBlob = new Blob(screenChunks, { type: 'video/webm' });
        checkAndUpload();
      };

      console.log(`🎥 [Proctoring Engine]: Started 30-second dual video recording specifically for event: [${eventType}]`);

      webcamRecorder.start();
      screenRecorder.start();

      // Automatically stop recording after 30 seconds
      setTimeout(() => {
        if (webcamRecorder.state !== 'inactive') webcamRecorder.stop();
        if (screenRecorder.state !== 'inactive') screenRecorder.stop();
      }, 30000);

    } catch (error) {
      console.error("[Proctoring Engine]: Error starting MediaRecorders:", error);
      isRecordingRef.current = false;
    }
  }, []);

  /**
   * Dispatches a pure JSON violation log report to the backend without recording video.
   *
   * @param {string} violationType - Internal classification identifier.
   * @param {string} customMessage - Human readable text for modal display.
   */
  const logReportOnlyViolation = useCallback((violationType, customMessage) => {
    console.warn(`[INTERNAL AUDIT LOG]: Reporting Violation (No Video) -> ${violationType}`);

    setWarning({
      isOpen: true,
      text: customMessage,
      violationType: violationType
    });

    if (typeof logViolationEvent === 'function') {
      logViolationEvent({ violationType, timestamp: new Date().toISOString() })
        .catch((err) => console.warn('[Proctoring Engine]: Backend offline for violation logging:', err.message));
    }
  }, []);

  /**
   * Starts a 5-second grace period timer for ALL focus loss, tab switches, Alt+Tab, and fullscreen exits.
   *
   * @param {string} violationType - Internal classification identifier.
   */
  const startAwayTimer = useCallback((violationType) => {
    if (awayTimerRef.current) return;

    console.log(`[Proctoring Engine]: Focus/Security lost (${violationType}). Starting 5-second grace period timer...`);

    awayTimerRef.current = setTimeout(() => {
      setWarning({
        isOpen: true,
        text: 'Violation event triggered. An assessment policy breach has been detected and logged.',
        violationType: violationType
      });

      trigger30SecRecording(violationType);
      awayTimerRef.current = null;
    }, 5000); // 5 second grace period
  }, [trigger30SecRecording]);

  /**
   * Clears the 5-second grace period timer if candidate returns in time.
   */
  const clearAwayTimer = useCallback(() => {
    if (awayTimerRef.current) {
      clearTimeout(awayTimerRef.current);
      awayTimerRef.current = null;
      console.log("[Proctoring Engine]: Candidate returned within 5 seconds. Grace timer cleared.");
    }
  }, []);

  /**
   * Requests and validates Webcam & Screen Capture streams.
   */
  const requestMediaStreams = useCallback(async () => {
    if (isInitializingRef.current) return;
    isInitializingRef.current = true;

    try {
      if (screenStreamRef.current) {
        screenStreamRef.current.getTracks().forEach((track) => track.stop());
      }

      let userWebcamStream = webcamStreamRef.current;
      if (!userWebcamStream || !userWebcamStream.active) {
        userWebcamStream = await navigator.mediaDevices.getUserMedia({
          video: { width: 640, height: 480 },
          audio: false
        });
      }

      const userScreenStream = await navigator.mediaDevices.getDisplayMedia({
        video: { displaySurface: 'monitor' },
        audio: false
      });

      const videoTrack = userScreenStream.getVideoTracks()[0];
      const settings = videoTrack.getSettings();

      if (settings.displaySurface && settings.displaySurface !== 'monitor') {
        videoTrack.stop();
        setScreenShareError('ENTIRE_SCREEN_REQUIRED');
        setScreenStream(null);
        screenStreamRef.current = null;
        isInitializingRef.current = false;
        return;
      }

      videoTrack.onended = () => {
        setScreenStream(null);
        screenStreamRef.current = null;
        setScreenShareError('SCREEN_SHARE_STOPPED');
      };

      setWebcamStream(userWebcamStream);
      setScreenStream(userScreenStream);
      webcamStreamRef.current = userWebcamStream;
      screenStreamRef.current = userScreenStream;
      setScreenShareError(null);

    } catch (err) {
      console.error('[Proctoring Engine]: Stream permission denied:', err);
      setScreenShareError('SCREEN_SHARE_DENIED');
      setScreenStream(null);
      screenStreamRef.current = null;
    } finally {
      isInitializingRef.current = false;
    }
  }, []);

  /**
   * Mount Effect: Stream setup & teardown
   */
  useEffect(() => {
    let isMounted = true;

    const init = async () => {
      if (isMounted) await requestMediaStreams();
    };

    init();

    return () => {
      isMounted = false;
      if (webcamStreamRef.current) webcamStreamRef.current.getTracks().forEach((track) => track.stop());
      if (screenStreamRef.current) screenStreamRef.current.getTracks().forEach((track) => track.stop());
    };
  }, [requestMediaStreams]);

  /**
   * AI Face Detection Frame Loop Effect:
   * Uses a 3-second continuous grace period (90 frames at 30 FPS).
   * Logs violation report ONLY (no video recording).
   */
  useEffect(() => {
    let active = true;

    // Pause frame scanning while a warning modal is active
    if (warning.isOpen) return;

    const startAIVision = async () => {
      try {
        await initializeFaceDetector();

        const processFrame = () => {
          if (!active) return;

          const videoElement = document.querySelector('video');

          if (videoElement && videoElement.readyState >= 2) {
            const faceCount = detectFacesInFrame(videoElement, performance.now());

            if (faceCount === 0) {
              noFaceTimerRef.current += 1;
              multiFaceTimerRef.current = 0;

              if (noFaceTimerRef.current > 90) {
                noFaceTimerRef.current = 0;
                logReportOnlyViolation(
                  'NO_FACE_DETECTED',
                  'No candidate detected in frame! Please ensure your webcam is uncovered and face is visible.'
                );
              }
            } else if (faceCount > 1) {
              multiFaceTimerRef.current += 1;
              noFaceTimerRef.current = 0;

              if (multiFaceTimerRef.current > 90) {
                multiFaceTimerRef.current = 0;
                logReportOnlyViolation(
                  'MULTIPLE_FACES_DETECTED',
                  'Multiple faces detected in frame! Please ensure you are completely alone during the exam.'
                );
              }
            } else if (faceCount === 1) {
              noFaceTimerRef.current = 0;
              multiFaceTimerRef.current = 0;
            }
          }

          aiAnimationFrameRef.current = requestAnimationFrame(processFrame);
        };

        processFrame();
      } catch (err) {
        console.error('[Proctoring Engine]: Failed to start AI vision detector loop:', err);
      }
    };

    if (webcamStream) {
      startAIVision();
    }

    return () => {
      active = false;
      if (aiAnimationFrameRef.current) {
        cancelAnimationFrame(aiAnimationFrameRef.current);
      }
    };
  }, [webcamStream, warning.isOpen, logReportOnlyViolation]);

  /**
   * Keyboard & OS Focus Security Event Listeners (All given 5-second grace period)
   */
  useEffect(() => {
    const handleKeyDown = (e) => {
      if (e.key === 'Alt' || e.key === 'Control' || e.key === 'Meta') {
        isModifierPressedRef.current = true;
      }

      // Intercept Alt+Tab or Ctrl+Tab -> Start 5s timer
      if ((e.altKey || e.ctrlKey) && (e.key === 'Tab' || e.key === 'Escape')) {
        e.preventDefault();
        startAwayTimer('KEYBOARD_APPLICATION_SWITCH');
      }
    };

    const handleKeyUp = (e) => {
      if (e.key === 'Alt' || e.key === 'Control' || e.key === 'Meta') {
        isModifierPressedRef.current = false;
      }
    };

    const handleWindowBlur = () => {
      if (isModifierPressedRef.current) {
        startAwayTimer('KEYBOARD_APPLICATION_SWITCH');
        isModifierPressedRef.current = false;
      } else {
        startAwayTimer('WINDOW_BLUR_OVER_5SEC');
      }
    };

    const handleWindowFocus = () => {
      isModifierPressedRef.current = false;
      clearAwayTimer();
    };

    const handleVisibilityChange = () => {
      if (document.hidden) {
        if (isModifierPressedRef.current) {
          startAwayTimer('KEYBOARD_APPLICATION_SWITCH');
          isModifierPressedRef.current = false;
        } else {
          startAwayTimer('TAB_SWITCH_OVER_5SEC');
        }
      } else {
        clearAwayTimer();
      }
    };

    const handleFullscreenChange = () => {
      if (!document.fullscreenElement) {
        startAwayTimer('FULLSCREEN_EXIT');
      } else {
        clearAwayTimer();
      }
    };

    const handleCopyPaste = (e) => {
      e.preventDefault();
      logReportOnlyViolation('COPY_PASTE_ATTEMPT', 'Copy/paste operations are disabled during the assessment.');
    };

    window.addEventListener('keydown', handleKeyDown);
    window.addEventListener('keyup', handleKeyUp);
    window.addEventListener('blur', handleWindowBlur);
    window.addEventListener('focus', handleWindowFocus);
    document.addEventListener('visibilitychange', handleVisibilityChange);
    document.addEventListener('fullscreenchange', handleFullscreenChange);
    document.addEventListener('copy', handleCopyPaste);
    document.addEventListener('paste', handleCopyPaste);
    document.addEventListener('contextmenu', handleCopyPaste);

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
  }, [startAwayTimer, clearAwayTimer, logReportOnlyViolation]);

  /**
   * Closes warning modal, resets AI counters, and re-enforces Fullscreen mode.
   */
  const closeWarning = () => {
    noFaceTimerRef.current = 0;
    multiFaceTimerRef.current = 0;

    setWarning({ isOpen: false, text: '', violationType: '' });

    if (!document.fullscreenElement && document.documentElement.requestFullscreen) {
      document.documentElement.requestFullscreen().catch((err) => {
        console.error("[Proctoring Engine]: Error re-entering fullscreen mode:", err);
      });
    }
  };

  return { warning, closeWarning, screenShareError, requestMediaStreams, webcamStream };
};