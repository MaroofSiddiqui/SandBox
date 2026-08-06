import React, { useState, useEffect, createContext, useContext } from 'react';
import useProctoring from '../../hooks/useProctoring'; 
import { WarningModal } from './WarningModal'; 
import { isMobileDevice } from '../../utils/deviceCheck'; // Adjust relative path as needed

// Create Context to make proctoring data accessible across child components
const ProctoringContext = createContext(null);

export const useProctoringContext = () => useContext(ProctoringContext);

export const ProctoringWrapper = ({ children, candidateId, examSessionId }) => {
  const proctoringData = useProctoring(candidateId, examSessionId);
  const { warning, closeWarning, enterFullscreen, requestMediaStreams } = proctoringData;
  
  const [isMobile, setIsMobile] = useState(false);
  const [isStarted, setIsStarted] = useState(false);

  // Check if candidate is using a mobile phone, tablet, or iPad on mount and window resize
  useEffect(() => {
    const checkDevice = () => {
      setIsMobile(isMobileDevice());
    };

    checkDevice();
    window.addEventListener('resize', checkDevice);
    return () => window.removeEventListener('resize', checkDevice);
  }, []);

  const handleStartExam = async () => {
    // 1. Trigger screen share prompt directly on user click
    const streamsGranted = await requestMediaStreams();
    
    // 2. Enter fullscreen mode after permissions are resolved
    if (streamsGranted) {
      await enterFullscreen();
      setIsStarted(true);
    }
  };

  // BLOCK ASSESSMENT IF ACCESSED FROM PHONE / TABLET / IPAD
  if (isMobile) {
    return (
      <div style={{
        position: 'fixed',
        top: 0,
        left: 0,
        width: '100vw',
        height: '100vh',
        backgroundColor: '#0f172a',
        zIndex: 999999,
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
        color: '#ffffff',
        fontFamily: 'Arial, sans-serif',
        padding: '20px',
        boxSizing: 'border-box'
      }}>
        <div style={{
          backgroundColor: '#1e293b',
          padding: '40px 30px',
          borderRadius: '12px',
          textAlign: 'center',
          maxWidth: '440px',
          border: '2px solid #ef4444',
          boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.5)'
        }}>
          <div style={{ fontSize: '48px', marginBottom: '15px' }}>🚫💻</div>
          <h2 style={{ margin: '0 0 15px 0', color: '#f87171' }}>Mobile Device Detected</h2>
          <p style={{ color: '#cbd5e1', lineHeight: '1.6', marginBottom: '20px', fontSize: '14px' }}>
            This assessment is strict and proctored. It <strong>cannot</strong> be attempted on mobile phones, tablets, or iPads.
          </p>
          <div style={{
            backgroundColor: '#0f172a',
            padding: '12px 16px',
            borderRadius: '8px',
            color: '#94a3b8',
            fontSize: '13px',
            border: '1px solid #334155'
          }}>
            Please open this link on a <strong>Desktop or Laptop computer</strong> using Google Chrome, Microsoft Edge, or Brave browser.
          </div>
        </div>
      </div>
    );
  }

  // DESKTOP / LAPTOP ASSESSMENT FLOW
  return (
    <ProctoringContext.Provider value={proctoringData}>
      <div style={{ position: 'relative', width: '100vw', height: '100vh', overflow: 'hidden' }}>
        
        {/* Splash Start Modal to trigger browser permission gesture safely */}
        {!isStarted && (
          <div style={{
            position: 'fixed',
            top: 0,
            left: 0,
            width: '100vw',
            height: '100vh',
            backgroundColor: '#0f172a',
            zIndex: 99999,
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'center',
            alignItems: 'center',
            color: '#ffffff',
            fontFamily: 'Arial, sans-serif'
          }}>
            <div style={{
              backgroundColor: '#1e293b',
              padding: '40px',
              borderRadius: '12px',
              textAlign: 'center',
              maxWidth: '480px',
              border: '1px solid #334155',
              boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.5)'
            }}>
              <h2 style={{ margin: '0 0 15px 0', color: '#60a5fa' }}>Proctored Assessment</h2>
              <p style={{ color: '#cbd5e1', lineHeight: '1.6', marginBottom: '25px', fontSize: '14px' }}>
                This test is proctored in real-time. Click below to share your entire screen and start the exam in fullscreen mode.
              </p>
              <button
                onClick={handleStartExam}
                style={{
                  backgroundColor: '#2563eb',
                  color: '#ffffff',
                  border: 'none',
                  padding: '14px 28px',
                  fontSize: '16px',
                  fontWeight: 'bold',
                  borderRadius: '8px',
                  cursor: 'pointer',
                  width: '100%',
                  transition: 'background-color 0.2s'
                }}
              >
                Start Assessment & Share Screen
              </button>
            </div>
          </div>
        )}

        {/* Security Warning Popup */}
        <WarningModal 
          isOpen={warning.isOpen} 
          warningText={warning.text} 
          onClose={closeWarning} 
        />
        
        {/* Children components (CodeEditor UI, Dashboards, etc.) */}
        {children}
      </div>
    </ProctoringContext.Provider>
  );
};

export default ProctoringWrapper;