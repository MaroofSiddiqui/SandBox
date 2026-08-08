import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Sidebar from "../../components/common/Sidebar";
import Topbar from "../../components/common/Topbar";
import {
    ArrowLeft,
    Users,
    CheckCircle,
    Send
} from "lucide-react";
import axiosInstance from "../../api/axiosInstance";
import "../../styles/hr-dashboard.css";

function AssessmentManagePage() {

    const { examId } = useParams();
    const navigate = useNavigate();

    const [activeTab, setActiveTab] = useState("candidates");

    const [assessment, setAssessment] = useState(null);
    const [isLoading, setIsLoading] = useState(true);

    // Real candidates from Auth Service
    const [candidates, setCandidates] = useState([]);

    const [selectedCandidateIds, setSelectedCandidateIds] = useState([]);

    const [assigning, setAssigning] = useState(false);

    const [assignMessage, setAssignMessage] = useState("");

    const [error, setError] = useState("");


    // ============================================================
    // LOAD ASSESSMENT + REAL CANDIDATES
    // ============================================================

    useEffect(() => {

        const fetchData = async () => {

            try {

                setIsLoading(true);
                setError("");

                // ------------------------------------------------
                // 1. GET ASSESSMENT
                // ------------------------------------------------

                const assessmentResponse = await fetch(
                    `http://localhost:8082/assessment/${examId}`,
                    {
                        headers: {
                            Authorization:
                                `Bearer ${localStorage.getItem("token")}`
                        }
                    }
                );

                if (!assessmentResponse.ok) {

                    throw new Error(
                        "Unable to load assessment."
                    );
                }

                const assessmentData =
                    await assessmentResponse.json();

                setAssessment(assessmentData);


                // ------------------------------------------------
                // 2. GET REAL CANDIDATES
                //
                // Auth Service:
                // GET /api/candidates
                //
                // axiosInstance already has:
                // baseURL = http://localhost:8081/api
                //
                // and automatically attaches JWT.
                // ------------------------------------------------

                const candidatesResponse =
                    await axiosInstance.get("/candidates");

                console.log(
                    "REAL CANDIDATES:",
                    candidatesResponse.data
                );

                setCandidates(
                    Array.isArray(candidatesResponse.data)
                        ? candidatesResponse.data
                        : []
                );

            } catch (err) {

                console.error(
                    "Assessment Manage Page Error:",
                    err
                );

                setError(
                    err.response?.data?.message ||
                    err.message ||
                    "Unable to load data."
                );

            } finally {

                setIsLoading(false);
            }
        };


        fetchData();

    }, [examId]);


    // ============================================================
    // SELECT / UNSELECT CANDIDATE
    // ============================================================

    const handleSelectCandidate = (id) => {

        setSelectedCandidateIds((previous) => {

            if (previous.includes(id)) {

                return previous.filter(
                    (candidateId) =>
                        candidateId !== id
                );
            }

            return [
                ...previous,
                id
            ];
        });
    };


    // ============================================================
    // ASSIGN CANDIDATES
    // ============================================================

    const handleAssignCandidates = async () => {

        if (selectedCandidateIds.length === 0) {

            alert(
                "Please select at least one candidate."
            );

            return;
        }

        try {

            setAssigning(true);
            setAssignMessage("");
            setError("");


            console.log(
                "Assigning candidate IDs:",
                selectedCandidateIds
            );


            // ------------------------------------------------
            // POST /assessment/{id}/assign
            // ------------------------------------------------

            const response = await fetch(
                `http://localhost:8082/assessment/${examId}/assign`,
                {
                    method: "POST",

                    headers: {
                        Authorization:
                            `Bearer ${localStorage.getItem("token")}`,

                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({
                        candidateIds:
                            selectedCandidateIds
                    })
                }
            );


            if (!response.ok) {

                let message =
                    "Failed to assign candidates.";

                try {

                    const errorData =
                        await response.json();

                    message =
                        errorData.message ||
                        message;

                } catch {
                    // Response may not contain JSON
                }

                throw new Error(message);
            }


            // ------------------------------------------------
            // SUCCESS
            // ------------------------------------------------

            setAssignMessage(
                `Successfully assigned ${selectedCandidateIds.length} candidate(s) to this assessment.`
            );


            setSelectedCandidateIds([]);


            // Remove success message after 4 seconds
            setTimeout(() => {

                setAssignMessage("");

            }, 4000);


        } catch (err) {

            console.error(
                "Assignment error:",
                err
            );

            setError(
                err.message ||
                "Failed to assign candidates."
            );

        } finally {

            setAssigning(false);
        }
    };


    // ============================================================
    // LOADING
    // ============================================================

    if (isLoading) {

        return (
            <div
                style={{
                    padding: "40px",
                    fontSize: "18px"
                }}
            >
                Loading assessment and candidates...
            </div>
        );
    }


    // ============================================================
    // ASSESSMENT NOT FOUND
    // ============================================================

    if (!assessment) {

        return (
            <div
                style={{
                    padding: "40px",
                    color: "#dc2626"
                }}
            >
                Assessment could not be loaded.
            </div>
        );
    }


    // ============================================================
    // UI
    // ============================================================

    return (

        <div className="dashboard-layout">

            <Sidebar />

            <main className="main-content">

                <Topbar />

                <div className="dashboard-content">


                    {/* ==================================================
                        HEADER
                       ================================================== */}

                    <div
                        className="section-header"
                        style={{
                            marginBottom: "20px",
                            display: "flex",
                            justifyContent:
                                "space-between"
                        }}
                    >

                        <div
                            style={{
                                display: "flex",
                                alignItems: "center",
                                gap: "12px"
                            }}
                        >

                            <button
                                onClick={() =>
                                    navigate(
                                        "/hr/assessments"
                                    )
                                }
                                style={{
                                    background: "white",
                                    border:
                                        "1px solid #e2e8f0",
                                    padding: "8px",
                                    borderRadius: "8px",
                                    cursor: "pointer"
                                }}
                            >

                                <ArrowLeft size={20} />

                            </button>


                            <div>

                                <h2
                                    style={{
                                        color: "#0f172a",
                                        margin: 0
                                    }}
                                >
                                    {assessment.title}
                                </h2>


                                <span
                                    style={{
                                        color: "#64748b",
                                        fontSize: "0.9rem"
                                    }}
                                >
                                    EXAM-{assessment.id}
                                </span>

                            </div>

                        </div>

                    </div>


                    {/* ==================================================
                        TAB
                       ================================================== */}

                    <div
                        style={{
                            display: "flex",
                            gap: "24px",
                            borderBottom:
                                "1px solid #e2e8f0",
                            marginBottom: "24px"
                        }}
                    >

                        <button
                            onClick={() =>
                                setActiveTab(
                                    "candidates"
                                )
                            }
                            style={{
                                background: "none",
                                border: "none",
                                borderBottom:
                                    activeTab ===
                                    "candidates"
                                        ? "2px solid #0f172a"
                                        : "2px solid transparent",
                                padding:
                                    "12px 4px",
                                fontSize:
                                    "1rem",
                                fontWeight: "600",
                                color:
                                    activeTab ===
                                    "candidates"
                                        ? "#0f172a"
                                        : "#64748b",
                                cursor: "pointer",
                                display: "flex",
                                gap: "8px",
                                alignItems:
                                    "center"
                            }}
                        >

                            <Users size={18} />

                            Assign Candidates

                        </button>

                    </div>


                    {/* ==================================================
                        ERROR MESSAGE
                       ================================================== */}

                    {error && (

                        <div
                            style={{
                                padding: "12px",
                                backgroundColor:
                                    "#fee2e2",
                                color: "#991b1b",
                                borderRadius: "8px",
                                marginBottom:
                                    "20px",
                                border:
                                    "1px solid #fecaca"
                            }}
                        >

                            {error}

                        </div>

                    )}


                    {/* ==================================================
                        SUCCESS MESSAGE
                       ================================================== */}

                    {assignMessage && (

                        <div
                            style={{
                                padding: "12px",
                                backgroundColor:
                                    "#dcfce7",
                                color: "#166534",
                                borderRadius: "8px",
                                marginBottom:
                                    "20px",
                                border:
                                    "1px solid #bbf7d0"
                            }}
                        >

                            <CheckCircle
                                size={18}
                                style={{
                                    display:
                                        "inline",
                                    verticalAlign:
                                        "middle",
                                    marginRight:
                                        "8px"
                                }}
                            />

                            {assignMessage}

                        </div>

                    )}


                    {/* ==================================================
                        CANDIDATE ASSIGNMENT
                       ================================================== */}

                    {activeTab ===
                        "candidates" && (

                        <div
                            className="recent-activity-section"
                        >

                            <div
                                style={{
                                    display:
                                        "flex",
                                    justifyContent:
                                        "space-between",
                                    alignItems:
                                        "center",
                                    marginBottom:
                                        "20px"
                                }}
                            >

                                <div>

                                    <h3
                                        style={{
                                            color:
                                                "#1e293b"
                                        }}
                                    >
                                        Candidate Pool
                                    </h3>

                                    <p
                                        style={{
                                            color:
                                                "#64748b",
                                            fontSize:
                                                "0.9rem"
                                        }}
                                    >
                                        Select real candidates
                                        from your organization
                                        to invite to this
                                        examination.
                                    </p>

                                </div>


                                <button
                                    className="action-btn"
                                    style={{
                                        backgroundColor:
                                            "#0f172a",
                                        color: "white",
                                        padding:
                                            "10px 20px",
                                        opacity:
                                            selectedCandidateIds.length ===
                                            0
                                                ? 0.5
                                                : 1
                                    }}
                                    onClick={
                                        handleAssignCandidates
                                    }
                                    disabled={
                                        assigning ||
                                        selectedCandidateIds.length ===
                                            0
                                    }
                                >

                                    {assigning
                                        ? "Assigning..."
                                        : (
                                            <>
                                                <Send
                                                    size={16}
                                                />

                                                Assign{" "}
                                                {
                                                    selectedCandidateIds.length
                                                }{" "}
                                                Selected
                                            </>
                                        )}

                                </button>

                            </div>


                            {/* ==================================================
                                NO CANDIDATES
                               ================================================== */}

                            {candidates.length ===
                                0 ? (

                                <div
                                    style={{
                                        padding:
                                            "30px",
                                        textAlign:
                                            "center",
                                        color:
                                            "#64748b",
                                        background:
                                            "#f8fafc",
                                        borderRadius:
                                            "8px"
                                    }}
                                >

                                    No candidates found
                                    for your organization.

                                </div>

                            ) : (

                                <div
                                    className="table-container"
                                >

                                    <table
                                        className="custom-table"
                                    >

                                        <thead>

                                            <tr>

                                                <th
                                                    style={{
                                                        width:
                                                            "40px"
                                                    }}
                                                />

                                                <th>
                                                    Candidate ID
                                                </th>

                                                <th>
                                                    Name
                                                </th>

                                                <th>
                                                    Email
                                                </th>

                                                <th>
                                                    Status
                                                </th>

                                            </tr>

                                        </thead>


                                        <tbody>

                                            {candidates.map(
                                                (
                                                    candidate
                                                ) => (

                                                    <tr
                                                        key={
                                                            candidate.id
                                                        }
                                                    >

                                                        <td>

                                                            <input
                                                                type="checkbox"
                                                                checked={selectedCandidateIds.includes(
                                                                    candidate.id
                                                                )}
                                                                onChange={() =>
                                                                    handleSelectCandidate(
                                                                        candidate.id
                                                                    )
                                                                }
                                                                style={{
                                                                    width:
                                                                        "16px",
                                                                    height:
                                                                        "16px",
                                                                    cursor:
                                                                        "pointer"
                                                                }}
                                                            />

                                                        </td>


                                                        <td>

                                                            <strong>
                                                                CAN-
                                                                {
                                                                    candidate.id
                                                                }
                                                            </strong>

                                                        </td>


                                                        <td>

                                                            {
                                                                candidate.name
                                                            }

                                                        </td>


                                                        <td>

                                                            {
                                                                candidate.email
                                                            }

                                                        </td>


                                                        <td>

                                                            <span
                                                                style={{
                                                                    padding:
                                                                        "4px 10px",
                                                                    borderRadius:
                                                                        "12px",
                                                                    fontSize:
                                                                        "0.8rem",
                                                                    background:
                                                                        candidate.status ===
                                                                        "ACTIVE"
                                                                            ? "#dcfce7"
                                                                            : "#fee2e2",
                                                                    color:
                                                                        candidate.status ===
                                                                        "ACTIVE"
                                                                            ? "#166534"
                                                                            : "#991b1b"
                                                                }}
                                                            >

                                                                {
                                                                    candidate.status ||
                                                                    "UNKNOWN"
                                                                }

                                                            </span>

                                                        </td>

                                                    </tr>

                                                )
                                            )}

                                        </tbody>

                                    </table>

                                </div>

                            )}

                        </div>

                    )}

                </div>

            </main>

        </div>
    );
}

export default AssessmentManagePage;