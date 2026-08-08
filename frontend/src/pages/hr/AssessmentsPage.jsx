import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Sidebar from "../../components/common/Sidebar";
import Topbar from "../../components/common/Topbar";
import { PlusCircle, Settings } from "lucide-react";
import { useAuth } from "../../context/AuthContext";
import "../../styles/hr-dashboard.css";

function AssessmentsPage() {
    const navigate = useNavigate();
    const { token } = useAuth();
    
    const [assessments, setAssessments] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState("");

    // DYNAMIC FETCH TO BACKEND
    useEffect(() => {
        const fetchAssessments = async () => {
            if (!token) return;
            try {
                const response = await fetch("http://localhost:8082/assessment/all", {
                    method: "GET",
                    headers: {
                        "Authorization": `Bearer ${token}`,
                        "Content-Type": "application/json"
                    }
                });

                if (!response.ok) throw new Error("Failed to fetch assessments from backend.");
                
                const data = await response.json();
                setAssessments(data);
            } catch (err) {
                setError(err.message);
            } finally {
                setIsLoading(false);
            }
        };

        fetchAssessments();
    }, [token]);

    return (
        <div className="dashboard-layout">
            <Sidebar />
            <main className="main-content">
                <Topbar />
                <div className="dashboard-content">
                    <div className="section-header">
                        <div>
                            <h2>Assessment Management</h2>
                            <p style={{ color: "#64748b", marginTop: "4px" }}>Manage and configure all organizational examinations.</p>
                        </div>
                        <button 
                            className="action-btn live-btn" 
                            style={{ backgroundColor: "#3b82f6", borderColor: "#3b82f6" }}
                            onClick={() => navigate("/hr/assessments/create")}
                        >
                            <PlusCircle size={18} /> Create New Assessment
                        </button>
                    </div>

                    <div className="recent-activity-section">
                        {isLoading && <p>Loading dynamic data from backend...</p>}
                        {error && <p style={{ color: "red" }}>{error}</p>}
                        
                        {!isLoading && !error && (
                            <div className="table-container">
                                <table className="custom-table">
                                    <thead>
                                        <tr>
                                            <th>Exam ID</th>
                                            <th>Title</th>
                                            <th>Duration</th>
                                            <th>Passing Marks</th>
                                            <th>Status</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {assessments.length === 0 ? (
                                            <tr>
                                                <td colSpan="6" style={{textAlign: "center"}}>No assessments found in database.</td>
                                            </tr>
                                        ) : (
                                            assessments.map((exam) => (
                                                <tr key={exam.id}>
                                                    <td><strong>EXAM-{exam.id}</strong></td>
                                                    <td>{exam.title}</td>
                                                    <td>{exam.durationInMinutes} mins</td>
                                                    <td>{exam.passingMarks}%</td>
                                                    <td>
                                                        <span className={`status-badge ${exam.isPublished ? 'completed' : 'active'}`}>
                                                            {exam.isPublished ? 'Published' : 'Draft'}
                                                        </span>
                                                    </td>
                                                    <td>
                                                        <button 
                                                            className="action-btn"
                                                            style={{ padding: "6px 12px", fontSize: "0.85rem" }}
                                                            onClick={() => navigate(`/hr/assessments/${exam.id}/manage`)}
                                                        >
                                                            <Settings size={14} /> Manage Exam
                                                        </button>
                                                    </td>
                                                </tr>
                                            ))
                                        )}
                                    </tbody>
                                </table>
                            </div>
                        )}
                    </div>
                </div>
            </main>
        </div>
    );
}

export default AssessmentsPage;