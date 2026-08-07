import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  getAllAssignments,
  getAssignmentDashboardAnalytics,
  assignAssessment,
  getAllAssessmentsList,
} from "../api/assignmentApi";
import {
  FileText,
  Clock,
  CheckCircle2,
  BarChart2,
  PlusCircle,
  Play,
  RotateCcw,
  Eye,
  UserCheck,
} from "lucide-react";
import "../styles/examination.css";

function CandidateDashboard() {
  const navigate = useNavigate();
  const [assignments, setAssignments] = useState([]);
  const [analytics, setAnalytics] = useState({
    totalAssignments: 0,
    assigned: 0,
    inProgress: 0,
    submitted: 0,
    evaluated: 0,
  });
  const [activeFilter, setActiveFilter] = useState("ALL");
  const [loading, setLoading] = useState(true);

  // Modal State for Assigning Candidate
  const [showAssignModal, setShowAssignModal] = useState(false);
  const [assessmentsList, setAssessmentsList] = useState([]);
  const [newAssignment, setNewAssignment] = useState({
    assessmentId: "",
    candidateId: "1",
  });

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      const [assignmentsRes, analyticsRes] = await Promise.allSettled([
        getAllAssignments(),
        getAssignmentDashboardAnalytics(),
      ]);

      if (assignmentsRes.status === "fulfilled" && assignmentsRes.value.data) {
        setAssignments(assignmentsRes.value.data);
      }
      if (analyticsRes.status === "fulfilled" && analyticsRes.value.data) {
        setAnalytics(analyticsRes.value.data);
      }
    } catch (err) {
      console.error("Error fetching candidate dashboard data:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleOpenAssignModal = async () => {
    try {
      const res = await getAllAssessmentsList();
      if (res && res.data) {
        setAssessmentsList(res.data);
      }
    } catch (err) {
      console.error("Failed to load assessments list", err);
    }
    setShowAssignModal(true);
  };

  const handleCreateAssignment = async (e) => {
    e.preventDefault();
    if (!newAssignment.assessmentId || !newAssignment.candidateId) return;

    try {
      await assignAssessment({
        assessmentId: Number(newAssignment.assessmentId),
        candidateId: Number(newAssignment.candidateId),
      });
      setShowAssignModal(false);
      fetchDashboardData();
    } catch (err) {
      alert(err.response?.data?.message || "Failed to assign assessment");
    }
  };

  const filteredAssignments = assignments.filter((item) => {
    if (activeFilter === "ALL") return true;
    return item.status === activeFilter;
  });

  return (
    <div className="candidate-dashboard-container">
      {/* Header */}
      <div className="portal-header">
        <div className="portal-title">
          <h1>Candidate Assessment Portal</h1>
          <p>Member 4 Module • Manage tests, track exam status & view analytics</p>
        </div>
        <button className="btn-primary" onClick={handleOpenAssignModal}>
          <PlusCircle size={18} /> Assign Assessment
        </button>
      </div>

      {/* Analytics Overview Cards */}
      <div className="metrics-grid">
        <div className="stat-card">
          <div className="stat-icon total">
            <FileText />
          </div>
          <div className="stat-info">
            <h3>{analytics.totalAssignments || assignments.length}</h3>
            <p>Total Assignments</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon assigned">
            <UserCheck />
          </div>
          <div className="stat-info">
            <h3>{analytics.assigned || assignments.filter((a) => a.status === "ASSIGNED").length}</h3>
            <p>Assigned</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon in-progress">
            <Clock />
          </div>
          <div className="stat-info">
            <h3>{analytics.inProgress || assignments.filter((a) => a.status === "IN_PROGRESS").length}</h3>
            <p>In Progress</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon submitted">
            <CheckCircle2 />
          </div>
          <div className="stat-info">
            <h3>{analytics.submitted || assignments.filter((a) => a.status === "SUBMITTED").length}</h3>
            <p>Submitted</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon evaluated">
            <BarChart2 />
          </div>
          <div className="stat-info">
            <h3>{analytics.evaluated || assignments.filter((a) => a.status === "EVALUATED").length}</h3>
            <p>Evaluated</p>
          </div>
        </div>
      </div>

      {/* Controls Bar & Filters */}
      <div className="controls-bar">
        <div className="filter-tabs">
          {["ALL", "ASSIGNED", "IN_PROGRESS", "SUBMITTED", "EVALUATED"].map((tab) => (
            <button
              key={tab}
              className={`tab-btn ${activeFilter === tab ? "active" : ""}`}
              onClick={() => setActiveFilter(tab)}
            >
              {tab.replace("_", " ")}
            </button>
          ))}
        </div>
      </div>

      {/* Assignments List */}
      {loading ? (
        <p style={{ textAlign: "center", color: "#94a3b8" }}>Loading assignments...</p>
      ) : filteredAssignments.length === 0 ? (
        <div style={{ textAlign: "center", padding: "4rem", background: "rgba(30,41,59,0.5)", borderRadius: "1rem" }}>
          <FileText size={48} style={{ color: "#64748b", marginBottom: "1rem" }} />
          <h3>No assignments found</h3>
          <p style={{ color: "#94a3b8" }}>Assign an assessment to get started.</p>
        </div>
      ) : (
        <div className="assignment-grid">
          {filteredAssignments.map((assignment) => (
            <div key={assignment.assignmentId} className="assignment-card">
              <div>
                <div className="card-top">
                  <h3>Assignment #{assignment.assignmentId}</h3>
                  <span className={`badge ${assignment.status}`}>{assignment.status}</span>
                </div>

                <div className="card-details">
                  <div>
                    <strong>Assessment ID:</strong> #{assignment.assessmentId}
                  </div>
                  <div>
                    <strong>Candidate ID:</strong> #{assignment.candidateId}
                  </div>
                  {assignment.assignedAt && (
                    <div>
                      <strong>Assigned At:</strong> {new Date(assignment.assignedAt).toLocaleString()}
                    </div>
                  )}
                </div>
              </div>

              <div className="card-actions">
                {assignment.status === "ASSIGNED" && (
                  <button
                    className="btn-action start"
                    onClick={() => navigate(`/exam/${assignment.assignmentId}`)}
                  >
                    <Play size={16} /> Start Exam
                  </button>
                )}

                {assignment.status === "IN_PROGRESS" && (
                  <button
                    className="btn-action resume"
                    onClick={() => navigate(`/exam/${assignment.assignmentId}`)}
                  >
                    <RotateCcw size={16} /> Resume Exam
                  </button>
                )}

                {(assignment.status === "SUBMITTED" || assignment.status === "EVALUATED") && (
                  <button
                    className="btn-action view"
                    onClick={() => navigate(`/result/${assignment.assignmentId}`)}
                  >
                    <Eye size={16} /> View Result
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Assign Modal */}
      {showAssignModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3 style={{ marginTop: 0 }}>Assign Candidate to Assessment</h3>
            <form onSubmit={handleCreateAssignment}>
              <div className="form-group">
                <label>Select Assessment</label>
                <select
                  className="form-control"
                  value={newAssignment.assessmentId}
                  onChange={(e) => setNewAssignment({ ...newAssignment, assessmentId: e.target.value })}
                  required
                >
                  <option value="">-- Choose Assessment --</option>
                  {assessmentsList.map((ast) => (
                    <option key={ast.id} value={ast.id}>
                      #{ast.id} - {ast.title || "Assessment"} ({ast.durationInMinutes || 30} mins)
                    </option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label>Candidate ID</label>
                <input
                  type="number"
                  className="form-control"
                  value={newAssignment.candidateId}
                  onChange={(e) => setNewAssignment({ ...newAssignment, candidateId: e.target.value })}
                  required
                />
              </div>

              <div style={{ display: "flex", justifyContent: "flex-end", gap: "0.75rem", marginTop: "1.5rem" }}>
                <button type="button" className="btn-secondary" onClick={() => setShowAssignModal(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn-primary">
                  Create Assignment
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default CandidateDashboard;
