import { useEffect, useRef, useState } from 'react';
import { useProctoring } from '../hooks/useProctoring';
import { WarningModal } from '../components/proctoring/WarningModal';
import { isMobileDevice } from '../utils/deviceCheck';

/**
 * Candidate Proctoring View Component
 * Pass real candidateId and examId props from your router/exam context!
 */
export const TestProctoringView = ({ 
  candidateId = "CANDIDATE_101", 
  examId = "EXAM_TEST_01",
  agoraCredentials = null 
}) => {
  const { warning, closeWarning, webcamStream } = useProctoring(candidateId, examId, agoraCredentials);
  const webcamVideoRef = useRef(null);
  
  const [isMobile, setIsMobile] = useState(() => isMobileDevice());

  useEffect(() => {
    const handleResize = () => {
      setIsMobile(isMobileDevice());
    };

    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  useEffect(() => {
    if (webcamVideoRef.current && webcamStream) {
      webcamVideoRef.current.srcObject = webcamStream;
    }
  }, [webcamStream]);

  const handleEnableFullscreen = () => {
    if (document.documentElement.requestFullscreen) {
      document.documentElement.requestFullscreen().catch((err) => {
        console.error("Failed to enter fullscreen mode", err);
      });
    }
  };

  if (isMobile) {
    return (
      <div style={{
        height: '100vh',
        width: '100vw',
        backgroundColor: '#0f172a',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        color: '#ffffff',
        fontFamily: 'system-ui, sans-serif',
        padding: '1.5rem',
        textAlign: 'center',
        boxSizing: 'border-box'
      }}>
        <div style={{
          backgroundColor: '#1e293b',
          padding: '2.5rem',
          borderRadius: '16px',
          maxWidth: '480px',
          width: '100%',
          boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.5)',
          border: '1px solid #334155'
        }}>
          <div style={{ fontSize: '3.5rem', marginBottom: '1rem' }}>💻</div>
          <h2 style={{ color: '#f8fafc', marginBottom: '0.75rem', fontSize: '1.5rem' }}>
            Desktop Required
          </h2>
          <p style={{ color: '#94a3b8', lineHeight: '1.6', fontSize: '1rem', margin: 0 }}>
            Online assessments cannot be taken on mobile phones or tablets. Please open this link on a desktop computer using Chrome or Edge.
          </p>
        </div>
      </div>
    );
  }

  return (
    <div style={{ 
      minHeight: '100vh', 
      backgroundColor: '#f8fafc', 
      padding: '2rem', 
      fontFamily: 'system-ui, -apple-system, sans-serif',
      color: '#0f172a',
      position: 'relative'
    }}>
      <div style={{ maxWidth: '850px', margin: '0 auto' }}>
        
        <header style={{ 
          backgroundColor: '#ffffff', 
          padding: '1.5rem 2rem', 
          borderRadius: '12px', 
          border: '1px solid #e2e8f0',
          boxShadow: '0 1px 3px rgba(0, 0, 0, 0.05)',
          marginBottom: '1.5rem' 
        }}>
          <h1 style={{ color: '#1e3a8a', fontSize: '1.75rem', margin: '0 0 0.5rem 0' }}>Proctoring Engine Test Bed</h1>
          <p style={{ color: '#64748b', fontSize: '0.975rem', margin: 0 }}>
            Active Student ID: <strong>{candidateId}</strong> | Exam Session: <strong>{examId}</strong>
          </p>
        </header>

        <section style={{ marginBottom: '1.5rem' }}>
          <button
            onClick={handleEnableFullscreen}
            style={{
              backgroundColor: '#2563eb',
              color: '#ffffff',
              padding: '0.75rem 1.5rem',
              borderRadius: '8px',
              border: 'none',
              fontWeight: '600',
              fontSize: '0.95rem',
              cursor: 'pointer',
              boxShadow: '0 2px 4px rgba(37, 99, 235, 0.2)'
            }}
          >
            Enter Fullscreen Mode
          </button>
        </section>

        <section style={{ 
          padding: '2rem', 
          backgroundColor: '#ffffff', 
          borderRadius: '12px', 
          border: '1px solid #e2e8f0',
          boxShadow: '0 1px 3px rgba(0, 0, 0, 0.05)'
        }}>
          <h3 style={{ marginTop: 0, color: '#1e293b', fontSize: '1.25rem' }}>Protected Assessment Area</h3>
          <p style={{ color: '#475569', lineHeight: '1.6' }}>
            Webcam and browser focus are actively monitored. Exiting fullscreen or switching tabs will be logged as policy breaches.
          </p>
        </section>

        {webcamStream && (
          <div style={{
            position: 'fixed',
            bottom: '24px',
            right: '24px',
            width: '180px',
            height: '135px',
            borderRadius: '12px',
            overflow: 'hidden',
            boxShadow: '0 10px 25px -5px rgba(0, 0, 0, 0.15)',
            border: '2px solid #2563eb',
            backgroundColor: '#000000',
            zIndex: 9000,
            pointerEvents: 'none'
          }}>
            <video
              ref={webcamVideoRef}
              autoPlay
              playsInline
              muted
              disablePictureInPicture
              controlsList="nodownload noplaybackrate pictureinpicture"
              style={{
                width: '100%',
                height: '100%',
                objectFit: 'cover',
                transform: 'scaleX(-1)',
                pointerEvents: 'none'
              }}
            />
            <div style={{
              position: 'absolute',
              top: '6px',
              left: '8px',
              backgroundColor: 'rgba(37, 99, 235, 0.85)',
              color: '#ffffff',
              fontSize: '0.65rem',
              fontWeight: '700',
              padding: '2px 6px',
              borderRadius: '4px',
              textTransform: 'uppercase',
              letterSpacing: '0.5px'
            }}>
              LIVE CAM
            </div>
          </div>
        )}

        <WarningModal
          isOpen={warning.isOpen}
          warningText={warning.text}
          onClose={closeWarning}
        />
      </div>
    </div>
  );
};

export default TestProctoringView;