import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";

import assessmentAxiosInstance from "../../api/assessmentAxiosInstance";
import { useAuth } from "../../context/AuthContext";

function CandidateDashboard() {

    const navigate = useNavigate();
    const { user, logout } = useAuth();

    // ============================================================
    // STATE
    // ============================================================

    const [assessments, setAssessments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [refreshing, setRefreshing] = useState(false);
    const [error, setError] = useState("");

    const [search, setSearch] = useState("");
    const [filter, setFilter] = useState("ALL");

    // ============================================================
    // LOAD ASSESSMENTS
    // ============================================================

    const loadAssessments = async (isRefresh = false) => {

        try {

            if (isRefresh) {
                setRefreshing(true);
            } else {
                setLoading(true);
            }

            setError("");

            const response =
                await assessmentAxiosInstance.get(
                    "/assessment/all"
                );

            const data = response.data;

            console.log(
                "[Candidate Dashboard] Assessments:",
                data
            );

            if (Array.isArray(data)) {
                setAssessments(data);
            } else if (Array.isArray(data?.content)) {
                setAssessments(data.content);
            } else {
                setAssessments([]);
            }

        } catch (err) {

            console.error(
                "[Candidate Dashboard] Failed to load assessments:",
                err
            );

            const backendMessage =
                err?.response?.data?.message ||
                err?.response?.data?.error;

            setError(
                backendMessage ||
                err?.message ||
                "Unable to load assessments."
            );

        } finally {

            setLoading(false);
            setRefreshing(false);
        }
    };

    // ============================================================
    // INITIAL LOAD
    // ============================================================

    useEffect(() => {

        loadAssessments();

    }, []);

    // ============================================================
    // LOGOUT
    // ============================================================

    const handleLogout = async () => {

        try {

            if (typeof logout === "function") {
                await logout();
            }

        } catch (err) {

            console.error(
                "Logout error:",
                err
            );

        } finally {

            localStorage.removeItem("token");

            navigate(
                "/login",
                {
                    replace: true
                }
            );
        }
    };

    // ============================================================
    // START ASSESSMENT
    // ============================================================

    const handleStartAssessment = (assessmentId) => {

        if (!assessmentId) {
            console.error(
                "Assessment ID is missing."
            );
            return;
        }

        console.log(
            "[Candidate Dashboard] Starting assessment:",
            assessmentId
        );

        navigate(
            `/candidate/assessment/${assessmentId}`
        );
    };

    // ============================================================
    // FILTER + SEARCH
    // ============================================================

    const filteredAssessments = useMemo(() => {

        return assessments.filter(
            (assessment) => {

                // ------------------------------------------------
                // Published assessment check
                // ------------------------------------------------

                const published =
                    assessment.isPublished ??
                    assessment.published ??
                    assessment.is_published ??
                    true;

                if (
                    filter !== "ALL" &&
                    assessment.questionType !== filter &&
                    assessment.type !== filter &&
                    assessment.assessmentType !== filter
                ) {
                    return false;
                }

                // ------------------------------------------------
                // Search
                // ------------------------------------------------

                const title =
                    assessment.title ||
                    "";

                const description =
                    assessment.description ||
                    "";

                const searchText =
                    `${title} ${description}`
                        .toLowerCase();

                const matchesSearch =
                    searchText.includes(
                        search.toLowerCase()
                    );

                return (
                    Boolean(published) &&
                    matchesSearch
                );
            }
        );

    }, [
        assessments,
        search,
        filter
    ]);

    // ============================================================
    // COUNTS
    // ============================================================

    const totalAssessments =
        assessments.length;

    const availableAssessments =
        assessments.filter(
            (assessment) =>
                Boolean(
                    assessment.isPublished ??
                    assessment.published ??
                    assessment.is_published ??
                    true
                )
        ).length;

    // ============================================================
    // LOADING
    // ============================================================

    if (loading) {

        return (
            <div
                style={{
                    minHeight: "100vh",
                    background: "#f1f5f9",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    fontFamily:
                        "Arial, sans-serif"
                }}
            >

                <div
                    style={{
                        textAlign: "center"
                    }}
                >

                    <div
                        style={{
                            fontSize: "42px",
                            marginBottom: "15px"
                        }}
                    >
                        ⏳
                    </div>

                    <h2>
                        Loading Candidate Portal...
                    </h2>

                </div>

            </div>
        );
    }

    // ============================================================
    // MAIN DASHBOARD
    // ============================================================

    return (

        <div
            style={{
                minHeight: "100vh",
                background: "#f1f5f9",
                display: "flex",
                fontFamily:
                    "Arial, sans-serif",
                color: "#0f172a"
            }}
        >

            {/* ====================================================
                SIDEBAR
               ==================================================== */}

            <aside
                style={{
                    width: "270px",
                    background:
                        "linear-gradient(180deg,#0f1b3d,#172554)",
                    color: "#ffffff",
                    padding: "30px 20px",
                    boxSizing: "border-box",
                    display: "flex",
                    flexDirection: "column",
                    position: "fixed",
                    top: 0,
                    left: 0,
                    bottom: 0
                }}
            >

                {/* LOGO */}

                <div
                    style={{
                        display: "flex",
                        alignItems: "center",
                        gap: "13px",
                        marginBottom: "35px"
                    }}
                >

                    <div
                        style={{
                            width: "48px",
                            height: "48px",
                            borderRadius: "12px",
                            background:
                                "linear-gradient(135deg,#3b82f6,#60a5fa)",
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "center",
                            fontSize: "25px",
                            fontWeight: "800"
                        }}
                    >
                        S
                    </div>

                    <div>

                        <div
                            style={{
                                fontSize: "21px",
                                fontWeight: "800"
                            }}
                        >
                            SandBox
                        </div>

                        <div
                            style={{
                                fontSize: "11px",
                                color: "#93c5fd",
                                letterSpacing:
                                    "0.5px"
                            }}
                        >
                            SECURE ASSESSMENT
                        </div>

                    </div>

                </div>


                {/* DASHBOARD */}

                <div
                    style={{
                        background:
                            "rgba(59,130,246,0.22)",
                        borderRadius: "12px",
                        padding: "14px 16px",
                        display: "flex",
                        alignItems: "center",
                        gap: "12px",
                        fontWeight: "700",
                        cursor: "default"
                    }}
                >

                    <span>
                        ▦
                    </span>

                    Dashboard

                </div>


                {/* SPACER */}

                <div
                    style={{
                        flex: 1
                    }}
                />


                {/* USER */}

                <div
                    style={{
                        borderTop:
                            "1px solid rgba(255,255,255,0.12)",
                        paddingTop: "20px",
                        marginBottom: "15px"
                    }}
                >

                    <div
                        style={{
                            display: "flex",
                            alignItems: "center",
                            gap: "12px"
                        }}
                    >

                        <div
                            style={{
                                width: "40px",
                                height: "40px",
                                borderRadius: "50%",
                                background: "#2563eb",
                                display: "flex",
                                alignItems: "center",
                                justifyContent: "center",
                                fontWeight: "700",
                                fontSize: "18px"
                            }}
                        >
                            {(user?.name || "C")
                                .charAt(0)
                                .toUpperCase()}
                        </div>

                        <div>

                            <div
                                style={{
                                    fontWeight: "700",
                                    fontSize: "14px"
                                }}
                            >
                                {user?.name ||
                                    "Candidate"}
                            </div>

                            <div
                                style={{
                                    color: "#93c5fd",
                                    fontSize: "12px"
                                }}
                            >
                                Candidate #
                                {user?.userId ||
                                    user?.id ||
                                    "N/A"}
                            </div>

                        </div>

                    </div>

                </div>


                {/* LOGOUT */}

                <button
                    onClick={handleLogout}
                    style={{
                        width: "100%",
                        padding: "12px",
                        borderRadius: "9px",
                        border:
                            "1px solid rgba(255,255,255,0.18)",
                        background:
                            "rgba(255,255,255,0.05)",
                        color: "#ffffff",
                        fontSize: "15px",
                        fontWeight: "600",
                        cursor: "pointer"
                    }}
                >
                    ↪ &nbsp; Logout
                </button>

            </aside>


            {/* ====================================================
                MAIN CONTENT
               ==================================================== */}

            <main
                style={{
                    marginLeft: "270px",
                    width: "calc(100% - 270px)",
                    padding: "35px 40px",
                    boxSizing: "border-box"
                }}
            >

                {/* =================================================
                    TOP HEADER
                   ================================================= */}

                <div
                    style={{
                        display: "flex",
                        justifyContent:
                            "space-between",
                        alignItems: "center",
                        marginBottom: "25px"
                    }}
                >

                    <div>

                        <div
                            style={{
                                color: "#64748b",
                                fontSize: "14px",
                                marginBottom: "5px"
                            }}
                        >
                            Candidate Portal
                        </div>

                        <h1
                            style={{
                                margin: 0,
                                fontSize: "30px"
                            }}
                        >
                            Welcome back,{" "}
                            {user?.name ||
                                "Candidate"} 👋
                        </h1>

                    </div>


                    {/* REFRESH */}

                    <button
                        onClick={() =>
                            loadAssessments(true)
                        }
                        disabled={refreshing}
                        style={{
                            padding:
                                "11px 18px",
                            borderRadius: "10px",
                            border:
                                "1px solid #dbe3ef",
                            background: "#ffffff",
                            color: "#334155",
                            fontSize: "15px",
                            fontWeight: "600",
                            cursor:
                                refreshing
                                    ? "not-allowed"
                                    : "pointer",
                            opacity:
                                refreshing
                                    ? 0.7
                                    : 1
                        }}
                    >
                        ↻{" "}
                        {refreshing
                            ? "Refreshing..."
                            : "Refresh"}
                    </button>

                </div>


                {/* =================================================
                    HERO
                   ================================================= */}

                <div
                    style={{
                        background:
                            "linear-gradient(120deg,#1d4ed8,#3b82f6)",
                        borderRadius: "20px",
                        padding: "32px 35px",
                        color: "#ffffff",
                        marginBottom: "25px",
                        boxShadow:
                            "0 15px 30px rgba(37,99,235,0.2)",
                        position: "relative",
                        overflow: "hidden"
                    }}
                >

                    <div
                        style={{
                            position: "relative",
                            zIndex: 1
                        }}
                    >

                        <div
                            style={{
                                fontSize: "14px",
                                opacity: 0.9,
                                marginBottom: "8px"
                            }}
                        >
                            YOUR SECURE ASSESSMENT PORTAL
                        </div>

                        <h2
                            style={{
                                margin:
                                    "0 0 10px 0",
                                fontSize: "29px"
                            }}
                        >
                            Ready for your next
                            challenge?
                        </h2>

                        <p
                            style={{
                                margin: 0,
                                maxWidth: "700px",
                                lineHeight: "1.6",
                                fontSize: "16px"
                            }}
                        >
                            View your available
                            assessments and complete
                            your secure examinations
                            from one place.
                        </p>

                    </div>

                </div>


                {/* =================================================
                    STAT CARDS
                   ================================================= */}

                <div
                    style={{
                        display: "grid",
                        gridTemplateColumns:
                            "repeat(3, minmax(0,1fr))",
                        gap: "18px",
                        marginBottom: "35px"
                    }}
                >

                    {/* TOTAL */}

                    <div
                        style={{
                            background: "#ffffff",
                            borderRadius: "14px",
                            padding: "20px",
                            border:
                                "1px solid #e2e8f0",
                            display: "flex",
                            gap: "15px",
                            alignItems: "center"
                        }}
                    >

                        <div
                            style={{
                                width: "48px",
                                height: "48px",
                                borderRadius: "12px",
                                background: "#eff6ff",
                                color: "#2563eb",
                                display: "flex",
                                alignItems: "center",
                                justifyContent: "center",
                                fontSize: "22px"
                            }}
                        >
                            ▣
                        </div>

                        <div>

                            <div
                                style={{
                                    color: "#64748b",
                                    fontSize: "13px"
                                }}
                            >
                                Total Assessments
                            </div>

                            <div
                                style={{
                                    fontSize: "26px",
                                    fontWeight: "800"
                                }}
                            >
                                {totalAssessments}
                            </div>

                            <div
                                style={{
                                    color: "#94a3b8",
                                    fontSize: "12px"
                                }}
                            >
                                Available in system
                            </div>

                        </div>

                    </div>


                    {/* AVAILABLE */}

                    <div
                        style={{
                            background: "#ffffff",
                            borderRadius: "14px",
                            padding: "20px",
                            border:
                                "1px solid #e2e8f0",
                            display: "flex",
                            gap: "15px",
                            alignItems: "center"
                        }}
                    >

                        <div
                            style={{
                                width: "48px",
                                height: "48px",
                                borderRadius: "12px",
                                background: "#ecfdf5",
                                color: "#16a34a",
                                display: "flex",
                                alignItems: "center",
                                justifyContent: "center",
                                fontSize: "22px"
                            }}
                        >
                            ✓
                        </div>

                        <div>

                            <div
                                style={{
                                    color: "#64748b",
                                    fontSize: "13px"
                                }}
                            >
                                Available Now
                            </div>

                            <div
                                style={{
                                    fontSize: "26px",
                                    fontWeight: "800"
                                }}
                            >
                                {availableAssessments}
                            </div>

                            <div
                                style={{
                                    color: "#94a3b8",
                                    fontSize: "12px"
                                }}
                            >
                                Published assessments
                            </div>

                        </div>

                    </div>


                    {/* IN PROGRESS */}

                    <div
                        style={{
                            background: "#ffffff",
                            borderRadius: "14px",
                            padding: "20px",
                            border:
                                "1px solid #e2e8f0",
                            display: "flex",
                            gap: "15px",
                            alignItems: "center"
                        }}
                    >

                        <div
                            style={{
                                width: "48px",
                                height: "48px",
                                borderRadius: "12px",
                                background: "#fff7ed",
                                color: "#ea580c",
                                display: "flex",
                                alignItems: "center",
                                justifyContent: "center",
                                fontSize: "22px"
                            }}
                        >
                            ◷
                        </div>

                        <div>

                            <div
                                style={{
                                    color: "#64748b",
                                    fontSize: "13px"
                                }}
                            >
                                In Progress
                            </div>

                            <div
                                style={{
                                    fontSize: "26px",
                                    fontWeight: "800"
                                }}
                            >
                                0
                            </div>

                            <div
                                style={{
                                    color: "#94a3b8",
                                    fontSize: "12px"
                                }}
                            >
                                Active attempts
                            </div>

                        </div>

                    </div>

                </div>


                {/* =================================================
                    ASSESSMENTS HEADER
                   ================================================= */}

                <div
                    style={{
                        display: "flex",
                        justifyContent:
                            "space-between",
                        alignItems: "center",
                        marginBottom: "20px"
                    }}
                >

                    <div>

                        <h2
                            style={{
                                margin:
                                    "0 0 5px 0",
                                fontSize: "22px"
                            }}
                        >
                            Available Assessments
                        </h2>

                        <p
                            style={{
                                margin: 0,
                                color: "#64748b"
                            }}
                        >
                            Select an assessment to
                            begin your secure
                            examination.
                        </p>

                    </div>


                    {/* SEARCH */}

                    <input
                        type="text"
                        value={search}
                        onChange={(event) =>
                            setSearch(
                                event.target.value
                            )
                        }
                        placeholder="Search assessments..."
                        style={{
                            width: "260px",
                            padding:
                                "12px 15px",
                            borderRadius: "10px",
                            border:
                                "1px solid #dbe3ef",
                            outline: "none",
                            fontSize: "14px",
                            background: "#ffffff"
                        }}
                    />

                </div>


                {/* =================================================
                    FILTER BUTTONS
                   ================================================= */}

                <div
                    style={{
                        display: "flex",
                        gap: "10px",
                        marginBottom: "20px"
                    }}
                >

                    {[
                        ["ALL", "All"],
                        ["MCQ", "MCQ"],
                        ["CODING", "Coding"]
                    ].map(
                        ([value, label]) => (

                            <button
                                key={value}
                                onClick={() =>
                                    setFilter(value)
                                }
                                style={{
                                    padding:
                                        "9px 18px",
                                    borderRadius:
                                        "20px",
                                    border:
                                        filter === value
                                            ? "1px solid #2563eb"
                                            : "1px solid #dbe3ef",
                                    background:
                                        filter === value
                                            ? "#eff6ff"
                                            : "#ffffff",
                                    color:
                                        filter === value
                                            ? "#2563eb"
                                            : "#475569",
                                    fontWeight:
                                        filter === value
                                            ? "700"
                                            : "500",
                                    cursor:
                                        "pointer"
                                }}
                            >
                                {label}
                            </button>

                        )
                    )}

                </div>


                {/* =================================================
                    ERROR
                   ================================================= */}

                {error && (

                    <div
                        style={{
                            background: "#fee2e2",
                            border:
                                "1px solid #fecaca",
                            color: "#991b1b",
                            padding: "15px",
                            borderRadius: "10px",
                            marginBottom: "20px"
                        }}
                    >
                        <strong>
                            Unable to load assessments
                        </strong>

                        <div
                            style={{
                                marginTop: "5px"
                            }}
                        >
                            {error}
                        </div>
                    </div>

                )}


                {/* =================================================
                    ASSESSMENT CARDS
                   ================================================= */}

                {filteredAssessments.length === 0 ? (

                    <div
                        style={{
                            background: "#ffffff",
                            borderRadius: "14px",
                            padding: "50px",
                            textAlign: "center",
                            border:
                                "1px solid #e2e8f0"
                        }}
                    >

                        <div
                            style={{
                                fontSize: "45px",
                                marginBottom: "10px"
                            }}
                        >
                            📋
                        </div>

                        <h3>
                            No assessments found
                        </h3>

                        <p
                            style={{
                                color: "#64748b"
                            }}
                        >
                            Try another search or
                            filter.
                        </p>

                    </div>

                ) : (

                    <div
                        style={{
                            display: "grid",
                            gridTemplateColumns:
                                "repeat(2, minmax(0,1fr))",
                            gap: "20px"
                        }}
                    >

                        {filteredAssessments.map(
                            (assessment) => {

                                const assessmentId =
                                    assessment.id;

                                const title =
                                    assessment.title ||
                                    "Untitled Assessment";

                                const description =
                                    assessment.description ||
                                    "Technical assessment";

                                const duration =
                                    assessment.durationInMinutes ??
                                    assessment.duration ??
                                    0;

                                const passingMarks =
                                    assessment.passingMarks ??
                                    0;

                                return (

                                    <div
                                        key={
                                            assessmentId
                                        }
                                        style={{
                                            background:
                                                "#ffffff",
                                            borderRadius:
                                                "15px",
                                            padding:
                                                "24px",
                                            border:
                                                "1px solid #e2e8f0",
                                            boxShadow:
                                                "0 2px 8px rgba(0,0,0,0.04)"
                                        }}
                                    >

                                        {/* CARD TOP */}

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

                                            <div
                                                style={{
                                                    width:
                                                        "50px",
                                                    height:
                                                        "50px",
                                                    borderRadius:
                                                        "13px",
                                                    background:
                                                        "#eff6ff",
                                                    color:
                                                        "#2563eb",
                                                    display:
                                                        "flex",
                                                    alignItems:
                                                        "center",
                                                    justifyContent:
                                                        "center",
                                                    fontSize:
                                                        "22px"
                                                }}
                                            >
                                                ▣
                                            </div>

                                            <span
                                                style={{
                                                    background:
                                                        "#ecfdf5",
                                                    color:
                                                        "#15803d",
                                                    padding:
                                                        "6px 12px",
                                                    borderRadius:
                                                        "20px",
                                                    fontSize:
                                                        "12px",
                                                    fontWeight:
                                                        "700"
                                                }}
                                            >
                                                AVAILABLE
                                            </span>

                                        </div>


                                        {/* TITLE */}

                                        <h3
                                            style={{
                                                fontSize:
                                                    "20px",
                                                margin:
                                                    "0 0 10px 0"
                                            }}
                                        >
                                            {title}
                                        </h3>


                                        {/* DESCRIPTION */}

                                        <p
                                            style={{
                                                color:
                                                    "#64748b",
                                                lineHeight:
                                                    "1.5",
                                                minHeight:
                                                    "45px",
                                                marginBottom:
                                                    "18px"
                                            }}
                                        >
                                            {description}
                                        </p>


                                        {/* DETAILS */}

                                        <div
                                            style={{
                                                display:
                                                    "flex",
                                                gap:
                                                    "20px",
                                                padding:
                                                    "14px 0",
                                                borderTop:
                                                    "1px solid #f1f5f9",
                                                borderBottom:
                                                    "1px solid #f1f5f9",
                                                color:
                                                    "#475569",
                                                fontSize:
                                                    "13px",
                                                marginBottom:
                                                    "18px"
                                            }}
                                        >

                                            <span>
                                                ⏱ {duration}
                                                {" "}min
                                            </span>

                                            <span>
                                                🎯 Passing:
                                                {" "}
                                                {passingMarks}
                                            </span>

                                        </div>


                                        {/* START BUTTON */}

                                        <button
                                            onClick={() =>
                                                handleStartAssessment(
                                                    assessmentId
                                                )
                                            }
                                            disabled={
                                                !assessmentId
                                            }
                                            style={{
                                                width:
                                                    "100%",
                                                padding:
                                                    "12px",
                                                border:
                                                    "none",
                                                borderRadius:
                                                    "9px",
                                                background:
                                                    !assessmentId
                                                        ? "#94a3b8"
                                                        : "#2563eb",
                                                color:
                                                    "#ffffff",
                                                fontSize:
                                                    "15px",
                                                fontWeight:
                                                    "700",
                                                cursor:
                                                    !assessmentId
                                                        ? "not-allowed"
                                                        : "pointer"
                                            }}
                                        >
                                            Start Assessment →
                                        </button>

                                    </div>
                                );
                            }
                        )}

                    </div>

                )}

            </main>

        </div>
    );
}

export default CandidateDashboard;