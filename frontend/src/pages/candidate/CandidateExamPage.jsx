import { useEffect, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";

import assessmentAxiosInstance from "../../api/assessmentAxiosInstance";
import { useAuth } from "../../context/AuthContext";

function CandidateExamPage() {

    const { examId } = useParams();
    const location = useLocation();
    const navigate = useNavigate();
    const { user } = useAuth();

    // ============================================================
    // SUBMISSION
    // ============================================================

    const submissionFromState =
        location.state?.submission || null;

    const [submission, setSubmission] =
        useState(submissionFromState);

    // ============================================================
    // ASSESSMENT
    // ============================================================

    const [assessment, setAssessment] =
        useState(null);

    const [questions, setQuestions] =
        useState([]);

    // ============================================================
    // ANSWERS
    // ============================================================

    /*
     * Structure:
     *
     * {
     *     questionId: selectedOptionId
     * }
     *
     * For coding questions:
     *
     * {
     *     questionId: "java source code"
     * }
     */
    const [answers, setAnswers] =
        useState({});

    // ============================================================
    // UI STATE
    // ============================================================

    const [loading, setLoading] =
        useState(true);

    const [error, setError] =
        useState("");

    const [currentQuestion, setCurrentQuestion] =
        useState(0);

    const [submitting, setSubmitting] =
        useState(false);

    const [submitError, setSubmitError] =
        useState("");

    const [result, setResult] =
        useState(null);

    // ============================================================
    // LOAD ASSESSMENT
    // ============================================================

    useEffect(() => {

        const loadExam = async () => {

            try {

                setLoading(true);
                setError("");

                if (!examId) {

                    throw new Error(
                        "Assessment ID is missing."
                    );
                }

                // ------------------------------------------------
                // GET ASSESSMENT
                // ------------------------------------------------

                const assessmentResponse =
                    await assessmentAxiosInstance.get(
                        `/assessment/${examId}`
                    );

                const assessmentData =
                    assessmentResponse.data;

                setAssessment(
                    assessmentData
                );

                // ------------------------------------------------
                // GET QUESTION IDS
                // ------------------------------------------------

                const questionIds =
                    assessmentData.questionIds || [];

                if (questionIds.length === 0) {

                    setQuestions([]);

                    return;
                }

                // ------------------------------------------------
                // GET QUESTIONS
                // ------------------------------------------------

                const questionResponses =
                    await Promise.all(
                        questionIds.map(
                            async (questionId) => {

                                try {

                                    const response =
                                        await assessmentAxiosInstance.get(
                                            `/question/${questionId}`
                                        );

                                    return response.data;

                                } catch (questionError) {

                                    console.error(
                                        "Failed to load question:",
                                        questionId,
                                        questionError
                                    );

                                    return null;
                                }
                            }
                        )
                    );

                setQuestions(
                    questionResponses.filter(
                        question =>
                            question !== null
                    )
                );

            } catch (err) {

                console.error(
                    "Failed to load examination:",
                    err
                );

                const backendMessage =
                    err?.response?.data?.message ||
                    err?.response?.data?.error;

                setError(
                    backendMessage ||
                    err?.message ||
                    "Unable to load assessment."
                );

            } finally {

                setLoading(false);
            }
        };

        loadExam();

    }, [examId]);


    // ============================================================
    // HANDLE ANSWER
    // ============================================================

    const handleAnswer = (
        questionId,
        value
    ) => {

        setAnswers(
            previous => ({
                ...previous,
                [questionId]: value
            })
        );

        setSubmitError("");
    };


    // ============================================================
    // NEXT QUESTION
    // ============================================================

    const handleNext = () => {

        if (
            currentQuestion <
            questions.length - 1
        ) {

            setCurrentQuestion(
                previous => previous + 1
            );

            window.scrollTo({
                top: 0,
                behavior: "smooth"
            });
        }
    };


    // ============================================================
    // PREVIOUS QUESTION
    // ============================================================

    const handlePrevious = () => {

        if (currentQuestion > 0) {

            setCurrentQuestion(
                previous => previous - 1
            );

            window.scrollTo({
                top: 0,
                behavior: "smooth"
            });
        }
    };


    // ============================================================
    // SAVE MCQ ANSWER
    // ============================================================

    const saveMcqAnswer = async (
        question
    ) => {

        const selectedOptionId =
            answers[question.id];

        /*
         * No answer selected.
         *
         * We simply skip unanswered questions.
         */

        if (
            selectedOptionId === undefined ||
            selectedOptionId === null
        ) {

            return null;
        }

        /*
         * Candidate submission is mandatory.
         */

        if (!submission?.submissionId) {

            throw new Error(
                "Submission ID is missing. Please restart the assessment."
            );
        }

        const response =
            await assessmentAxiosInstance.post(
                "/candidate-answer/mcq",
                {
                    submissionId:
                        submission.submissionId,

                    questionId:
                        question.id,

                    selectedOptionId:
                        selectedOptionId
                }
            );

        return response.data;
    };


    // ============================================================
    // SUBMIT ASSESSMENT
    // ============================================================

    const handleSubmitAssessment =
        async () => {

            if (submitting) {
                return;
            }

            setSubmitError("");
            setSubmitting(true);

            try {

                // ------------------------------------------------
                // VALIDATE SUBMISSION
                // ------------------------------------------------

                if (!submission) {

                    throw new Error(
                        "Assessment submission information is missing."
                    );
                }

                if (!submission.submissionId) {

                    throw new Error(
                        "Submission ID is missing."
                    );
                }

                console.log(
                    "[Assessment] Submitting assessment..."
                );

                console.log(
                    "[Assessment] Submission ID:",
                    submission.submissionId
                );

                // ------------------------------------------------
                // SAVE MCQ ANSWERS
                // ------------------------------------------------

                for (const question of questions) {

                    if (
                        question.questionType ===
                        "MCQ"
                    ) {

                        const selectedOptionId =
                            answers[question.id];

                        if (
                            selectedOptionId !==
                                undefined &&
                            selectedOptionId !== null
                        ) {

                            console.log(
                                "[Assessment] Saving MCQ answer:",
                                {
                                    questionId:
                                        question.id,

                                    selectedOptionId
                                }
                            );

                            await saveMcqAnswer(
                                question
                            );
                        }
                    }
                }

                // ------------------------------------------------
                // CHECK CODING ANSWERS
                // ------------------------------------------------

                const codingQuestions =
                    questions.filter(
                        question =>
                            question.questionType ===
                            "CODING"
                    );

                const answeredCodingQuestions =
                    codingQuestions.filter(
                        question => {

                            const code =
                                answers[
                                    question.id
                                ];

                            return (
                                typeof code ===
                                    "string" &&
                                code.trim().length > 0
                            );
                        }
                    );

                /*
                 * IMPORTANT:
                 *
                 * The current backend CandidateAnswer API
                 * requires codingEvaluationId for coding answers.
                 *
                 * Raw source code cannot be sent directly to
                 * /candidate-answer/coding.
                 *
                 * Therefore we keep this information visible
                 * in the console for now.
                 *
                 * Once the 8083 CodeEvaluation flow returns a
                 * codingEvaluationId, it can be saved here.
                 */

                if (
                    answeredCodingQuestions.length >
                    0
                ) {

                    console.warn(
                        "[Assessment] Coding answers entered but codingEvaluationId is not available yet.",
                        answeredCodingQuestions.map(
                            question => ({
                                questionId:
                                    question.id,

                                title:
                                    question.title
                            })
                        )
                    );
                }

                // ------------------------------------------------
                // FINISH ASSESSMENT
                // ------------------------------------------------

                console.log(
                    "[Assessment] Finishing submission:",
                    submission.submissionId
                );

                const finishResponse =
                    await assessmentAxiosInstance.post(
                        `/assessment-submission/finish/${submission.submissionId}`
                    );

                const finishData =
                    finishResponse.data;

                console.log(
                    "[Assessment] Assessment finished:",
                    finishData
                );

                // ------------------------------------------------
                // SAVE RESULT
                // ------------------------------------------------

                setResult(
                    finishData
                );

            } catch (err) {

                console.error(
                    "[Assessment] Failed to submit assessment:",
                    err
                );

                const backendMessage =
                    err?.response?.data?.message ||
                    err?.response?.data?.error;

                setSubmitError(
                    backendMessage ||
                    err?.message ||
                    "Unable to submit assessment."
                );

            } finally {

                setSubmitting(false);
            }
        };


    // ============================================================
    // LOADING
    // ============================================================

    if (loading) {

        return (
            <div
                style={{
                    minHeight: "100vh",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    background: "#f8fafc",
                    fontFamily: "Arial, sans-serif"
                }}
            >

                <h2>
                    Loading assessment...
                </h2>

            </div>
        );
    }


    // ============================================================
    // ERROR
    // ============================================================

    if (error) {

        return (
            <div
                style={{
                    minHeight: "100vh",
                    background: "#f8fafc",
                    padding: "40px",
                    fontFamily: "Arial, sans-serif"
                }}
            >

                <div
                    style={{
                        maxWidth: "900px",
                        margin: "0 auto",
                        background: "#ffffff",
                        padding: "30px",
                        borderRadius: "12px",
                        border: "1px solid #fecaca"
                    }}
                >

                    <h2
                        style={{
                            color: "#991b1b"
                        }}
                    >
                        Unable to load assessment
                    </h2>

                    <p
                        style={{
                            color: "#b91c1c"
                        }}
                    >
                        {error}
                    </p>

                    <button
                        onClick={() =>
                            navigate("/candidate")
                        }
                        style={{
                            background: "#2563eb",
                            color: "#ffffff",
                            border: "none",
                            padding: "10px 18px",
                            borderRadius: "8px",
                            cursor: "pointer"
                        }}
                    >
                        Back to Dashboard
                    </button>

                </div>

            </div>
        );
    }


    // ============================================================
    // RESULT SCREEN
    // ============================================================

    if (result) {

        const score =
            result.score ?? 0;

        const passingMarks =
            assessment?.passingMarks ?? 0;

        const passed =
            Number(score) >=
            Number(passingMarks);

        return (
            <div
                style={{
                    minHeight: "100vh",
                    background: "#f1f5f9",
                    padding: "40px",
                    fontFamily:
                        "Arial, sans-serif",
                    color: "#0f172a"
                }}
            >

                <div
                    style={{
                        maxWidth: "700px",
                        margin: "60px auto",
                        background: "#ffffff",
                        padding: "40px",
                        borderRadius: "16px",
                        boxShadow:
                            "0 4px 15px rgba(0,0,0,0.08)",
                        textAlign: "center"
                    }}
                >

                    <div
                        style={{
                            fontSize: "48px",
                            marginBottom: "15px"
                        }}
                    >
                        {passed ? "✓" : "!"}
                    </div>

                    <h1
                        style={{
                            color:
                                passed
                                    ? "#15803d"
                                    : "#b91c1c",
                            marginBottom: "10px"
                        }}
                    >
                        Assessment Submitted
                    </h1>

                    <p
                        style={{
                            color: "#64748b"
                        }}
                    >
                        {assessment?.title ||
                            "Assessment"}
                    </p>

                    <div
                        style={{
                            marginTop: "30px",
                            padding: "25px",
                            background:
                                "#f8fafc",
                            borderRadius: "12px"
                        }}
                    >

                        <div
                            style={{
                                color: "#64748b",
                                fontSize: "14px"
                            }}
                        >
                            Your Score
                        </div>

                        <div
                            style={{
                                fontSize: "42px",
                                fontWeight: "700",
                                marginTop: "5px"
                            }}
                        >
                            {score}
                        </div>

                        <div
                            style={{
                                color: "#64748b",
                                marginTop: "5px"
                            }}
                        >
                            Passing Marks:{" "}
                            {passingMarks}
                        </div>

                    </div>

                    <div
                        style={{
                            marginTop: "20px",
                            padding: "12px",
                            borderRadius: "8px",
                            background:
                                passed
                                    ? "#dcfce7"
                                    : "#fee2e2",
                            color:
                                passed
                                    ? "#166534"
                                    : "#991b1b",
                            fontWeight: "600"
                        }}
                    >
                        {passed
                            ? "PASSED"
                            : "NOT PASSED"}
                    </div>

                    <p
                        style={{
                            marginTop: "20px",
                            color: "#64748b",
                            fontSize: "14px"
                        }}
                    >
                        Submission ID:{" "}
                        {result.submissionId}
                    </p>

                    <button
                        onClick={() =>
                            navigate("/candidate")
                        }
                        style={{
                            marginTop: "25px",
                            background:
                                "#2563eb",
                            color:
                                "#ffffff",
                            border: "none",
                            padding:
                                "12px 24px",
                            borderRadius:
                                "8px",
                            fontWeight:
                                "600",
                            cursor:
                                "pointer"
                        }}
                    >
                        Back to Dashboard
                    </button>

                </div>

            </div>
        );
    }


    // ============================================================
    // CURRENT QUESTION
    // ============================================================

    const question =
        questions[currentQuestion];


    // ============================================================
    // MAIN EXAM PAGE
    // ============================================================

    return (

        <div
            style={{
                minHeight: "100vh",
                background: "#f1f5f9",
                padding: "30px",
                fontFamily:
                    "Arial, sans-serif",
                color: "#0f172a"
            }}
        >

            <div
                style={{
                    maxWidth: "1100px",
                    margin: "0 auto"
                }}
            >

                {/* ==================================================
                    HEADER
                   ================================================== */}

                <div
                    style={{
                        background: "#ffffff",
                        padding: "25px",
                        borderRadius: "12px",
                        marginBottom: "20px",
                        boxShadow:
                            "0 2px 8px rgba(0,0,0,0.08)"
                    }}
                >

                    <h1
                        style={{
                            margin: 0,
                            color: "#1e3a8a"
                        }}
                    >
                        {assessment?.title ||
                            "Secure Assessment"}
                    </h1>

                    <p
                        style={{
                            color: "#64748b",
                            marginBottom: 0
                        }}
                    >
                        Candidate:{" "}
                        <strong>
                            {user?.name ||
                                "Candidate"}
                        </strong>

                        {" | "}

                        Candidate ID:{" "}
                        <strong>
                            {user?.userId ||
                                submission?.candidateId ||
                                "N/A"}
                        </strong>

                        {" | "}

                        Assessment ID:{" "}
                        <strong>
                            {examId}
                        </strong>

                    </p>

                </div>


                {/* ==================================================
                    ASSESSMENT INFORMATION
                   ================================================== */}

                <div
                    style={{
                        display: "grid",
                        gridTemplateColumns:
                            "repeat(3, 1fr)",
                        gap: "15px",
                        marginBottom: "20px"
                    }}
                >

                    <div
                        style={{
                            background: "#ffffff",
                            padding: "18px",
                            borderRadius: "10px"
                        }}
                    >

                        <strong>
                            Duration
                        </strong>

                        <div
                            style={{
                                marginTop: "5px",
                                color: "#475569"
                            }}
                        >
                            {assessment?.durationInMinutes ||
                                0} minutes
                        </div>

                    </div>


                    <div
                        style={{
                            background: "#ffffff",
                            padding: "18px",
                            borderRadius: "10px"
                        }}
                    >

                        <strong>
                            Passing Marks
                        </strong>

                        <div
                            style={{
                                marginTop: "5px",
                                color: "#475569"
                            }}
                        >
                            {assessment?.passingMarks ??
                                0}
                        </div>

                    </div>


                    <div
                        style={{
                            background: "#ffffff",
                            padding: "18px",
                            borderRadius: "10px"
                        }}
                    >

                        <strong>
                            Questions
                        </strong>

                        <div
                            style={{
                                marginTop: "5px",
                                color: "#475569"
                            }}
                        >
                            {questions.length}
                        </div>

                    </div>

                </div>


                {/* ==================================================
                    SUBMIT ERROR
                   ================================================== */}

                {submitError && (

                    <div
                        style={{
                            background:
                                "#fee2e2",
                            color:
                                "#991b1b",
                            border:
                                "1px solid #fecaca",
                            padding:
                                "15px",
                            borderRadius:
                                "8px",
                            marginBottom:
                                "20px",
                            lineHeight:
                                "1.5"
                        }}
                    >

                        <strong>
                            Unable to submit assessment
                        </strong>

                        <div
                            style={{
                                marginTop: "5px"
                            }}
                        >
                            {submitError}
                        </div>

                    </div>

                )}


                {/* ==================================================
                    NO QUESTIONS
                   ================================================== */}

                {questions.length === 0 && (

                    <div
                        style={{
                            background: "#ffffff",
                            padding: "40px",
                            borderRadius: "12px",
                            textAlign: "center"
                        }}
                    >

                        <h2>
                            No questions available
                        </h2>

                        <p
                            style={{
                                color: "#64748b"
                            }}
                        >
                            This assessment does not
                            contain any questions yet.
                        </p>

                    </div>

                )}


                {/* ==================================================
                    QUESTION
                   ================================================== */}

                {questions.length > 0 &&
                    question && (

                    <div
                        style={{
                            background: "#ffffff",
                            padding: "30px",
                            borderRadius: "12px",
                            boxShadow:
                                "0 2px 8px rgba(0,0,0,0.08)"
                        }}
                    >

                        {/* QUESTION HEADER */}

                        <div
                            style={{
                                display: "flex",
                                justifyContent:
                                    "space-between",
                                alignItems:
                                    "center",
                                marginBottom:
                                    "25px"
                            }}
                        >

                            <span
                                style={{
                                    fontWeight:
                                        "600",
                                    color:
                                        "#475569"
                                }}
                            >
                                Question{" "}
                                {currentQuestion + 1}
                                {" "}of{" "}
                                {questions.length}
                            </span>


                            <span
                                style={{
                                    background:
                                        "#dbeafe",
                                    color:
                                        "#1d4ed8",
                                    padding:
                                        "6px 12px",
                                    borderRadius:
                                        "20px",
                                    fontSize:
                                        "13px",
                                    fontWeight:
                                        "600"
                                }}
                            >
                                {question.questionType ||
                                    "QUESTION"}
                            </span>

                        </div>


                        {/* QUESTION TEXT */}

                        <h2
                            style={{
                                fontSize: "20px",
                                lineHeight: "1.6",
                                marginBottom:
                                    "25px"
                            }}
                        >
                            {question.title}
                        </h2>


                        {/* MARKS */}

                        <p
                            style={{
                                color:
                                    "#64748b"
                            }}
                        >

                            Marks:{" "}

                            <strong>
                                {question.marks}
                            </strong>

                            {question.difficulty && (
                                <>
                                    {" | "}
                                    Difficulty:{" "}

                                    <strong>
                                        {question.difficulty}
                                    </strong>
                                </>
                            )}

                        </p>


                        {/* ==================================================
                            MCQ OPTIONS
                           ================================================== */}

                        {question.questionType ===
                            "MCQ" && (

                            <div
                                style={{
                                    marginTop:
                                        "25px"
                                }}
                            >

                                {(
                                    question.mcqOptions ||
                                    []
                                ).map(
                                    (
                                        option,
                                        index
                                    ) => {

                                        const optionId =
                                            option.id ??
                                            index;

                                        const selected =
                                            answers[
                                                question.id
                                            ] ===
                                            optionId;

                                        return (

                                            <label
                                                key={
                                                    optionId
                                                }
                                                style={{
                                                    display:
                                                        "block",
                                                    padding:
                                                        "15px",
                                                    marginBottom:
                                                        "12px",
                                                    border:
                                                        selected
                                                            ? "2px solid #2563eb"
                                                            : "1px solid #e2e8f0",
                                                    borderRadius:
                                                        "8px",
                                                    background:
                                                        selected
                                                            ? "#eff6ff"
                                                            : "#ffffff",
                                                    cursor:
                                                        "pointer"
                                                }}
                                            >

                                                <input
                                                    type="radio"
                                                    name={
                                                        `question-${question.id}`
                                                    }
                                                    value={
                                                        optionId
                                                    }
                                                    checked={
                                                        selected
                                                    }
                                                    onChange={() =>
                                                        handleAnswer(
                                                            question.id,
                                                            optionId
                                                        )
                                                    }
                                                    style={{
                                                        marginRight:
                                                            "10px"
                                                    }}
                                                />

                                                {option.optionText ??
                                                    option.text ??
                                                    option.content ??
                                                    `Option ${
                                                        index +
                                                        1
                                                    }`}

                                            </label>

                                        );
                                    }
                                )}

                            </div>

                        )}


                        {/* ==================================================
                            CODING QUESTION
                           ================================================== */}

                        {question.questionType ===
                            "CODING" && (

                            <div
                                style={{
                                    marginTop:
                                        "25px"
                                }}
                            >

                                <textarea
                                    value={
                                        answers[
                                            question.id
                                        ] || ""
                                    }
                                    onChange={
                                        event =>
                                            handleAnswer(
                                                question.id,
                                                event.target
                                                    .value
                                            )
                                    }
                                    placeholder="Write your code here..."
                                    style={{
                                        width:
                                            "100%",
                                        minHeight:
                                            "300px",
                                        padding:
                                            "15px",
                                        boxSizing:
                                            "border-box",
                                        border:
                                            "1px solid #cbd5e1",
                                        borderRadius:
                                            "8px",
                                        fontFamily:
                                            "Consolas, monospace",
                                        fontSize:
                                            "14px",
                                        resize:
                                            "vertical"
                                    }}
                                />

                            </div>

                        )}


                        {/* ==================================================
                            NAVIGATION
                           ================================================== */}

                        <div
                            style={{
                                display:
                                    "flex",
                                justifyContent:
                                    "space-between",
                                marginTop:
                                    "30px"
                            }}
                        >

                            <button
                                onClick={
                                    handlePrevious
                                }
                                disabled={
                                    currentQuestion ===
                                    0 ||
                                    submitting
                                }
                                style={{
                                    padding:
                                        "10px 20px",
                                    borderRadius:
                                        "8px",
                                    border:
                                        "1px solid #cbd5e1",
                                    background:
                                        currentQuestion ===
                                            0 ||
                                        submitting
                                            ? "#e2e8f0"
                                            : "#ffffff",
                                    cursor:
                                        currentQuestion ===
                                            0 ||
                                        submitting
                                            ? "not-allowed"
                                            : "pointer"
                                }}
                            >
                                Previous
                            </button>


                            {currentQuestion <
                                questions.length - 1 ? (

                                <button
                                    onClick={
                                        handleNext
                                    }
                                    disabled={
                                        submitting
                                    }
                                    style={{
                                        padding:
                                            "10px 25px",
                                        border:
                                            "none",
                                        borderRadius:
                                            "8px",
                                        background:
                                            submitting
                                                ? "#64748b"
                                                : "#2563eb",
                                        color:
                                            "#ffffff",
                                        fontWeight:
                                            "600",
                                        cursor:
                                            submitting
                                                ? "not-allowed"
                                                : "pointer"
                                    }}
                                >
                                    Next
                                </button>

                            ) : (

                                <button
                                    onClick={
                                        handleSubmitAssessment
                                    }
                                    disabled={
                                        submitting
                                    }
                                    style={{
                                        padding:
                                            "10px 25px",
                                        border:
                                            "none",
                                        borderRadius:
                                            "8px",
                                        background:
                                            submitting
                                                ? "#64748b"
                                                : "#16a34a",
                                        color:
                                            "#ffffff",
                                        fontWeight:
                                            "600",
                                        cursor:
                                            submitting
                                                ? "not-allowed"
                                                : "pointer"
                                    }}
                                >

                                    {submitting
                                        ? "Submitting..."
                                        : "Submit Assessment"}

                                </button>

                            )}

                        </div>

                    </div>

                )}

            </div>

        </div>
    );
}

export default CandidateExamPage;