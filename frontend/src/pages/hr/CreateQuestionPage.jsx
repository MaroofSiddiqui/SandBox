import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import Sidebar from "../../components/common/Sidebar";
import Topbar from "../../components/common/Topbar";
import { ArrowLeft, Save, CheckCircle, Plus, Trash2 } from "lucide-react";
import { useAuth } from "../../context/AuthContext";
import "../../styles/hr-dashboard.css"; 

function CreateQuestionPage() {
    const navigate = useNavigate();
    const { token } = useAuth();
    
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState(false);

    // Matches your QuestionDto exactly
    const [formData, setFormData] = useState({
        title: "",
        questionType: "MCQ", // Default to MCQ
        difficulty: "EASY",
        marks: 1.0,
        category: "",
        
        // Exact match for McqOptionDto fields
        mcqOptions: [
            { optionText: "", isCorrect: false },
            { optionText: "", isCorrect: false }
        ],

        // Coding Fields
        problemStatement: "",
        driverCode: "",
        sampleTestCasesJson: "[]",
        hiddenTestCasesJson: "[]",
        timeLimitInSeconds: 2,
        memoryLimitInMb: 128
    });

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData({
            ...formData,
            [name]: type === "checkbox" ? checked : (type === "number" ? Number(value) : value)
        });
    };

    // --- MCQ OPTION HANDLERS ---
    const handleOptionChange = (index, field, value) => {
        const newOptions = [...formData.mcqOptions];
        newOptions[index][field] = value;
        setFormData({ ...formData, mcqOptions: newOptions });
    };

    const addOption = () => {
        setFormData({ ...formData, mcqOptions: [...formData.mcqOptions, { optionText: "", isCorrect: false }] });
    };

    const removeOption = (index) => {
        const newOptions = formData.mcqOptions.filter((_, i) => i !== index);
        setFormData({ ...formData, mcqOptions: newOptions });
    };

    // --- SUBMIT TO BACKEND ---
    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError("");
        
        // Clean up payload based on type so we don't send null/unnecessary data
        const payload = {
            title: formData.title,
            questionType: formData.questionType,
            difficulty: formData.difficulty,
            marks: formData.marks,
            category: formData.category
        };

        if (formData.questionType === "MCQ") {
            payload.mcqOptions = formData.mcqOptions;
        } else {
            payload.problemStatement = formData.problemStatement;
            payload.driverCode = formData.driverCode;
            payload.sampleTestCasesJson = formData.sampleTestCasesJson;
            payload.hiddenTestCasesJson = formData.hiddenTestCasesJson;
            payload.timeLimitInSeconds = formData.timeLimitInSeconds;
            payload.memoryLimitInMb = formData.memoryLimitInMb;
        }

        try {
            const response = await fetch("http://localhost:8082/question/create", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}` 
                },
                body: JSON.stringify(payload)
            });

            if (!response.ok) throw new Error("Failed to save question to database.");

            setSuccess(true);
            setTimeout(() => {
                navigate("/hr/questions"); 
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
                    
                    {/* Header */}
                    <div className="section-header" style={{ marginBottom: "24px" }}>
                        <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
                            <button 
                                onClick={() => navigate(-1)}
                                style={{ background: "white", border: "1px solid #e2e8f0", padding: "8px", borderRadius: "8px", cursor: "pointer" }}
                            >
                                <ArrowLeft size={20} />
                            </button>
                            <h2>Add New Question</h2>
                        </div>
                    </div>

                    <div className="recent-activity-section" style={{ maxWidth: "800px" }}>
                        {error && <div style={{ color: "red", padding: "10px", background: "#fee2e2", borderRadius: "5px", marginBottom: "15px" }}>{error}</div>}
                        {success && (
                            <div style={{ color: "green", padding: "10px", background: "#dcfce7", borderRadius: "5px", marginBottom: "15px", display: "flex", alignItems: "center", gap: "8px" }}>
                                <CheckCircle size={20} /> Question Saved to Database!
                            </div>
                        )}

                        <form onSubmit={handleSubmit} style={{ display: "flex", flexDirection: "column", gap: "20px" }}>
                            
                            {/* TOP LEVEL CONFIG */}
                            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "16px" }}>
                                <div>
                                    <label style={{ display: "block", marginBottom: "8px", fontWeight: "600", color: "#1e293b" }}>Question Type</label>
                                    <select 
                                        name="questionType" 
                                        value={formData.questionType} 
                                        onChange={handleChange}
                                        style={{ width: "100%", padding: "10px", borderRadius: "8px", border: "1px solid #cbd5e1", backgroundColor: "white" }}
                                    >
                                        <option value="MCQ">Multiple Choice (MCQ)</option>
                                        <option value="CODING">Programming / Coding</option>
                                    </select>
                                </div>
                                <div>
                                    <label style={{ display: "block", marginBottom: "8px", fontWeight: "600", color: "#1e293b" }}>Category / Tag</label>
                                    <input 
                                        type="text" 
                                        name="category"
                                        value={formData.category}
                                        onChange={handleChange}
                                        placeholder="e.g., Java, React, SQL"
                                        style={{ width: "100%", padding: "10px", borderRadius: "8px", border: "1px solid #cbd5e1" }}
                                    />
                                </div>
                            </div>

                            <div>
                                <label style={{ display: "block", marginBottom: "8px", fontWeight: "600", color: "#1e293b" }}>Question Title</label>
                                <input 
                                    type="text" 
                                    name="title"
                                    value={formData.title}
                                    onChange={handleChange}
                                    required
                                    style={{ width: "100%", padding: "10px", borderRadius: "8px", border: "1px solid #cbd5e1" }}
                                />
                            </div>

                            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "16px" }}>
                                <div>
                                    <label style={{ display: "block", marginBottom: "8px", fontWeight: "600", color: "#1e293b" }}>Difficulty</label>
                                    <select 
                                        name="difficulty" 
                                        value={formData.difficulty} 
                                        onChange={handleChange}
                                        style={{ width: "100%", padding: "10px", borderRadius: "8px", border: "1px solid #cbd5e1", backgroundColor: "white" }}
                                    >
                                        <option value="EASY">Easy</option>
                                        <option value="MEDIUM">Medium</option>
                                        <option value="HARD">Hard</option>
                                    </select>
                                </div>
                                <div>
                                    <label style={{ display: "block", marginBottom: "8px", fontWeight: "600", color: "#1e293b" }}>Marks</label>
                                    <input 
                                        type="number" 
                                        name="marks"
                                        value={formData.marks}
                                        onChange={handleChange}
                                        required
                                        step="0.5"
                                        style={{ width: "100%", padding: "10px", borderRadius: "8px", border: "1px solid #cbd5e1" }}
                                    />
                                </div>
                            </div>

                            <hr style={{ borderTop: "1px solid #e2e8f0", margin: "10px 0" }} />

                            {/* CONDITIONAL RENDER: MCQ FIELDS */}
                            {formData.questionType === "MCQ" && (
                                <div>
                                    <h4 style={{ marginBottom: "16px", color: "#0f172a" }}>Multiple Choice Options</h4>
                                    {formData.mcqOptions.map((option, index) => (
                                        <div key={index} style={{ display: "flex", alignItems: "center", gap: "12px", marginBottom: "12px" }}>
                                            <input 
                                                type="checkbox"
                                                checked={option.isCorrect}
                                                onChange={(e) => handleOptionChange(index, "isCorrect", e.target.checked)}
                                                style={{ width: "20px", height: "20px" }}
                                                title="Mark as correct answer"
                                            />
                                            <input 
                                                type="text" 
                                                value={option.optionText}
                                                onChange={(e) => handleOptionChange(index, "optionText", e.target.value)}
                                                placeholder={`Option ${index + 1}`}
                                                required
                                                style={{ flex: 1, padding: "10px", borderRadius: "8px", border: "1px solid #cbd5e1" }}
                                            />
                                            {formData.mcqOptions.length > 2 && (
                                                <button type="button" onClick={() => removeOption(index)} style={{ background: "none", border: "none", color: "#ef4444", cursor: "pointer" }}>
                                                    <Trash2 size={20} />
                                                </button>
                                            )}
                                        </div>
                                    ))}
                                    <button 
                                        type="button" 
                                        onClick={addOption}
                                        style={{ display: "flex", alignItems: "center", gap: "6px", background: "none", border: "none", color: "#3b82f6", fontWeight: "600", cursor: "pointer", marginTop: "10px" }}
                                    >
                                        <Plus size={16} /> Add Another Option
                                    </button>
                                </div>
                            )}

                            {/* CONDITIONAL RENDER: CODING FIELDS */}
                            {formData.questionType === "CODING" && (
                                <div style={{ display: "flex", flexDirection: "column", gap: "16px" }}>
                                    <div>
                                        <label style={{ display: "block", marginBottom: "8px", fontWeight: "600", color: "#1e293b" }}>Problem Statement</label>
                                        <textarea 
                                            name="problemStatement"
                                            value={formData.problemStatement}
                                            onChange={handleChange}
                                            required
                                            placeholder="Describe the coding challenge..."
                                            style={{ width: "100%", padding: "10px", borderRadius: "8px", border: "1px solid #cbd5e1", minHeight: "120px" }}
                                        />
                                    </div>
                                    <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "16px" }}>
                                        <div>
                                            <label style={{ display: "block", marginBottom: "8px", fontWeight: "600", color: "#1e293b" }}>Time Limit (Seconds)</label>
                                            <input 
                                                type="number" 
                                                name="timeLimitInSeconds"
                                                value={formData.timeLimitInSeconds}
                                                onChange={handleChange}
                                                required
                                                style={{ width: "100%", padding: "10px", borderRadius: "8px", border: "1px solid #cbd5e1" }}
                                            />
                                        </div>
                                        <div>
                                            <label style={{ display: "block", marginBottom: "8px", fontWeight: "600", color: "#1e293b" }}>Memory Limit (MB)</label>
                                            <input 
                                                type="number" 
                                                name="memoryLimitInMb"
                                                value={formData.memoryLimitInMb}
                                                onChange={handleChange}
                                                required
                                                style={{ width: "100%", padding: "10px", borderRadius: "8px", border: "1px solid #cbd5e1" }}
                                            />
                                        </div>
                                    </div>
                                </div>
                            )}

                            <button 
                                type="submit" 
                                disabled={isLoading}
                                className="action-btn" 
                                style={{ backgroundColor: "#0f172a", color: "white", justifyContent: "center", marginTop: "24px", padding: "12px", border: "none" }}
                            >
                                {isLoading ? "Saving to Database..." : <><Save size={18} /> Save Question</>}
                            </button>
                        </form>
                    </div>
                </div>
            </main>
        </div>
    );
}

export default CreateQuestionPage;