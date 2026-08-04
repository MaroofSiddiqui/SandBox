import { useEffect, useRef, useState, useCallback } from 'react';
import AgoraRTC from 'agora-rtc-sdk-ng';
import { uploadViolationEvidence, logViolationEvent } from '../api/proctoringService';
import { initializeFaceDetector, detectFacesInFrame } from '../utils/faceDetectionService';

// turn this to false in production so guardrails stay active
const DEMO_MODE = false;

// agora client instance for streaming webcam
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
  const awayTimerRef = useRef(null);
  const isModifierPressedRef = useRef(false);

  const noFaceTimerRef = useRef(0);
  const multiFaceTimerRef = useRef(0);
  const aiAnimationFrameRef = useRef(null);

  const webcamStreamRef = useRef(null);
  const screenStreamRef = useRef(null);
  const localAgoraVideoTrackRef = useRef(null);
  const localAgoraAudioTrackRef = useRef(null);

  // publish candidate stream to agora
  const publishCandidateToAgora = useCallback(async () => {
    if (!agoraCredentials || !agoraCredentials.appId || !agoraCredentials.channelName) {
      console.log('agora details not provided so skipping live stream publish');
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
      console.log('candidate live stream published to agora');
    } catch (err) {
      console.warn('agora publish failed', err.message);
    }
  }, [agoraCredentials]);

  // records 30 second video snippet when violation occurs
  const trigger30SecRecording = useCallback((eventType) => {
    if (isRecordingRef.current) return;

    const currentWebcam = webcamStreamRef.current;
    const currentScreen = screenStreamRef.current;

    if (!currentWebcam) {
      console.error('webcam stream not ready for recording');
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
          .catch((err) => console.warn('evidence upload error', err.message))
          .finally(() => { isRecordingRef.current = false; });
      };

      mediaRecorder.start();

      setTimeout(() => {
        if (mediaRecorder.state !== 'inactive') mediaRecorder.stop();
      }, 30000);

    } catch (error) {
      console.error('error starting media recorder', error);
      isRecordingRef.current = false;
    }
  }, [candidateId, examId]);

  // logs violation text message to backend database
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
      }).catch((err) => console.warn('backend logging failed', err.message));
    }
  }, [candidateId, examId]);

  // starts away timer for tab switch or window blur
  const startAwayTimer = useCallback((violationType) => {
    if (awayTimerRef.current) return;

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
          details: 'Candidate away from exam window for more than 5 seconds'
        }).catch((err) => console.warn('backend logging failed', err.message));
      }

      trigger30SecRecording(violationType);
      awayTimerRef.current = null;
    }, 5000);
  }, [candidateId, examId, trigger30SecRecording]);

  // clears away timer if student returns quickly
  const clearAwayTimer = useCallback(() => {
    if (awayTimerRef.current) {
      clearTimeout(awayTimerRef.current);
      awayTimerRef.current = null;
    }
  }, []);

  // requests camera and forces entire screen selection
  const requestMediaStreams = useCallback(async () => {
    if (isInitializingRef.current) return;
    isInitializingRef.current = true;

    try {
      // ask for webcam and mic
      let userWebcamStream = webcamStreamRef.current;
      if (!userWebcamStream || !userWebcamStream.active) {
        userWebcamStream = await navigator.mediaDevices.getUserMedia({
          video: { width: 640, height: 480 },
          audio: true
        });
      }

      setWebcamStream(userWebcamStream);
      webcamStreamRef.current = userWebcamStream;

      // ask for entire screen share
      const displayStream = await navigator.mediaDevices.getDisplayMedia({
        video: {
          displaySurface: 'monitor',
          cursor: 'always'
        },
        audio: false
      });

      // verify if user selected entire monitor or just a single tab
      const videoTrack = displayStream.getVideoTracks()[0];
      const settings = videoTrack.getSettings ? videoTrack.getSettings() : {};

      if (settings.displaySurface && settings.displaySurface !== 'monitor') {
        videoTrack.stop();
        setScreenShareError('INVALID_SCREEN_SURFACE');
        
        setWarning({
          isOpen: true,
          text: 'Security Warning: You must select your ENTIRE SCREEN. Sharing individual app windows or tabs is not allowed.',
          violationType: 'INVALID_SCREEN_SURFACE'
        });
        return;
      }

      // detect if candidate stops screen sharing manually
      videoTrack.onended = () => {
        logReportOnlyViolation(
          'SCREEN_SHARE_STOPPED',
          'Screen share stopped! You must share your entire screen continuously during the exam.'
        );
      };

      setScreenStream(displayStream);
      screenStreamRef.current = displayStream;
      setScreenShareError(null);

      publishCandidateToAgora();

    } catch (err) {
      console.error('stream access denied', err);
      setScreenShareError('SCREEN_SHARE_DENIED');
      setWarning({
        isOpen: true,
        text: 'Screen Share Required: Please allow entire screen sharing to start the assessment.',
        violationType: 'SCREEN_SHARE_DENIED'
      });
    } finally {
      isInitializingRef.current = false;
    }
  }, [publishCandidateToAgora, logReportOnlyViolation]);

  // initialize camera and screen on mount
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
      if (localAgoraVideoTrackRef.current) localAgoraVideoTrackRef.current.close();
      if (localAgoraAudioTrackRef.current) localAgoraAudioTrackRef.current.close();
      agoraClient.leave();
    };
  }, [requestMediaStreams]);

  // ai face detector frame loop
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
        console.error('ai detector failed', err);
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

  // security event listeners for keyboard shortcuts and focus changes
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
      if (DEMO_MODE) return;
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
      if (DEMO_MODE) return;
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
      if (DEMO_MODE) return;
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
          }).catch((err) => console.warn('backend log error', err.message));
        }

        trigger30SecRecording('FULLSCREEN_EXIT');
      } else {
        clearAwayTimer();
      }
    };

    const handleCopyPaste = (e) => {
      if (DEMO_MODE) return;
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

  // close warning popup and request screen share or fullscreen again
  const closeWarning = () => {
    noFaceTimerRef.current = 0;
    multiFaceTimerRef.current = 0;

    const lastViolationType = warning.violationType;
    setWarning({ isOpen: false, text: '', violationType: '' });

    if (lastViolationType === 'INVALID_SCREEN_SURFACE' || lastViolationType === 'SCREEN_SHARE_DENIED') {
      requestMediaStreams();
      return;
    }

    if (!document.fullscreenElement && document.documentElement.requestFullscreen) {
      document.documentElement.requestFullscreen().catch((err) => {
        console.error('fullscreen error', err);
      });
    }
  };

  return { warning, closeWarning, screenShareError, requestMediaStreams, webcamStream, screenStream };
};

export default useProctoring;