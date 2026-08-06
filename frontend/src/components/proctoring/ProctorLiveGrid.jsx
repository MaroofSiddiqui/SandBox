import { useState } from 'react';

/**
 * HR Proctoring Live Monitoring Grid
 * Configured for Maximum Safe Limit: 12 candidates per page (4x3 Grid)
 */
export const ProctorLiveGrid = ({ examSessions = [], webcamStream = null }) => {
  const [selectedExamId, setSelectedExamId] = useState(examSessions[0]?.examId || '');
  
  const [currentPage, setCurrentPage] = useState(1);
  const candidatesPerPage = 12;

  const [focusedCandidateUid, setFocusedCandidateUid] = useState(null);

  const currentExam = examSessions.find((s) => s.examId === selectedExamId) || examSessions[0];
  const candidates = currentExam?.candidates || [];

  const totalCandidates = candidates.length;
  const totalPages = Math.ceil(totalCandidates / candidatesPerPage) || 1;
  const startIndex = (currentPage - 1) * candidatesPerPage;
  const currentCandidates = candidates.slice(startIndex, startIndex + candidatesPerPage);

  const handleExamChange = (e) => {
    setSelectedExamId(e.target.value);
    setCurrentPage(1);
    setFocusedCandidateUid(null);
  };

  const handleTileClick = (uid) => {
    setFocusedCandidateUid((prevUid) => (prevUid === uid ? null : uid));
  };

  return (
    <div style={{ backgroundColor: '#0f172a', padding: '1.5rem', borderRadius: '16px', border: '1px solid #1e293b' }}>
      
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <div>
          <h3 style={{ color: '#ffffff', margin: '0 0 0.25rem 0', fontSize: '1.25rem' }}>
            Live Candidate Monitoring Grid
          </h3>
          <p style={{ color: '#64748b', margin: 0, fontSize: '0.875rem' }}>
            Select an ongoing assessment to view live candidate feeds (Max 12 per page)
          </p>
        </div>

        <div>
          <label style={{ color: '#94a3b8', fontSize: '0.875rem', marginRight: '0.5rem' }}>
            Active Exam:
          </label>
          <select
            value={selectedExamId}
            onChange={handleExamChange}
            style={{
              backgroundColor: '#1e293b',
              color: '#ffffff',
              border: '1px solid #334155',
              padding: '0.5rem 1rem',
              borderRadius: '8px',
              fontSize: '0.875rem',
              outline: 'none',
              cursor: 'pointer'
            }}
          >
            {examSessions.map((session) => (
              <option key={session.examId} value={session.examId}>
                {session.examTitle} ({session.candidates ? session.candidates.length : 0} Students)
              </option>
            ))}
          </select>
        </div>
      </div>

      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))',
        gap: '1rem',
        marginBottom: '1.5rem'
      }}>
        {currentCandidates.map((candidate) => {
          const isFocused = focusedCandidateUid === candidate.uid;
          const isLocalCandidate = candidate.uid === 101 || candidate.candidateId?.includes("You");

          return (
            <div
              key={candidate.uid || candidate.candidateId}
              onClick={() => handleTileClick(candidate.uid)}
              style={{
                position: 'relative',
                height: '180px',
                backgroundColor: '#000000',
                borderRadius: '12px',
                overflow: 'hidden',
                cursor: 'pointer',
                border: isFocused ? '3px solid #2563eb' : '1px solid #334155',
                boxShadow: isFocused ? '0 0 15px rgba(37, 99, 235, 0.4)' : 'none',
                transition: 'all 0.2s ease-in-out'
              }}
            >
              {isLocalCandidate && webcamStream ? (
                <video
                  ref={(ref) => { if (ref) ref.srcObject = webcamStream; }}
                  autoPlay
                  playsInline
                  muted={!isFocused}
                  style={{ width: '100%', height: '100%', objectFit: 'cover', transform: 'scaleX(-1)' }}
                />
              ) : (
                <div style={{
                  display: 'flex',
                  flexDirection: 'column',
                  alignItems: 'center',
                  justifyContent: 'center',
                  height: '100%',
                  color: '#64748b'
                }}>
                  <div style={{ fontSize: '1.75rem', marginBottom: '0.25rem' }}>📷</div>
                  <span style={{ fontSize: '0.8rem' }}>Connecting feed...</span>
                </div>
              )}

              <div style={{
                position: 'absolute',
                bottom: '8px',
                left: '8px',
                backgroundColor: 'rgba(15, 23, 42, 0.85)',
                padding: '4px 8px',
                borderRadius: '6px',
                color: '#ffffff',
                fontSize: '0.725rem',
                fontWeight: '600',
                display: 'flex',
                alignItems: 'center',
                gap: '6px',
                backdropFilter: 'blur(4px)'
              }}>
                <span>{candidate.candidateId}</span>
                <span style={{ color: isFocused ? '#60a5fa' : '#94a3b8' }}>
                  {isFocused ? '🔊 (Live Audio)' : '🔇 (Muted)'}
                </span>
              </div>
            </div>
          );
        })}
      </div>

      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <span style={{ color: '#64748b', fontSize: '0.875rem' }}>
          Showing {totalCandidates > 0 ? startIndex + 1 : 0} - {Math.min(startIndex + candidatesPerPage, totalCandidates)} of {totalCandidates} candidates
        </span>

        <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
          <button
            onClick={() => setCurrentPage((prev) => Math.max(prev - 1, 1))}
            disabled={currentPage === 1}
            style={{
              backgroundColor: currentPage === 1 ? '#1e293b' : '#2563eb',
              color: currentPage === 1 ? '#475569' : '#ffffff',
              border: 'none',
              padding: '0.5rem 1rem',
              borderRadius: '6px',
              fontSize: '0.85rem',
              fontWeight: '600',
              cursor: currentPage === 1 ? 'not-allowed' : 'pointer'
            }}
          >
            ← Previous
          </button>

          <span style={{ color: '#94a3b8', fontSize: '0.85rem', padding: '0 0.5rem' }}>
            Page {currentPage} of {totalPages}
          </span>

          <button
            onClick={() => setCurrentPage((prev) => Math.min(prev + 1, totalPages))}
            disabled={currentPage === totalPages}
            style={{
              backgroundColor: currentPage === totalPages ? '#1e293b' : '#2563eb',
              color: currentPage === totalPages ? '#475569' : '#ffffff',
              border: 'none',
              padding: '0.5rem 1rem',
              borderRadius: '6px',
              fontSize: '0.85rem',
              fontWeight: '600',
              cursor: currentPage === totalPages ? 'not-allowed' : 'pointer'
            }}
          >
            Next →
          </button>
        </div>
      </div>
    </div>
  );
};

export default ProctorLiveGrid;