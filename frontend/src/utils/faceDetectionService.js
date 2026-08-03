import { FaceDetector, FilesetResolver } from '@mediapipe/tasks-vision';

// Holds our AI face detector once it's loaded up
let faceDetector = null;

/**
 * Loads Google's MediaPipe face detection model into the browser.
 * This runs locally on the student's computer GPU so it's super fast and free.
 */
export const initializeFaceDetector = async () => {
  // If we already loaded the detector earlier, no need to load it again
  if (faceDetector) return faceDetector;

  try {
    // Grab the web assembly helper files from the web
    const vision = await FilesetResolver.forVisionTasks(
      'https://cdn.jsdelivr.net/npm/@mediapipe/tasks-vision@latest/wasm'
    );

    // Turn on the face detector with light settings so it doesn't slow down the PC
    faceDetector = await FaceDetector.createFromOptions(vision, {
      baseOptions: {
        modelAssetPath: `https://storage.googleapis.com/mediapipe-models/face_detector/blaze_face_short_range/float16/1/blaze_face_short_range.tflite`,
        delegate: 'GPU', // Uses graphics card for fast performance
      },
      runningMode: 'VIDEO', // Set to scan a live camera stream
      minDetectionConfidence: 0.5, // Must be at least 50% sure it's a real face
    });

    console.log('AI Face Detector loaded and ready!');
    return faceDetector;
  } catch (error) {
    console.error('Failed to load the AI face detector:', error);
    throw error;
  }
};

/**
 * Checks a single frame from the video stream and counts how many faces are in it.
 */
export const detectFacesInFrame = (videoElement, timestamp) => {
  // Make sure the video is actually playing before scanning
  if (!faceDetector || !videoElement || videoElement.readyState < 2) {
    return -1; // Video not ready yet
  }

  // Ask the AI to count faces in this frame
  const result = faceDetector.detectForVideo(videoElement, timestamp);
  
  // Return the total number of faces found
  return result.detections ? result.detections.length : 0;
};