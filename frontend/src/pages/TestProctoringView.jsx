import React, { useEffect, useRef } from 'react';
import { useProctoring } from '../hooks/useProctoring';
import { WarningModal } from '../components/proctoring/WarningModal';

/**
 * TestProctoringView Component
 *
 * Serves as the candidate-facing assessment view or sandbox page.
 * Renders the protected exam container, enforces fullscreen mode, presents a mandatory 
 * screen-share lockout overlay if compliance is lost, and displays a floating, 
 * clean candidate live webcam preview box.
 *
 * @component
 * @returns {JSX.Element} The assessment interface with proctoring guardrails.
 */
export const TestProctoringView = () => {
  const { warning, closeWarning, screenShareError, requestMediaStreams, webcamStream } = useProctoring();
  const webcamVideoRef = useRef(null);

  /**
   * Synchronizes active webcam MediaStream to the floating HTML5 video element.
   */
  useEffect(() => {
    if (webcamVideoRef.current && webcamStream) {
      webcamVideoRef.current.srcObject = webcamStream;
    }
  }, [webcamStream]);

  /**
   * Requests the browser to enter Fullscreen mode.
   */
  const handleEnableFullscreen = () => {
    if (document.documentElement.requestFullscreen) {
      document.documentElement.requestFullscreen().catch((err) => {
        console.error("[UI]: Failed to enter fullscreen mode:", err);
      });
    }
  };

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
        
        {/* MANDATORY EXAM LOCKOUT OVERLAY (Active when screen share is invalid or stopped) */}
        {screenShareError && (
          <div style={{
            position: 'fixed',
            top: 0,
            left: 0,
            width: '100vw',
            height: '100vh',
            backgroundColor: 'rgba(241, 245, 249, 0.95)',
            backdropFilter: 'blur(6px)',
            zIndex: 999999, // Renders above all exam elements to block interaction
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'center',
            alignItems: 'center',
            color: '#0f172a',
            textAlign: 'center',
            padding: '2rem'
          }}>
            <div style={{ 
              backgroundColor: '#ffffff', 
              padding: '2.5rem', 
              borderRadius: '16px', 
              maxWidth: '540px', 
              width: '90%',
              border: '1px solid #cbd5e1',
              borderTop: '6px solid #0284c7',
              boxShadow: '0 25px 50px -12px rgba(14, 165, 233, 0.18)'
            }}>
              <div style={{
                backgroundColor: '#e0f2fe',
                width: '64px',
                height: '64px',
                borderRadius: '50%',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                margin: '0 auto 1.25rem auto',
                fontSize: '2rem'
              }}>
                🔒
              </div>

              <h2 style={{ color: '#0369a1', marginBottom: '1rem', fontSize: '1.75rem', fontWeight: '700' }}>
                Assessment Paused
              </h2>
              
              <p style={{ color: '#334155', marginBottom: '1.75rem', fontSize: '1.05rem', lineHeight: '1.6' }}>
                {screenShareError === 'ENTIRE_SCREEN_REQUIRED' && 'You selected a single tab or window. To proceed with the assessment, you MUST select "Entire Screen".'}
                {screenShareError === 'SCREEN_SHARE_STOPPED' && 'Screen sharing was interrupted. Your assessment is locked until screen sharing is restored.'}
                {screenShareError === 'SCREEN_SHARE_DENIED' && 'Screen sharing permission was denied. Sharing your entire screen is mandatory.'}
              </p>

              <button
                onClick={requestMediaStreams}
                style={{
                  backgroundColor: '#0284c7',
                  color: '#ffffff',
                  padding: '0.875rem 1.75rem',
                  border: 'none',
                  borderRadius: '8px',
                  fontWeight: '600',
                  fontSize: '1.05rem',
                  cursor: 'pointer',
                  width: '100%',
                  boxShadow: '0 4px 12px rgba(2, 132, 199, 0.3)',
                  transition: 'background-color 0.2s ease'
                }}
              >
                Share Entire Screen to Resume
              </button>
            </div>
          </div>
        )}

        {/* Header Header Container */}
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
            Module 6: Security Verification & Client Guardrails
          </p>
        </header>

        {/* Actions Controls */}
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

        {/* Main Exam Card Content Placeholder */}
        <section style={{ 
          padding: '2rem', 
          backgroundColor: '#ffffff', 
          borderRadius: '12px', 
          border: '1px solid #e2e8f0',
          boxShadow: '0 1px 3px rgba(0, 0, 0, 0.05)'
        }}>
          <h3 style={{ marginTop: 0, color: '#1e293b', fontSize: '1.25rem' }}>Protected Assessment Area</h3>
          <p style={{ color: '#475569', lineHeight: '1.6' }}>
            All screen activity and webcam metrics are actively monitored. Look at the bottom-right corner to see your live camera feed!
          </p>
        </section>

        {/* FLOATING WEBCAM SELF-VIEW WINDOW */}
        {webcamStream && (
          <div style={{
            position: 'fixed',
            bottom: '24px',
            right: '24px',
            width: '180px',
            height: '135px',
            borderRadius: '12px',
            overflow: 'hidden',
            boxShadow: '0 10px 25px -5px rgba(0, 0, 0, 0.15), 0 8px 10px -6px rgba(0, 0, 0, 0.05)',
            border: '2px solid #2563eb',
            backgroundColor: '#000000',
            zIndex: 9000,
            pointerEvents: 'none' // Prevents browser hover popups, translation, and PiP icons
          }}>
            <video
              ref={webcamVideoRef}
              autoPlay
              playsInline
              muted
              disablePictureInPicture // Disables Chromium native Picture-in-Picture widget
              controlsList="nodownload noplaybackrate pictureinpicture" // Suppresses native media controls
              style={{
                width: '100%',
                height: '100%',
                objectFit: 'cover',
                transform: 'scaleX(-1)', // Horizontal mirror effect for natural feedback
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

        {/* Generic Violation Alert Modal */}
        <WarningModal
          isOpen={warning.isOpen}
          warningText={warning.text}
          onClose={closeWarning}
        />
      </div>
    </div>
  );
};