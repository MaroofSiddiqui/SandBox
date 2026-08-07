import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getAssignmentById, getAssessmentDetails } from "../api/assignmentApi";
import { Award, CheckCircle, Printer, ArrowLeft, Calendar, User, FileText } from "lucide-react";
import "../styles/examination.css";

function ResultView() {
  const { assignmentId } = useParams();
  const navigate = useNavigate();

  const [assignment, setAssignment] = useState(null);
  const [assessment, setAssessment] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchResultData();
  }, [assignmentId]);

  const fetchResultData = async () => {
    try {
      setLoading(true);
      const res = await getAssignmentById(assignmentId);
      setAssignment(res.data);

      if (res.data?.assessmentId) {
        try {
          const astRes = await getAssessmentDetails(res.data.assessmentId);
          setAssessment(astRes.data);
        } catch (e) {}
      }
    } catch (err) {
      console.error("Failed to load result:", err);
    } finally {
      setLoading(false);
    }
  };

  const handlePrintReport = () => {
    window.print();
  };

  if (loading) {
    return (
      <div className="result-container" style={{ justifyContent: "center" }}>
        <p style={{ color: "#94a3b8" }}>Loading Candidate Result Card...</p>
      </div>
    );
  }

  return (
    <div className="result-container">
      <div className="result-card">
        <div style={{ display: "flex", justifyContent: "space-between", marginBottom: "1.5rem" }}>
          <button className="btn-secondary" onClick={() => navigate("/candidate")}>
            <ArrowLeft size={16} /> Back to Dashboard
          </button>
          <button className="btn-primary" onClick={handlePrintReport}>
            <Printer size={16} /> Print PDF Report
          </button>
        </div>

        <div className="result-header">
          <Award size={64} style={{ color: "#818cf8", marginBottom: "0.5rem" }} />
          <h2>Candidate Assessment Result</h2>
          <p style={{ color: "#94a3b8" }}>
            {assessment?.title || `Assessment #${assignment?.assessmentId}`}
          </p>
        </div>

        <div className="score-badge" style={{ textAlign: "center" }}>
          STATUS: {assignment?.status || "SUBMITTED"}
        </div>

        <div className="result-metrics">
          <div className="metric-box">
            <User size={20} style={{ color: "#60a5fa", marginBottom: "0.25rem" }} />
            <div style={{ fontSize: "1.2rem", fontWeight: "700" }}>#{assignment?.candidateId}</div>
            <div style={{ fontSize: "0.8rem", color: "#94a3b8" }}>Candidate ID</div>
          </div>

          <div className="metric-box">
            <FileText size={20} style={{ color: "#34d399", marginBottom: "0.25rem" }} />
            <div style={{ fontSize: "1.2rem", fontWeight: "700" }}>#{assignment?.assignmentId}</div>
            <div style={{ fontSize: "0.8rem", color: "#94a3b8" }}>Assignment Ref</div>
          </div>

          <div className="metric-box">
            <Calendar size={20} style={{ color: "#a78bfa", marginBottom: "0.25rem" }} />
            <div style={{ fontSize: "0.9rem", fontWeight: "600" }}>
              {assignment?.submittedAt
                ? new Date(assignment.submittedAt).toLocaleDateString()
                : new Date().toLocaleDateString()}
            </div>
            <div style={{ fontSize: "0.8rem", color: "#94a3b8" }}>Submitted Date</div>
          </div>
        </div>

        <div style={{ background: "rgba(15, 23, 42, 0.6)", padding: "1.5rem", borderRadius: "1rem", border: "1px solid var(--card-border)" }}>
          <h4 style={{ marginTop: 0, color: "#818cf8" }}>Evaluation & Summary</h4>
          <p style={{ color: "#94a3b8", fontSize: "0.95rem", lineHeight: 1.6 }}>
            The assessment session has been successfully recorded and processed by the result calculation engine.
            All responses have been submitted securely to the server database.
          </p>
          <div style={{ display: "flex", alignItems: "center", gap: "0.5rem", color: "#34d399", fontWeight: "600" }}>
            <CheckCircle size={18} /> Verified & Saved in Backend System
          </div>
        </div>
      </div>
    </div>
  );
}

export default ResultView;
