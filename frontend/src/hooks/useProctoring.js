import { useEffect, useRef, useState, useCallback } from 'react';
import AgoraRTC from 'agora-rtc-sdk-ng';
import { uploadViolationEvidence, logViolationEvent } from '../api/proctoringService';
import { initializeFaceDetector, detectFacesInFrame } from '../utils/faceDetectionService';

const DEMO_MODE = false;

// Create Agora client for webcam stream
const agoraClient = AgoraRTC.createClient({ mode: 'rtc', codec: 'vp8' });

export const useProctoring = (
  candidateId = 'CANDIDATE_DEFAULT',
  examId = 'EXAM_DEFAULT',
  agoraCredentials = null
) => {
  const [warning, setWarning] = useState({ isOpen: false, text: '', violationType: '' });
  const [screenShareError, setScreenShareError] = useState(null);
  
  const [webcamStream, setWebcamStream] = useState(null);
  const [screenStream, setScreenStream] = useState(null);

  const isRecordingRef = useRef(false);
  const isInitializingRef = useRef(false);
  const isPromptActiveRef = useRef(false);
  const awayTimerRef = useRef(null);
  const isModifierPressedRef = useRef(false);

  const noFaceTimerRef = useRef(0);
  const multiFaceTimerRef = useRef(0);
  const aiAnimationFrameRef = useRef(null);

  const webcamStreamRef = useRef(null);
  const screenStreamRef = useRef(null);
  const localAgoraVideoTrackRef = useRef(null);
  const localAgoraAudioTrackRef = useRef(null);

  // Trigger browser Fullscreen mode
  const enterFullscreen = useCallback(async () => {
    try {
      if (!document.fullscreenElement && document.documentElement.requestFullscreen) {
        await document.documentElement.requestFullscreen();
      }
    } catch (err) {
      console.warn('[Proctoring Engine]: Fullscreen request blocked:', err.message);
    }
  }, []);

  // Publish webcam feed to Agora channel for HR monitoring
  const publishCandidateToAgora = useCallback(async () => {
    if (!agoraCredentials || !agoraCredentials.appId || !agoraCredentials.channelName) {
      console.log('[Proctoring Engine]: Agora credentials missing, skipping live stream.');
      return;
    }

    try {
      const { appId, channelName, token, uid } = agoraCredentials;
      await agoraClient.join(appId, channelName, token || null, uid || null);

      const videoTrack = await AgoraRTC.createCameraVideoTrack();
      const audioTrack = await AgoraRTC.createMicrophoneAudioTrack();

      localAgoraVideoTrackRef.current = videoTrack;
      localAgoraAudioTrackRef.current = audioTrack;

      await agoraClient.publish([videoTrack, audioTrack]);
      console.log('[Proctoring Engine]: Live stream published successfully.');
    } catch (err) {
      console.warn('[Proctoring Engine]: Agora publish failed:', err.message);
    }
  }, [agoraCredentials]);

  // Record 30-second video snippet on security violation
  const trigger30SecRecording = useCallback((eventType) => {
    if (isRecordingRef.current) return;

    const currentWebcam = webcamStreamRef.current;
    const currentScreen = screenStreamRef.current;

    if (!currentWebcam) {
      console.error('[Proctoring Engine]: Webcam stream not found.');
      return;
    }

    try {
      isRecordingRef.current = true;

      const canvas = document.createElement('canvas');
      canvas.width = 1280;
      canvas.height = 720;
      const ctx = canvas.getContext('2d');

      const screenVid = document.createElement('video');
      screenVid.srcObject = currentScreen || currentWebcam;
      screenVid.muted = true;
      screenVid.play();

      const webcamVid = document.createElement('video');
      webcamVid.srcObject = currentWebcam;
      webcamVid.muted = true;
      webcamVid.play();

      let animFrameId;
      const drawFrame = () => {
        ctx.fillStyle = '#000000';
        ctx.fillRect(0, 0, canvas.width, canvas.height);

        if (screenVid.readyState >= 2) {
          ctx.drawImage(screenVid, 0, 0, canvas.width, canvas.height);
        }

        if (webcamVid.readyState >= 2) {
          const pipWidth = 200;
          const pipHeight = 150;
          const margin = 30;
          const x = canvas.width - pipWidth - margin;
          const y = canvas.height - pipHeight - margin;

          ctx.strokeStyle = '#ffffff';
          ctx.lineWidth = 3;
          ctx.strokeRect(x, y, pipWidth, pipHeight);
          ctx.drawImage(webcamVid, x, y, pipWidth, pipHeight);
        }

        animFrameId = requestAnimationFrame(drawFrame);
      };

      drawFrame();

      const compositeStream = canvas.captureStream(30);

      let options = { mimeType: 'video/webm' };
      if (!MediaRecorder.isTypeSupported('video/webm')) {
        options = { mimeType: '' };
      }

      const mergedChunks = [];
      const mediaRecorder = new MediaRecorder(compositeStream, options);

      mediaRecorder.ondataavailable = (e) => {
        if (e.data && e.data.size > 0) mergedChunks.push(e.data);
      };

      mediaRecorder.onstop = () => {
        cancelAnimationFrame(animFrameId);
        const mergedBlob = new Blob(mergedChunks, { type: 'video/webm' });

        uploadViolationEvidence({
          webcamBlob: mergedBlob,
          screenBlob: null,
          violationType: eventType,
          candidateId,
          examId,
          timestamp: Date.now()
        })
          .catch((err) => console.warn('[Proctoring Engine]: Upload failed:', err.message))
          .finally(() => { isRecordingRef.current = false; });
      };

      mediaRecorder.start();

      setTimeout(() => {
        if (mediaRecorder.state !== 'inactive') mediaRecorder.stop();
      }, 30000);

    } catch (error) {
      console.error('[Proctoring Engine]: MediaRecorder error:', error);
      isRecordingRef.current = false;
    }
  }, [candidateId, examId]);

  // Log violation details to backend
  const logReportOnlyViolation = useCallback((violationType, customMessage) => {
    setWarning({
      isOpen: true,
      text: customMessage,
      violationType: violationType
    });

    if (typeof logViolationEvent === 'function') {
      logViolationEvent({ 
        candidateId,
        examId,
        violationType, 
        timestamp: Date.now(),
        details: customMessage
      }).catch((err) => console.warn('[Proctoring Engine]: Backend offline:', err.message));
    }
  }, [candidateId, examId]);

  // Start 2-second away grace timer for tab switch, window blur, or app switch
  const startAwayTimer = useCallback((violationType) => {
    if (isInitializingRef.current || isPromptActiveRef.current || awayTimerRef.current) return;

    awayTimerRef.current = setTimeout(() => {
      setWarning({
        isOpen: true,
        text: 'Violation event logged. Assessment security policy breach detected.',
        violationType: violationType
      });

      if (typeof logViolationEvent === 'function') {
        logViolationEvent({ 
          candidateId,
          examId,
          violationType, 
          timestamp: Date.now(),
          details: 'Candidate away from window for more than 2 seconds'
        }).catch((err) => console.warn('[Proctoring Engine]: Backend offline:', err.message));
      }

      trigger30SecRecording(violationType);
      awayTimerRef.current = null;
    }, 2000);
  }, [candidateId, examId, trigger30SecRecording]);

  // Clear timer if candidate returns within 2 seconds
  const clearAwayTimer = useCallback(() => {
    if (awayTimerRef.current) {
      clearTimeout(awayTimerRef.current);
      awayTimerRef.current = null;
    }
  }, []);

  // Request camera and screen access directly on user click
  const requestMediaStreams = useCallback(async () => {
    isInitializingRef.current = true;
    isPromptActiveRef.current = true;

    try {
      // 1. Get Webcam Stream
      let userWebcamStream = webcamStreamRef.current;
      if (!userWebcamStream || !userWebcamStream.active) {
        userWebcamStream = await navigator.mediaDevices.getUserMedia({
          video: { width: 640, height: 480 },
          audio: true
        });
      }

      setWebcamStream(userWebcamStream);
      webcamStreamRef.current = userWebcamStream;

      // 2. Request Entire Screen Share
      const displayStream = await navigator.mediaDevices.getDisplayMedia({
        video: {
          displaySurface: 'monitor',
          cursor: 'always'
        },
        audio: false
      });

      const videoTrack = displayStream.getVideoTracks()[0];
      const settings = videoTrack.getSettings ? videoTrack.getSettings() : {};

      // ENFORCE ENTIRE SCREEN ONLY: Reject tabs or windows
      if (settings.displaySurface && settings.displaySurface !== 'monitor') {
        videoTrack.stop();
        setScreenShareError('INVALID_SCREEN_SURFACE');
        
        setWarning({
          isOpen: true,
          text: 'Security Violation: You MUST select your ENTIRE SCREEN. Sharing individual windows or browser tabs is prohibited.',
          violationType: 'INVALID_SCREEN_SURFACE'
        });
        return false;
      }

      // Handle candidate manually stopping screen share
      videoTrack.onended = () => {
        logReportOnlyViolation(
          'SCREEN_SHARE_STOPPED',
          'Screen sharing was stopped! You must share your ENTIRE SCREEN continuously during the assessment.'
        );
      };

      setScreenStream(displayStream);
      screenStreamRef.current = displayStream;
      setScreenShareError(null);

      publishCandidateToAgora();
      return true;

    } catch (err) {
      console.error('[Proctoring Engine]: Stream permission access denied:', err);
      setScreenShareError('SCREEN_SHARE_DENIED');
      setWarning({
        isOpen: true,
        text: 'Screen Share Required: Please allow entire screen sharing to start the assessment.',
        violationType: 'SCREEN_SHARE_DENIED'
      });
      return false;
    } finally {
      setTimeout(() => {
        isInitializingRef.current = false;
        isPromptActiveRef.current = false;
      }, 2000);
    }
  }, [publishCandidateToAgora, logReportOnlyViolation]);

  // Clean up media streams on component unmount
  useEffect(() => {
    return () => {
      if (webcamStreamRef.current) webcamStreamRef.current.getTracks().forEach((track) => track.stop());
      if (screenStreamRef.current) screenStreamRef.current.getTracks().forEach((track) => track.stop());
      if (localAgoraVideoTrackRef.current) localAgoraVideoTrackRef.current.close();
      if (localAgoraAudioTrackRef.current) localAgoraAudioTrackRef.current.close();
      agoraClient.leave();
    };
  }, []);

  // AI face detection loop
  useEffect(() => {
    let active = true;

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
                  'No face detected in webcam view. Please stay visible in front of camera.'
                );
              }
            } else if (faceCount > 1) {
              multiFaceTimerRef.current += 1;
              noFaceTimerRef.current = 0;

              if (multiFaceTimerRef.current > 90) {
                multiFaceTimerRef.current = 0;
                logReportOnlyViolation(
                  'MULTIPLE_FACES_DETECTED',
                  'Multiple faces detected. Please make sure you are alone.'
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
        console.error('[Proctoring Engine]: AI face detection error:', err);
      }
    };

    if (webcamStream) {
      startAIVision();
    }

    return () => {
      active = false;
      if (aiAnimationFrameRef.current) cancelAnimationFrame(aiAnimationFrameRef.current);
    };
  }, [webcamStream, warning.isOpen, logReportOnlyViolation]);

  // Desktop Security Listeners
  useEffect(() => {
    const handleKeyDown = (e) => {
      if (e.key === 'Alt' || e.key === 'Control' || e.key === 'Meta') {
        isModifierPressedRef.current = true;
      }

      if ((e.altKey || e.ctrlKey) && (e.key === 'Tab' || e.key === 'Escape')) {
        e.preventDefault();
        if (!DEMO_MODE) startAwayTimer('KEYBOARD_APPLICATION_SWITCH');
      }
    };

    const handleKeyUp = (e) => {
      if (e.key === 'Alt' || e.key === 'Control' || e.key === 'Meta') {
        isModifierPressedRef.current = false;
      }
    };

    const handleWindowBlur = () => {
      if (DEMO_MODE || isPromptActiveRef.current || isInitializingRef.current) return;
      if (isModifierPressedRef.current) {
        startAwayTimer('KEYBOARD_APPLICATION_SWITCH');
        isModifierPressedRef.current = false;
      } else {
        startAwayTimer('WINDOW_BLUR_OVER_2SEC');
      }
    };

    const handleWindowFocus = () => {
      isModifierPressedRef.current = false;
      clearAwayTimer();
    };

    const handleVisibilityChange = () => {
      if (DEMO_MODE || isPromptActiveRef.current || isInitializingRef.current) return;
      if (document.hidden) {
        if (isModifierPressedRef.current) {
          startAwayTimer('KEYBOARD_APPLICATION_SWITCH');
          isModifierPressedRef.current = false;
        } else {
          startAwayTimer('TAB_SWITCH_OVER_2SEC');
        }
      } else {
        clearAwayTimer();
      }
    };

    const handleFullscreenChange = () => {
      if (DEMO_MODE || isPromptActiveRef.current || isInitializingRef.current) return;
      if (!document.fullscreenElement) {
        setWarning({
          isOpen: true,
          text: 'Fullscreen mode exited. Please re-enter fullscreen immediately.',
          violationType: 'FULLSCREEN_EXIT'
        });

        if (typeof logViolationEvent === 'function') {
          logViolationEvent({ 
            candidateId,
            examId,
            violationType: 'FULLSCREEN_EXIT', 
            timestamp: Date.now(),
            details: 'Candidate exited fullscreen mode'
          }).catch((err) => console.warn('[Proctoring Engine]: Backend log error:', err.message));
        }

        trigger30SecRecording('FULLSCREEN_EXIT');
      } else {
        clearAwayTimer();
      }
    };

    // MERGED: Monaco Code Editor Exception handling for Copy/Paste/Context Menu
    const handleCopyPaste = (e) => {
      if (DEMO_MODE) return;
      if (e.target.closest && e.target.closest('.monaco-editor')) return;

      e.preventDefault();
      logReportOnlyViolation('COPY_PASTE_ATTEMPT', 'Copy paste actions are disabled during exam.');
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
  }, [startAwayTimer, clearAwayTimer, logReportOnlyViolation, trigger30SecRecording, candidateId, examId]);

  // Close warning modal and automatically re-prompt screen share + re-enter fullscreen
  const closeWarning = async () => {
    noFaceTimerRef.current = 0;
    multiFaceTimerRef.current = 0;

    const lastViolationType = warning.violationType;
    setWarning({ isOpen: false, text: '', violationType: '' });

    if (
      lastViolationType === 'INVALID_SCREEN_SURFACE' || 
      lastViolationType === 'SCREEN_SHARE_DENIED' || 
      lastViolationType === 'SCREEN_SHARE_STOPPED'
    ) {
      const granted = await requestMediaStreams();
      if (granted) {
        await enterFullscreen();
      }
      return;
    }

    await enterFullscreen();
  };

  return { 
    warning, 
    closeWarning, 
    screenShareError, 
    requestMediaStreams, 
    enterFullscreen,
    webcamStream, 
    screenStream 
  };
};

export const handleMonacoPaste = (candidateId, examSessionId) => {
  console.log(`[Proctoring]: Paste used in editor by ${candidateId} (session: ${examSessionId})`);
};

// Export both named and default for backwards compatibility across all feature branches
export default useProctoring;