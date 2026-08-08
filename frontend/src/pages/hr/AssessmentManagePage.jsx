import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Sidebar from "../../components/common/Sidebar";
import Topbar from "../../components/common/Topbar";
import { ArrowLeft, Users, Video, Info, CheckCircle, Send, Plus } from "lucide-react";
import { useAuth } from "../../context/AuthContext";
import "../../styles/hr-dashboard.css"; 

function AssessmentManagePage() {
    const { examId } = useParams();
    const navigate = useNavigate();
    const { token } = useAuth();

    const [activeTab, setActiveTab] = useState("candidates"); // Default to candidates tab
    const [assessment, setAssessment] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    
    // Candidate Assignment State
    const [candidates, setCandidates] = useState([]); // List of all users
    const [selectedCandidateIds, setSelectedCandidateIds] = useState([]);
    const [assigning, setAssigning] = useState(false);
    const [assignMessage, setAssignMessage] = useState("");

    useEffect(() => {
        const fetchData = async () => {
            try {
                // 1. Fetch Exam Details
                const examRes = await fetch(`http://localhost:8082/assessment/${examId}`, {
                    headers: { "Authorization": `Bearer ${token}` }
                });
                const examData = await examRes.json();
                setAssessment(examData);

                // 2. Fetch All Candidates (Assuming auth-service runs on 8081)
                // UPDATE THIS URL if your user fetch endpoint is different
                const usersRes = await fetch(`http://localhost:8081/auth/users?role=CANDIDATE`, {
                    headers: { "Authorization": `Bearer ${token}` }
                });
                if (usersRes.ok) {
                    const usersData = await usersRes.json();
                    setCandidates(usersData);
                } else {
                    // Mock data if auth-service isn't ready
                    setCandidates([
                        { id: 1, name: "Rahul Sharma", email: "rahul@test.com" },
                        { id: 2, name: "Priya Patel", email: "priya@test.com" },
                        { id: 3, name: "Amit Kumar", email: "amit@test.com" }
                    ]);
                }
            } catch (err) {
                console.error(err);
            } finally {
                setIsLoading(false);
            }
        };

        if (token) fetchData();
    }, [examId, token]);

    // Handle Checkbox selection
    const handleSelectCandidate = (id) => {
        setSelectedCandidateIds(prev => 
            prev.includes(id) ? prev.filter(cId => cId !== id) : [...prev, id]
        );
    };

    // SUBMIT ASSIGNMENTS TO BACKEND
    const handleAssignCandidates = async () => {
        if (selectedCandidateIds.length === 0) {
            alert("Please select at least one candidate.");
            return;
        }

        setAssigning(true);
        setAssignMessage("");

        try {
            const response = await fetch(`http://localhost:8082/assessment/${examId}/assign`, {
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${token}`,
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ candidateIds: selectedCandidateIds })
            });

            if (!response.ok) throw new Error("Failed to assign candidates.");
            
            setAssignMessage(`Successfully assigned ${selectedCandidateIds.length} candidates to this exam!`);
            setSelectedCandidateIds([]); // Clear selection
            setTimeout(() => setAssignMessage(""), 4000);
            
        } catch (err) {
            alert(err.message);
        } finally {
            setAssigning(false);
        }
    };

    if (isLoading) return <div style={{ padding: "40px" }}>Loading command center...</div>;
    if (!assessment) return null;

    return (
        <div className="dashboard-layout">
            <Sidebar />
            <main className="main-content">
                <Topbar />
                <div className="dashboard-content">
                    
                    {/* Header */}
                    <div className="section-header" style={{ marginBottom: "20px", display: "flex", justifyContent: "space-between" }}>
                        <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
                            <button onClick={() => navigate("/hr/assessments")} style={{ background: "white", border: "1px solid #e2e8f0", padding: "8px", borderRadius: "8px", cursor: "pointer" }}>
                                <ArrowLeft size={20} />
                            </button>
                            <div>
                                <h2 style={{ color: "#0f172a", margin: 0 }}>{assessment.title}</h2>
                                <span style={{ color: "#64748b", fontSize: "0.9rem" }}>EXAM-{assessment.id}</span>
                            </div>
                        </div>
                    </div>

                    {/* Tabs */}
                    <div style={{ display: "flex", gap: "24px", borderBottom: "1px solid #e2e8f0", marginBottom: "24px" }}>
                        <button onClick={() => setActiveTab("candidates")} style={{ background: "none", border: "none", borderBottom: activeTab === "candidates" ? "2px solid #0f172a" : "2px solid transparent", padding: "12px 4px", fontSize: "1rem", fontWeight: "600", color: activeTab === "candidates" ? "#0f172a" : "#64748b", cursor: "pointer", display: "flex", gap: "8px", alignItems: "center" }}>
                            <Users size={18} /> Assign Candidates
                        </button>
                    </div>

                    {assignMessage && (
                        <div style={{ padding: "12px", backgroundColor: "#dcfce7", color: "#166534", borderRadius: "8px", marginBottom: "20px", border: "1px solid #bbf7d0" }}>
                            <CheckCircle size={18} style={{ display: "inline", verticalAlign: "middle", marginRight: "8px" }}/> 
                            {assignMessage}
                        </div>
                    )}

                    {/* ASSIGN CANDIDATES TAB */}
                    {activeTab === "candidates" && (
                        <div className="recent-activity-section">
                            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "20px" }}>
                                <div>
                                    <h3 style={{ color: "#1e293b" }}>Candidate Pool</h3>
                                    <p style={{ color: "#64748b", fontSize: "0.9rem" }}>Select users to invite to this examination.</p>
                                </div>
                                <button 
                                    className="action-btn" 
                                    style={{ backgroundColor: "#0f172a", color: "white", padding: "10px 20px", opacity: selectedCandidateIds.length === 0 ? 0.5 : 1 }}
                                    onClick={handleAssignCandidates}
                                    disabled={assigning || selectedCandidateIds.length === 0}
                                >
                                    {assigning ? "Assigning..." : <><Send size={16} /> Assign {selectedCandidateIds.length} Selected</>}
                                </button>
                            </div>
                            
                            <div className="table-container">
                                <table className="custom-table">
                                    <thead>
                                        <tr>
                                            <th style={{ width: "40px" }}></th>
                                            <th>Candidate ID</th>
                                            <th>Name</th>
                                            <th>Email</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {candidates.map((candidate) => (
                                            <tr key={candidate.id}>
                                                <td>
                                                    <input 
                                                        type="checkbox" 
                                                        checked={selectedCandidateIds.includes(candidate.id)}
                                                        onChange={() => handleSelectCandidate(candidate.id)}
                                                        style={{ width: "16px", height: "16px", cursor: "pointer" }}
                                                    />
                                                </td>
                                                <td><strong>CAN-{candidate.id}</strong></td>
                                                <td>{candidate.name}</td>
                                                <td>{candidate.email}</td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    )}
                </div>
            </main>
        </div>
    );
}

export default AssessmentManagePage;