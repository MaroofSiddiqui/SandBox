import React, { useState, createContext, useContext } from 'react';
import useProctoring from '../../hooks/useProctoring'; 
import { WarningModal } from './WarningModal'; 

const ProctoringContext = createContext(null);

export const useProctoringContext = () => useContext(ProctoringContext);

export const ProctoringWrapper = ({ children, candidateId, examSessionId }) => {
  const proctoringData = useProctoring(candidateId, examSessionId);
  const { warning, closeWarning, enterFullscreen, requestMediaStreams } = proctoringData;
  const [isStarted, setIsStarted] = useState(false);

  const handleStartExam = async () => {
    const streamsGranted = await requestMediaStreams();
    if (streamsGranted) {
      await enterFullscreen();
      setIsStarted(true);
    }
  };

  return (
    <ProctoringContext.Provider value={proctoringData}>
      <div style={{ position: 'relative', width: '100vw', height: '100vh', overflow: 'hidden' }}>
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

        <WarningModal 
          isOpen={warning.isOpen} 
          warningText={warning.text} 
          onClose={closeWarning} 
        />
        
        {children}
      </div>
    </ProctoringContext.Provider>
  );
};

export default ProctoringWrapper;