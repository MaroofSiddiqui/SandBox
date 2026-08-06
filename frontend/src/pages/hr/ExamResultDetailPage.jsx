import React from "react";
import { useNavigate, useParams } from "react-router-dom";
import Sidebar from "../../components/common/Sidebar";
import Topbar from "../../components/common/Topbar";

const MOCK_LEADERBOARD = [
  {
    rank: "#1",
    candidateId: "CAND-101",
    name: "Aarav Sharma",
    codingScore: 90,
    mcqScore: 45,
    totalScore: 135,
    violations: 0
  },
  {
    rank: "#2",
    candidateId: "CAND-102",
    name: "Sanya Gupta",
    codingScore: 85,
    mcqScore: 40,
    totalScore: 125,
    violations: 2
  },
  {
    rank: "#3",
    candidateId: "CAND-103",
    name: "Vikram Malhotra",
    codingScore: 70,
    mcqScore: 35,
    totalScore: 105,
    violations: 7
  },
  {
    rank: "#4",
    candidateId: "CAND-104",
    name: "Rohan Verma",
    codingScore: 60,
    mcqScore: 30,
    totalScore: 90,
    violations: 1
  }
];

export const ExamResultDetailPage = () => {
  const navigate = useNavigate();
  const { examId } = useParams();

  return (
    <div style={{ display: "flex", minHeight: "100vh", backgroundColor: "#ffffff" }}>
      <Sidebar />

      <div style={{ flexGrow: 1, display: "flex", flexDirection: "column" }}>
        <Topbar />

        <main style={{ padding: "32px", flexGrow: 1 }}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "20px" }}>
            <h2 style={{ fontSize: "20px", fontWeight: "700", color: "#000000", margin: 0 }}>
              Assessment Results {examId ? `- ${examId}` : ""}
            </h2>

            <button
              onClick={() => navigate("/hr/reports")}
              style={{
                backgroundColor: "#f1f5f9",
                color: "#0f172a",
                border: "1px solid #cbd5e1",
                padding: "6px 12px",
                borderRadius: "4px",
                fontSize: "13px",
                fontWeight: "600",
                cursor: "pointer"
              }}
            >
              ← Back to Reports
            </button>
          </div>

          <div style={{ border: "1px solid #e5e7eb", borderRadius: "4px" }}>
            <table style={{ width: "100%", borderCollapse: "collapse", textAlign: "left", fontSize: "14px" }}>
              <thead>
                <tr style={{ borderBottom: "1px solid #e5e7eb", backgroundColor: "#fafafa", color: "#000000" }}>
                  <th style={{ padding: "12px 16px" }}>Rank</th>
                  <th style={{ padding: "12px 16px" }}>Candidate ID</th>
                  <th style={{ padding: "12px 16px" }}>Name</th>
                  <th style={{ padding: "12px 16px" }}>Coding Score</th>
                  <th style={{ padding: "12px 16px" }}>MCQ Score</th>
                  <th style={{ padding: "12px 16px" }}>Total Score</th>
                  <th style={{ padding: "12px 16px" }}>Violations</th>
                </tr>
              </thead>
              <tbody>
                {MOCK_LEADERBOARD.map((cand) => (
                  <tr key={cand.candidateId} style={{ borderBottom: "1px solid #e5e7eb", color: "#000000" }}>
                    <td style={{ padding: "12px 16px", fontWeight: "600" }}>{cand.rank}</td>
                    <td style={{ padding: "12px 16px" }}>{cand.candidateId}</td>
                    <td style={{ padding: "12px 16px" }}>{cand.name}</td>
                    <td style={{ padding: "12px 16px" }}>{cand.codingScore}</td>
                    <td style={{ padding: "12px 16px" }}>{cand.mcqScore}</td>
                    <td style={{ padding: "12px 16px", fontWeight: "600" }}>{cand.totalScore}</td>
                    <td style={{ padding: "12px 16px" }}>{cand.violations}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </main>
      </div>
    </div>
  );
};

export default ExamResultDetailPage;