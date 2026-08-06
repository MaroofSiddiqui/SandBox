import React from "react";
import { useNavigate } from "react-router-dom";
import Sidebar from "../../components/common/Sidebar";
import Topbar from "../../components/common/Topbar";

const MOCK_REPORTS = [
  {
    examId: "EXAM-101",
    title: "Java Full Stack Developer Assessment",
    date: "05 Aug 2026",
    candidates: 12,
    status: "ENDED"
  },
  {
    examId: "EXAM-102",
    title: "Python Data Structures & Algorithms",
    date: "02 Aug 2026",
    candidates: 18,
    status: "ENDED"
  },
  {
    examId: "EXAM-103",
    title: "Frontend Engineering (React + JS)",
    date: "28 Jul 2026",
    candidates: 25,
    status: "ENDED"
  }
];

export const ReportsPage = () => {
  const navigate = useNavigate();

  return (
    <div style={{ display: "flex", minHeight: "100vh", backgroundColor: "#f8fafc" }}>
      <Sidebar />

      <div style={{ flexGrow: 1, display: "flex", flexDirection: "column" }}>
        <Topbar />

        <main style={{ padding: "32px", flexGrow: 1 }}>
          <div style={{ marginBottom: "20px" }}>
            <h2 style={{ fontSize: "20px", fontWeight: "700", color: "#0f172a", margin: 0 }}>
              Assessment Reports
            </h2>
            <p style={{ color: "#64748b", fontSize: "14px", margin: "4px 0 0 0" }}>
              View evaluation reports for completed exams
            </p>
          </div>

          {/* Table Container */}
          <div style={{
            backgroundColor: "#ffffff",
            borderRadius: "8px",
            border: "1px solid #e2e8f0",
            overflow: "hidden"
          }}>
            <table style={{ width: "100%", borderCollapse: "collapse", textAlign: "left", fontSize: "14px" }}>
              <thead>
                <tr style={{ borderBottom: "1px solid #e2e8f0", backgroundColor: "#f8fafc", color: "#0f172a", fontWeight: "600" }}>
                  <th style={{ padding: "14px 16px" }}>Exam ID</th>
                  <th style={{ padding: "14px 16px" }}>Assessment Title</th>
                  <th style={{ padding: "14px 16px" }}>Date Ended</th>
                  <th style={{ padding: "14px 16px" }}>Candidates</th>
                  <th style={{ padding: "14px 16px" }}>Status</th>
                  <th style={{ padding: "14px 16px" }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {MOCK_REPORTS.map((report) => (
                  <tr key={report.examId} style={{ borderBottom: "1px solid #f1f5f9", color: "#0f172a" }}>
                    <td style={{ padding: "14px 16px", fontWeight: "600" }}>{report.examId}</td>
                    <td style={{ padding: "14px 16px" }}>{report.title}</td>
                    <td style={{ padding: "14px 16px", color: "#64748b" }}>{report.date}</td>
                    <td style={{ padding: "14px 16px" }}>{report.candidates} Submitted</td>
                    <td style={{ padding: "14px 16px", fontWeight: "600", fontSize: "12px", color: "#16a34a" }}>
                      {report.status}
                    </td>
                    <td style={{ padding: "14px 16px" }}>
                      <button
                        onClick={() => navigate(`/hr/results/${report.examId}`)}
                        style={{
                          backgroundColor: "#2563eb",
                          color: "#ffffff",
                          border: "none",
                          padding: "6px 14px",
                          borderRadius: "4px",
                          fontSize: "13px",
                          fontWeight: "600",
                          cursor: "pointer"
                        }}
                      >
                        View Results
                      </button>
                    </td>
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

export default ReportsPage;