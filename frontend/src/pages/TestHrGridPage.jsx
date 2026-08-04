import { useEffect, useState } from 'react';
import { ProctorLiveGrid } from '../components/proctoring/ProctorLiveGrid';
import { useProctoring } from '../hooks/useProctoring';

export const TestHrGridPage = () => {
  const { webcamStream } = useProctoring();
  const [examSessions, setExamSessions] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Example: Fetch active exams & candidates from real backend endpoint
    fetch('/api/proctoring/active-sessions')
      .then((res) => res.json())
      .then((data) => {
        if (data && data.length > 0) {
          setExamSessions(data);
        } else {
          // Fallback structure if backend returns empty session list
          setExamSessions(fallbackSessions);
        }
      })
      .catch(() => setExamSessions(fallbackSessions))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div style={{ padding: '2rem', backgroundColor: '#020617', minHeight: '100vh' }}>
      <h2 style={{ color: '#ffffff', marginBottom: '1rem' }}>
        HR Proctoring Monitoring Dashboard
      </h2>
      {loading ? (
        <p style={{ color: '#94a3b8' }}>Loading active sessions...</p>
      ) : (
        <ProctorLiveGrid examSessions={examSessions} webcamStream={webcamStream} />
      )}
    </div>
  );
};

const fallbackSessions = [
  {
    examId: "EXAM_TEST_01",
    examTitle: "Active Assessment Session 1",
    candidates: [
      { candidateId: "CANDIDATE_101 (You)", channelName: "EXAM_TEST_01", uid: 101 },
      { candidateId: "CANDIDATE_102", channelName: "EXAM_TEST_01", uid: 102 }
    ]
  }
];

export default TestHrGridPage;