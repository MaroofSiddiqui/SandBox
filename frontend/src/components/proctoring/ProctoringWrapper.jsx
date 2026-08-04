import React, { useEffect } from 'react';
// Exact path as per your screenshot
import useProctoring from '../../hooks/useProctoring'; 
import { WarningModal } from './WarningModal'; 

export const ProctoringWrapper = ({ children, candidateId, examSessionId }) => {
  // Hook se functions aur state nikal rahe hain
  const { warning, closeWarning, requestMediaStreams } = useProctoring();

  // Jaise hi component load ho, camera aur screen recording permission maango
  useEffect(() => {
    requestMediaStreams();
  }, [requestMediaStreams]);

  return (
    <div style={{ position: 'relative', width: '100vw', height: '100vh', overflow: 'hidden' }}>
      {/* Agar koi violation hogi, toh yeh Modal apne aap dikh jayega */}
      <WarningModal 
        isOpen={warning.isOpen} 
        warningText={warning.text} 
        onClose={closeWarning} 
      />
      
      {/* Yeh children aapka CodeEvaluation (Editor) UI hai */}
      {children}
    </div>
  );
};

// Monaco editor me paste hone par sirf silently track karo (audit ke liye) — 
// candidate ko koi alert nahi, kyunki editor me paste allowed hai
export const handleMonacoPaste = (candidateId, examSessionId) => {
  console.log(`[Proctoring]: Paste used in editor by ${candidateId} (session: ${examSessionId})`);
};