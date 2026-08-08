import { useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import { useProctoring } from "../../hooks/useProctoring";
import { WarningModal } from "../../components/proctoring/WarningModal";
import { isMobileDevice } from "../../utils/deviceCheck";

import { useAuth } from "../../context/AuthContext";
import assessmentAxiosInstance from "../../api/assessmentAxiosInstance";

export const TestProctoringView = () => {

    // ============================================================
    // ROUTER + AUTH
    // ============================================================

    const { assessmentId } = useParams();
    const navigate = useNavigate();
    const { user } = useAuth();

    // ============================================================
    // PROCTORING
    // ============================================================

    const {
        warning,
        closeWarning,
        webcamStream,
        screenStream,
        requestMediaStreams,
        enterFullscreen
    } = useProctoring(
        user?.userId
            ? String(user.userId)
            : "UNKNOWN_CANDIDATE",

        assessmentId
            ? String(assessmentId)
            : "UNKNOWN_EXAM",

        null
    );

    // ============================================================
    // REFS
    // ============================================================

    const webcamVideoRef = useRef(null);

    // ============================================================
    // STATE
    // ============================================================

    const [isMobile, setIsMobile] = useState(() =>
        isMobileDevice()
    );

    const [starting, setStarting] = useState(false);

    const [error, setError] = useState("");

    // ============================================================
    // MOBILE / TABLET DETECTION
    // ============================================================

    useEffect(() => {

        const handleResize = () => {
            setIsMobile(isMobileDevice());
        };

        window.addEventListener(
            "resize",
            handleResize
        );

        return () => {
            window.removeEventListener(
                "resize",
                handleResize
            );
        };

    }, []);

    // ============================================================
    // ATTACH WEBCAM STREAM
    // ============================================================

    useEffect(() => {

        if (
            webcamVideoRef.current &&
            webcamStream
        ) {

            webcamVideoRef.current.srcObject =
                webcamStream;
        }

    }, [webcamStream]);

    // ============================================================
    // START SECURE ASSESSMENT
    // ============================================================

    const handleStartAssessment = async () => {

        if (starting) {
            return;
        }

        setError("");
        setStarting(true);

        try {

            // ----------------------------------------------------
            // STEP 1
            // Validate candidate
            // ----------------------------------------------------

            if (!user?.userId) {

                throw new Error(
                    "Candidate information is not available. Please login again."
                );

            }

            // ----------------------------------------------------
            // STEP 2
            // Validate assessment ID
            // ----------------------------------------------------

            if (!assessmentId) {

                throw new Error(
                    "Assessment ID is missing."
                );

            }

            console.log(
                "[Assessment] Starting assessment:",
                assessmentId
            );

            console.log(
                "[Assessment] Candidate:",
                user.userId
            );

            // ----------------------------------------------------
            // STEP 3
            // Request webcam + entire screen
            // ----------------------------------------------------

            const granted =
                await requestMediaStreams();

            if (!granted) {

                throw new Error(
                    "Webcam and entire screen access are required to start the assessment."
                );

            }

            // ----------------------------------------------------
            // STEP 4
            // Enter fullscreen
            // ----------------------------------------------------

            await enterFullscreen();

            // ----------------------------------------------------
            // STEP 5
            // Create Assessment Submission
            //
            // Candidate ID is NOT sent manually.
            //
            // Backend gets candidate ID from JWT.
            // ----------------------------------------------------

            console.log(
                "[Assessment] Creating submission..."
            );

            const response =
                await assessmentAxiosInstance.post(
                    `/assessment-submission/start/${assessmentId}`
                );

            const submission =
                response.data;

            console.log(
                "[Assessment] Submission created:",
                submission
            );

            // ----------------------------------------------------
            // STEP 6
            // Validate backend response
            //
            // IMPORTANT:
            //
            // Backend StartAssessmentResponse contains:
            //
            // submissionId
            // assessmentId
            // candidateId
            // status
            // startedAt
            //
            // It does NOT contain "id".
            // ----------------------------------------------------

            if (!submission) {

                throw new Error(
                    "Assessment submission was not created."
                );

            }

            if (!submission.submissionId) {

                throw new Error(
                    "Submission ID was not returned by the server."
                );

            }

            console.log(
                "[Assessment] Submission ID:",
                submission.submissionId
            );

            console.log(
                "[Assessment] Assessment ID:",
                submission.assessmentId
            );

            console.log(
                "[Assessment] Candidate ID:",
                submission.candidateId
            );

            console.log(
                "[Assessment] Status:",
                submission.status
            );

            console.log(
                "[Assessment] Started At:",
                submission.startedAt
            );

            // ----------------------------------------------------
            // STEP 7
            // Navigate to actual exam
            // ----------------------------------------------------

            navigate(
                `/candidate/exam/${assessmentId}`,
                {
                    state: {
                        submission
                    }
                }
            );

        } catch (err) {

            console.error(
                "[Assessment] Failed to start assessment:",
                err
            );

            // ----------------------------------------------------
            // Extract backend / Axios error
            // ----------------------------------------------------

            const backendMessage =
                err?.response?.data?.message ||
                err?.response?.data?.error;

            const message =
                backendMessage ||
                err?.message ||
                "Unable to start the assessment.";

            setError(message);

        } finally {

            setStarting(false);

        }
    };

    // ============================================================
    // MOBILE RESTRICTION
    // ============================================================

    if (isMobile) {

        return (
            <div
                style={{
                    height: "100vh",
                    width: "100vw",
                    backgroundColor: "#0f172a",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    color: "#ffffff",
                    fontFamily:
                        "system-ui, sans-serif",
                    padding: "1.5rem",
                    textAlign: "center",
                    boxSizing: "border-box"
                }}
            >

                <div
                    style={{
                        backgroundColor: "#1e293b",
                        padding: "2.5rem",
                        borderRadius: "16px",
                        maxWidth: "480px",
                        width: "100%",
                        boxShadow:
                            "0 20px 25px -5px rgba(0,0,0,0.5)",
                        border:
                            "1px solid #334155"
                    }}
                >

                    <div
                        style={{
                            fontSize: "3.5rem",
                            marginBottom: "1rem"
                        }}
                    >
                        💻
                    </div>

                    <h2
                        style={{
                            color: "#f8fafc",
                            marginBottom: "0.75rem",
                            fontSize: "1.5rem"
                        }}
                    >
                        Desktop Required
                    </h2>

                    <p
                        style={{
                            color: "#94a3b8",
                            lineHeight: "1.6",
                            fontSize: "1rem",
                            margin: 0
                        }}
                    >
                        Online assessments cannot be taken
                        on mobile phones or tablets.
                        Please open this assessment on a
                        desktop computer using Chrome or Edge.
                    </p>

                </div>

            </div>
        );
    }

    // ============================================================
    // MAIN PROCTORING PAGE
    // ============================================================

    return (
        <div
            style={{
                minHeight: "100vh",
                backgroundColor: "#f8fafc",
                padding: "2rem",
                fontFamily:
                    "system-ui, -apple-system, sans-serif",
                color: "#0f172a"
            }}
        >

            <div
                style={{
                    maxWidth: "850px",
                    margin: "0 auto"
                }}
            >

                {/* ==================================================
                    HEADER
                   ================================================== */}

                <header
                    style={{
                        backgroundColor: "#ffffff",
                        padding: "1.5rem 2rem",
                        borderRadius: "12px",
                        border:
                            "1px solid #e2e8f0",
                        boxShadow:
                            "0 1px 3px rgba(0,0,0,0.05)",
                        marginBottom: "1.5rem"
                    }}
                >

                    <h1
                        style={{
                            color: "#1e3a8a",
                            fontSize: "1.75rem",
                            margin:
                                "0 0 0.5rem 0"
                        }}
                    >
                        SandBox Secure Assessment
                    </h1>

                    <p
                        style={{
                            color: "#64748b",
                            fontSize: "0.975rem",
                            margin: 0
                        }}
                    >

                        Candidate:{" "}
                        <strong>
                            {user?.name || "Candidate"}
                        </strong>

                        {" | "}

                        Candidate ID:{" "}
                        <strong>
                            {user?.userId || "N/A"}
                        </strong>

                        {" | "}

                        Exam:{" "}
                        <strong>
                            {assessmentId || "N/A"}
                        </strong>

                    </p>

                </header>

                {/* ==================================================
                    ERROR
                   ================================================== */}

                {error && (

                    <div
                        style={{
                            backgroundColor: "#fee2e2",
                            color: "#991b1b",
                            border:
                                "1px solid #fecaca",
                            padding: "15px",
                            borderRadius: "8px",
                            marginBottom: "20px",
                            lineHeight: "1.5"
                        }}
                    >

                        <strong>
                            Unable to start assessment
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

                {/* ==================================================
                    PRE-EXAM SCREEN
                   ================================================== */}

                <section
                    style={{
                        backgroundColor: "#ffffff",
                        padding: "2rem",
                        borderRadius: "12px",
                        border:
                            "1px solid #e2e8f0",
                        boxShadow:
                            "0 1px 3px rgba(0,0,0,0.05)"
                    }}
                >

                    <h2
                        style={{
                            marginTop: 0,
                            color: "#1e293b"
                        }}
                    >
                        Ready to Start?
                    </h2>

                    <p
                        style={{
                            color: "#475569",
                            lineHeight: "1.6"
                        }}
                    >
                        Before starting the assessment,
                        make sure you understand the following
                        security requirements.
                    </p>

                    <ul
                        style={{
                            color: "#475569",
                            lineHeight: "1.9"
                        }}
                    >

                        <li>
                            Allow webcam access
                        </li>

                        <li>
                            Allow entire screen sharing
                        </li>

                        <li>
                            Enter fullscreen mode
                        </li>

                        <li>
                            Keep your face visible
                        </li>

                        <li>
                            Do not switch tabs
                        </li>

                        <li>
                            Do not switch applications
                        </li>

                        <li>
                            Do not exit fullscreen
                        </li>

                        <li>
                            Copy/paste and context menu
                            actions are restricted
                        </li>

                    </ul>

                    {/* ==================================================
                        REQUIREMENT STATUS
                       ================================================== */}

                    <div
                        style={{
                            display: "grid",
                            gridTemplateColumns:
                                "repeat(2, minmax(0, 1fr))",
                            gap: "12px",
                            marginTop: "20px",
                            marginBottom: "25px"
                        }}
                    >

                        {/* WEBCAM */}

                        <div
                            style={{
                                padding: "14px",
                                background:
                                    "#f8fafc",
                                borderRadius: "8px",
                                border:
                                    "1px solid #e2e8f0"
                            }}
                        >

                            <strong>
                                Webcam
                            </strong>

                            <div
                                style={{
                                    marginTop: "5px",
                                    color:
                                        webcamStream
                                            ? "#15803d"
                                            : "#64748b"
                                }}
                            >
                                {webcamStream
                                    ? "✓ Active"
                                    : "Not started"}
                            </div>

                        </div>

                        {/* SCREEN SHARE */}

                        <div
                            style={{
                                padding: "14px",
                                background:
                                    "#f8fafc",
                                borderRadius: "8px",
                                border:
                                    "1px solid #e2e8f0"
                            }}
                        >

                            <strong>
                                Screen Share
                            </strong>

                            <div
                                style={{
                                    marginTop: "5px",
                                    color:
                                        screenStream
                                            ? "#15803d"
                                            : "#64748b"
                                }}
                            >
                                {screenStream
                                    ? "✓ Active"
                                    : "Not started"}
                            </div>

                        </div>

                    </div>

                    {/* ==================================================
                        START BUTTON
                       ================================================== */}

                    <button
                        onClick={
                            handleStartAssessment
                        }
                        disabled={starting}
                        style={{
                            width: "100%",
                            backgroundColor:
                                starting
                                    ? "#64748b"
                                    : "#2563eb",
                            color: "#ffffff",
                            padding:
                                "0.9rem 1.5rem",
                            borderRadius: "8px",
                            border: "none",
                            fontWeight: "600",
                            fontSize: "1rem",
                            cursor:
                                starting
                                    ? "not-allowed"
                                    : "pointer",
                            transition:
                                "background-color 0.2s"
                        }}
                    >

                        {starting
                            ? "Starting Secure Assessment..."
                            : "Start Secure Assessment"}

                    </button>

                </section>

                {/* ==================================================
                    WEBCAM PREVIEW
                   ================================================== */}

                {webcamStream && (

                    <div
                        style={{
                            position: "fixed",
                            bottom: "24px",
                            right: "24px",
                            width: "200px",
                            height: "150px",
                            borderRadius: "12px",
                            overflow: "hidden",
                            boxShadow:
                                "0 10px 25px -5px rgba(0,0,0,0.3)",
                            border:
                                "3px solid #2563eb",
                            backgroundColor: "#000000",
                            zIndex: 9000,
                            pointerEvents: "none"
                        }}
                    >

                        <video
                            ref={webcamVideoRef}
                            autoPlay
                            playsInline
                            muted
                            disablePictureInPicture
                            controlsList="nodownload noplaybackrate pictureinpicture"
                            style={{
                                width: "100%",
                                height: "100%",
                                objectFit: "cover",
                                transform:
                                    "scaleX(-1)"
                            }}
                        />

                        <div
                            style={{
                                position: "absolute",
                                top: "6px",
                                left: "8px",
                                backgroundColor:
                                    "rgba(37,99,235,0.85)",
                                color: "#ffffff",
                                fontSize: "0.65rem",
                                fontWeight: "700",
                                padding: "2px 6px",
                                borderRadius: "4px",
                                letterSpacing:
                                    "0.5px"
                            }}
                        >
                            LIVE CAM
                        </div>

                    </div>

                )}

                {/* ==================================================
                    WARNING MODAL
                   ================================================== */}

                <WarningModal
                    isOpen={warning.isOpen}
                    warningText={warning.text}
                    onClose={closeWarning}
                />

            </div>

        </div>
    );
};

export default TestProctoringView;