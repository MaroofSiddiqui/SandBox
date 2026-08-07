import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  getAssignmentById,
  startAssessmentSession,
  submitAssessmentSession,
  getAssessmentDetails,
  getAllQuestionsList,
} from "../api/assignmentApi";
import Timer from "../components/Exams/Timer";
import QuestionPalette from "../components/Exams/QuestionPalette";
import McqQuestion from "../components/Exams/McqQuestion";
import OneWordQuestion from "../components/Exams/OneWordQuestion";
import NavigationButtons from "../components/Exams/NavigationButtons";
import { AlertTriangle, Lock } from "lucide-react";
import "../styles/examination.css";

function ExaminationView() {
  const { assignmentId } = useParams();
  const navigate = useNavigate();

  const [assignment, setAssignment] = useState(null);
  const [assessment, setAssessment] = useState(null);
  const [questions, setQuestions] = useState([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [answers, setAnswers] = useState({});
  const [reviewFlags, setReviewFlags] = useState({});
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [showSubmitConfirm, setShowSubmitConfirm] = useState(false);

  useEffect(() => {
    initExamSession();
  }, [assignmentId]);

  const initExamSession = async () => {
    try {
      setLoading(true);
      // Fetch assignment
      const assignRes = await getAssignmentById(assignmentId);
      const assignData = assignRes.data;
      setAssignment(assignData);

      if (assignData.status === "SUBMITTED" || assignData.status === "EVALUATED") {
        navigate(`/result/${assignmentId}`);
        return;
      }

      // If ASSIGNED, call start assessment
      if (assignData.status === "ASSIGNED") {
        try {
          const startRes = await startAssessmentSession(assignmentId);
          setAssignment(startRes.data);
        } catch (e) {
          console.warn("Already started or error starting:", e);
        }
      }

      // Fetch assessment info
      let qList = [];
      try {
        const assessmentRes = await getAssessmentDetails(assignData.assessmentId);
        setAssessment(assessmentRes.data);

        if (assessmentRes.data.questionIds && assessmentRes.data.questionIds.length > 0) {
          const allQsRes = await getAllQuestionsList();
          const allQs = allQsRes.data || [];
          qList = allQs.filter((q) => assessmentRes.data.questionIds.includes(q.id));
        }
      } catch (err) {
        console.warn("Could not fetch full assessment questions list, loading mock fallback questions", err);
      }

      // Fallback sample questions if none returned
      if (!qList || qList.length === 0) {
        qList = [
          {
            id: 101,
            title: "What is the time complexity of binary search on a sorted array?",
            questionType: "MCQ",
            marks: 5,
            mcqOptions: [
              { id: "A", optionText: "O(n)" },
              { id: "B", optionText: "O(log n)" },
              { id: "C", optionText: "O(n log n)" },
              { id: "D", optionText: "O(1)" },
            ],
          },
          {
            id: 102,
            title: "Which HTTP status code signifies a successful Resource Creation?",
            questionType: "MCQ",
            marks: 5,
            mcqOptions: [
              { id: "A", optionText: "200 OK" },
              { id: "B", optionText: "201 Created" },
              { id: "C", optionText: "204 No Content" },
              { id: "D", optionText: "400 Bad Request" },
            ],
          },
          {
            id: 103,
            title: "Write a function to reverse a string in Java or Python.",
            questionType: "CODING",
            marks: 10,
            problemStatement: "Input: 'hello'\nOutput: 'olleh'",
          },
        ];
      }

      setQuestions(qList);

      // Load cached local answers
      const savedAnswers = localStorage.getItem(`exam_answers_${assignmentId}`);
      if (savedAnswers) {
        try {
          setAnswers(JSON.parse(savedAnswers));
        } catch (e) {}
      }
    } catch (err) {
      console.error("Failed to load exam session:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleAnswerChange = (val) => {
    const updated = { ...answers, [currentIndex]: val };
    setAnswers(updated);
    // Auto-save to localStorage
    localStorage.setItem(`exam_answers_${assignmentId}`, JSON.stringify(updated));
  };

  const handleToggleReview = () => {
    setReviewFlags((prev) => ({
      ...prev,
      [currentIndex]: !prev[currentIndex],
    }));
  };

  const handleClearAnswer = () => {
    const updated = { ...answers };
    delete updated[currentIndex];
    setAnswers(updated);
    localStorage.setItem(`exam_answers_${assignmentId}`, JSON.stringify(updated));
  };

  const handleSubmitExam = async () => {
    if (submitting) return;
    try {
      setSubmitting(true);
      await submitAssessmentSession(assignmentId);
      localStorage.removeItem(`exam_answers_${assignmentId}`);
      navigate(`/result/${assignmentId}`);
    } catch (err) {
      alert(err.response?.data?.message || "Failed to submit assessment");
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="exam-layout" style={{ justifyContent: "center", alignItems: "center" }}>
        <p style={{ color: "#94a3b8", fontSize: "1.2rem" }}>Loading Examination Environment...</p>
      </div>
    );
  }

  const currentQ = questions[currentIndex];

  return (
    <div className="exam-layout">
      {/* Header */}
      <div className="exam-header">
        <div className="exam-title-box">
          <h2>{assessment?.title || `Assessment #${assignment?.assessmentId}`}</h2>
          <span>Candidate Assignment #{assignmentId}</span>
        </div>

        <Timer
          durationMinutes={assessment?.durationInMinutes || 30}
          onTimeUp={handleSubmitExam}
        />
      </div>

      {/* Main Content Area */}
      <div className="exam-main">
        <div className="question-area">
          {currentQ && (
            <div className="question-card">
              <div className="question-header">
                <span className="q-num">Question {currentIndex + 1} of {questions.length}</span>
                <span className="q-marks">{currentQ.marks || 5} Marks</span>
              </div>

              <div className="q-title">{currentQ.title}</div>

              {currentQ.questionType === "MCQ" ? (
                <McqQuestion
                  question={currentQ}
                  selectedAnswer={answers[currentIndex]}
                  onAnswerChange={handleAnswerChange}
                />
              ) : (
                <OneWordQuestion
                  question={currentQ}
                  value={answers[currentIndex] || ""}
                  onChange={handleAnswerChange}
                />
              )}
            </div>
          )}

          {/* Navigation Controls */}
          <NavigationButtons
            hasPrevious={currentIndex > 0}
            hasNext={currentIndex < questions.length - 1}
            onPrevious={() => setCurrentIndex((prev) => Math.max(0, prev - 1))}
            onNext={() => setCurrentIndex((prev) => Math.min(questions.length - 1, prev + 1))}
            onClear={handleClearAnswer}
            onToggleReview={handleToggleReview}
            isReview={reviewFlags[currentIndex]}
            onSubmit={() => setShowSubmitConfirm(true)}
          />
        </div>

        {/* Sidebar Question Palette */}
        <QuestionPalette
          questions={questions}
          currentIndex={currentIndex}
          answers={answers}
          reviewFlags={reviewFlags}
          onSelectQuestion={(idx) => setCurrentIndex(idx)}
        />
      </div>

      {/* Submit Confirmation Modal */}
      {showSubmitConfirm && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ textAlign: "center" }}>
            <AlertTriangle size={48} style={{ color: "#f59e0b", marginBottom: "1rem" }} />
            <h3 style={{ marginTop: 0 }}>Are you sure you want to submit?</h3>
            <p style={{ color: "#94a3b8", fontSize: "0.9rem" }}>
              Answered: {Object.keys(answers).length} / {questions.length} Questions
            </p>
            <div style={{ display: "flex", justifyContent: "center", gap: "1rem", marginTop: "1.5rem" }}>
              <button className="btn-secondary" onClick={() => setShowSubmitConfirm(false)}>
                Cancel & Resume
              </button>
              <button className="btn-submit" onClick={handleSubmitExam} disabled={submitting}>
                {submitting ? "Submitting..." : "Confirm & Submit"}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default ExaminationView;
