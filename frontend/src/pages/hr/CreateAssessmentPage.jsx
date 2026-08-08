import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import Sidebar from "../../components/common/Sidebar";
import Topbar from "../../components/common/Topbar";
import { ArrowLeft, Save, CheckCircle } from "lucide-react";
import { useAuth } from "../../context/AuthContext";
import "../../styles/hr-dashboard.css"; 

function CreateAssessmentPage() {
    const navigate = useNavigate();
    const { token } = useAuth();
    
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState(false);

    // MATCHES AssessmentDto EXACTLY
    const [formData, setFormData] = useState({
        title: "",
        description: "",
        durationInMinutes: 60,
        passingMarks: 50.0,
        negativeMarks: 0.0,
        isPublished: false,
        questionIds: [] 
    });

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData({
            ...formData,
            [name]: type === "checkbox" ? checked : value
        });
    };

    // DYNAMIC POST TO BACKEND
    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError("");
        
        try {
            const response = await fetch("http://localhost:8082/assessment/create", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}` 
                },
                body: JSON.stringify({
                    title: formData.title,
                    description: formData.description,
                    durationInMinutes: parseInt(formData.durationInMinutes),
                    passingMarks: parseFloat(formData.passingMarks),
                    negativeMarks: parseFloat(formData.negativeMarks),
                    isPublished: formData.isPublished,
                    questionIds: formData.questionIds 
                })
            });

            if (!response.ok) throw new Error("Failed to save to backend.");

            setSuccess(true);
            setTimeout(() => {
                navigate("/hr/assessments");
            }, 1500);

        } catch (err) {
            setError(err.message);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="dashboard-layout">
            <Sidebar />
            <main className="main-content">
                <Topbar />
                <div className="dashboard-content">
                    <div className="section-header" style={{ marginBottom: "24px" }}>
                        <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
                            <button 
                                onClick={() => navigate("/hr/assessments")}
                                style={{ background: "white", border: "1px solid #e2e8f0", padding: "8px", borderRadius: "8px", cursor: "pointer" }}
                            >
                                <ArrowLeft size={20} />
                            </button>
                            <h2>Create New Assessment</h2>
                        </div>
                    </div>

                    <div className="recent-activity-section" style={{ maxWidth: "800px" }}>
                        {error && <div style={{ color: "red", padding: "10px", background: "#fee2e2", borderRadius: "5px", marginBottom: "15px" }}>{error}</div>}
                        {success && (
                            <div style={{ color: "green", padding: "10px", background: "#dcfce7", borderRadius: "5px", marginBottom: "15px", display: "flex", alignItems: "center", gap: "8px" }}>
                                <CheckCircle size={20} /> Saved to Database Successfully!
                            </div>
                        )}

                        <form onSubmit={handleSubmit} style={{ display: "flex", flexDirection: "column", gap: "20px" }}>
                            <div>
                                <label style={{ display: "block", marginBottom: "8px", fontWeight: "600", color: "#1e293b" }}>Assessment Title</label>
                                <input 
                                    type="text" 
                                    name="title"
                                    value={formData.title}
                                    onChange={handleChange}
                                    required
                                    style={{ width: "100%", padding: "10px", borderRadius: "8px", border: "1px solid #cbd5e1" }}
                                />
                            </div>

                            <div>
                                <label style={{ display: "block", marginBottom: "8px", fontWeight: "600", color: "#1e293b" }}>Description</label>
                                <textarea 
                                    name="description"
                                    value={formData.description}
                                    onChange={handleChange}
                                    style={{ width: "100%", padding: "10px", borderRadius: "8px", border: "1px solid #cbd5e1", minHeight: "100px" }}
                                />
                            </div>

                            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr 1fr", gap: "16px" }}>
                                <div>
                                    <label style={{ display: "block", marginBottom: "8px", fontWeight: "600", color: "#1e293b" }}>Duration (Minutes)</label>
                                    <input 
                                        type="number" 
                                        name="durationInMinutes"
                                        value={formData.durationInMinutes}
                                        onChange={handleChange}
                                        required
                                        style={{ width: "100%", padding: "10px", borderRadius: "8px", border: "1px solid #cbd5e1" }}
                                    />
                                </div>
                                <div>
                                    <label style={{ display: "block", marginBottom: "8px", fontWeight: "600", color: "#1e293b" }}>Passing Marks (%)</label>
                                    <input 
                                        type="number" 
                                        name="passingMarks"
                                        value={formData.passingMarks}
                                        onChange={handleChange}
                                        required
                                        style={{ width: "100%", padding: "10px", borderRadius: "8px", border: "1px solid #cbd5e1" }}
                                    />
                                </div>
                                <div>
                                    <label style={{ display: "block", marginBottom: "8px", fontWeight: "600", color: "#1e293b" }}>Negative Marks</label>
                                    <input 
                                        type="number" 
                                        name="negativeMarks"
                                        value={formData.negativeMarks}
                                        onChange={handleChange}
                                        required
                                        step="0.1"
                                        style={{ width: "100%", padding: "10px", borderRadius: "8px", border: "1px solid #cbd5e1" }}
                                    />
                                </div>
                            </div>

                            <div style={{ display: "flex", alignItems: "center", gap: "10px", marginTop: "10px" }}>
                                <input 
                                    type="checkbox" 
                                    name="isPublished"
                                    id="isPublished"
                                    checked={formData.isPublished}
                                    onChange={handleChange}
                                    style={{ width: "18px", height: "18px" }}
                                />
                                <label htmlFor="isPublished" style={{ fontWeight: "600", color: "#1e293b", cursor: "pointer" }}>
                                    Publish immediately
                                </label>
                            </div>

                            <button 
                                type="submit" 
                                disabled={isLoading}
                                className="action-btn live-btn" 
                                style={{ backgroundColor: "#3b82f6", borderColor: "#3b82f6", justifyContent: "center", marginTop: "16px", padding: "12px" }}
                            >
                                {isLoading ? "Saving to Backend..." : <><Save size={18} /> Save Assessment to Database</>}
                            </button>
                        </form>
                    </div>
                </div>
            </main>
        </div>
    );
}

export default CreateAssessmentPage;