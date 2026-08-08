import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Sidebar from "../../components/common/Sidebar";
import Topbar from "../../components/common/Topbar";
import DashboardCard from "../../components/common/DashboardCard";
import { useAuth } from "../../context/AuthContext";

import { 
    FileText, 
    Activity, 
    UserCheck, 
    Plus, 
    Users, 
    Video, 
    CalendarCheck 
} from "lucide-react";

import "../../styles/hr-dashboard.css";

function HrDashboard() {
    const navigate = useNavigate();
    const { token } = useAuth();

    // STRICTLY THE 3 METRICS YOU REQUESTED
    const [stats, setStats] = useState({
        examsCreated: 0,
        studentsGivingExam: 0,
        shortlistedForInterview: 0
    });
    
    const [activeExams, setActiveExams] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState("");

    // DYNAMIC FETCH TO BACKEND (We will map this to your actual controller next)
    useEffect(() => {
        const fetchDashboardData = async () => {
            if (!token) return;
            try {
                // NOTE: We will update this URL once you share the Dashboard Controller!
                const response = await fetch("http://localhost:8082/hr/dashboard/metrics", {
                    method: "GET",
                    headers: {
                        "Authorization": `Bearer ${token}`,
                        "Content-Type": "application/json"
                    }
                });

                if (!response.ok) throw new Error("Failed to fetch dashboard metrics.");
                
                const data = await response.json();
                setStats({
                    examsCreated: data.examsCreated || 0,
                    studentsGivingExam: data.studentsGivingExam || 0,
                    shortlistedForInterview: data.shortlistedForInterview || 0
                });
                setActiveExams(data.recentExams || []);

            } catch (err) {
                console.error("Dashboard Fetch Error:", err);
                // Forcing mock data just so the UI doesn't break while we wire the backend
                setStats({ examsCreated: 12, studentsGivingExam: 45, shortlistedForInterview: 18 });
                setActiveExams([
                    { id: 101, title: "Java Full Stack Developer", activeTakers: 22, status: "LIVE" },
                    { id: 102, title: "React Frontend Intern", activeTakers: 0, status: "REVIEW PENDING" }
                ]);
            } finally {
                setIsLoading(false);
            }
        };

        fetchDashboardData();
    }, [token]);

    return (
        <div className="dashboard-layout">
            <Sidebar />
            <main className="main-content">
                <Topbar />
                
                <div className="dashboard-content">
                    {/* PROFESSIONAL HEADER */}
                    <div style={{ marginBottom: "12px" }}>
                        <h2 style={{ color: "#0f172a", fontSize: "1.75rem", fontWeight: "700" }}>HR Command Center</h2>
                        <p style={{ color: "#64748b", fontSize: "0.95rem" }}>Monitor examination pipeline and interview shortlisting.</p>
                    </div>

                    {/* FOCUSED METRICS GRID */}
                    <div className="metrics-grid" style={{ gridTemplateColumns: "repeat(3, 1fr)" }}>
                        <DashboardCard 
                            title="Exams Created" 
                            value={stats.examsCreated} 
                            icon={<FileText size={28} />} 
                            color="#3b82f6" // Professional Blue
                        />
                        <DashboardCard 
                            title="Students Giving Exam" 
                            value={stats.studentsGivingExam} 
                            icon={<Activity size={28} />} 
                            color="#ef4444" // Live/Active Red
                        />
                        <DashboardCard 
                            title="Shortlisted for Interview" 
                            value={stats.shortlistedForInterview} 
                            icon={<UserCheck size={28} />} 
                            color="#10b981" // Success Green
                        />
                    </div>

                    {/* PURPOSE-DRIVEN ACTION BUTTONS */}
                    <div className="quick-actions-section" style={{ marginTop: "24px", background: "white", padding: "20px", borderRadius: "12px", border: "1px solid #e2e8f0" }}>
                        <h3 style={{ marginBottom: "16px", color: "#1e293b", fontSize: "1.1rem" }}>Core Operations</h3>
                        <div style={{ display: "flex", gap: "16px", flexWrap: "wrap" }}>
                            {/* 1. Exam Creation */}
                            <button 
                                className="action-btn" 
                                style={{ backgroundColor: "#0f172a", color: "white", border: "none", padding: "12px 24px" }}
                                onClick={() => navigate("/hr/assessments/create")}
                            >
                                <Plus size={18} /> Create New Exam
                            </button>
                            
                            {/* 2. Assign Candidates */}
                            <button 
                                className="action-btn"
                                style={{ backgroundColor: "#f8fafc", color: "#334155", border: "1px solid #cbd5e1", padding: "12px 24px" }}
                                onClick={() => navigate("/hr/assessments")}
                            >
                                <Users size={18} /> Assign Candidates to Exam
                            </button>

                            {/* 3. Live Monitoring */}
                            <button 
                                className="action-btn"
                                style={{ backgroundColor: "#fef2f2", color: "#dc2626", border: "1px solid #fecaca", padding: "12px 24px" }}
                            >
                                <Video size={18} /> Live Proctoring Monitor
                            </button>

                            {/* 4. Interview Scheduling */}
                            <button 
                                className="action-btn"
                                style={{ backgroundColor: "#ecfdf5", color: "#059669", border: "1px solid #a7f3d0", padding: "12px 24px" }}
                            >
                                <CalendarCheck size={18} /> Process Shortlisted
                            </button>
                        </div>
                    </div>

                    {/* ACTIVE PIPELINE TABLE */}
                    <div className="recent-activity-section" style={{ marginTop: "24px" }}>
                        <div className="section-header">
                            <h3 style={{ color: "#1e293b" }}>Exam Pipeline Status</h3>
                        </div>
                        
                        <div className="table-container">
                            <table className="custom-table">
                                <thead>
                                    <tr style={{ backgroundColor: "#f8fafc" }}>
                                        <th>Exam ID</th>
                                        <th>Exam Title</th>
                                        <th>Active Test Takers</th>
                                        <th>System Status</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {activeExams.map((exam) => (
                                        <tr key={exam.id}>
                                            <td><strong>EXAM-{exam.id}</strong></td>
                                            <td>{exam.title}</td>
                                            <td>
                                                {exam.activeTakers > 0 ? (
                                                    <span style={{ color: "#dc2626", fontWeight: "600", display: "flex", alignItems: "center", gap: "6px" }}>
                                                        <span style={{ width: "8px", height: "8px", backgroundColor: "#dc2626", borderRadius: "50%", display: "inline-block", animation: "pulse 2s infinite" }}></span>
                                                        {exam.activeTakers} Testing Now
                                                    </span>
                                                ) : (
                                                    <span style={{ color: "#64748b" }}>0 Active</span>
                                                )}
                                            </td>
                                            <td>
                                                <span className={`status-badge ${exam.status === 'LIVE' ? 'active' : 'completed'}`}>
                                                    {exam.status}
                                                </span>
                                            </td>
                                            <td>
                                                <button 
                                                    className="link-btn" 
                                                    style={{ color: "#3b82f6", fontWeight: "600" }}
                                                    onClick={() => navigate(`/hr/assessments/${exam.id}/manage`)}
                                                >
                                                    Manage Exam ➔
                                                </button>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    </div>

                </div>
            </main>
        </div>
    );
}

export default HrDashboard;