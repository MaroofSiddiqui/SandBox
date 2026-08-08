import { Navigate, Route, Routes, useParams } from "react-router-dom";

// =========================
// PUBLIC PAGES
// =========================
import Home from "./pages/public/Home";
import About from "./pages/public/About";
import NotFound from "./pages/public/NotFound";
import AccessDenied from "./pages/public/AccessDenied";

// =========================
// AUTH PAGES
// =========================
import Login from "./pages/auth/Login";
import Register from "./pages/auth/Register";
import ForgotPassword from "./pages/auth/ForgotPassword";
import VerifyOtp from "./pages/auth/VerifyOtp";
import ResetPassword from "./pages/auth/ResetPassword";
import VerifyEmail from "./pages/auth/VerifyEmail";

// =========================
// CANDIDATE PAGES
// =========================
import CandidateDashboard from "./pages/candidate/CandidateDashboard";
import TestProctoringView from "./pages/candidate/TestProctoringView";
import CandidateExamPage from "./pages/candidate/CandidateExamPage";

// =========================
// HR PAGES
// =========================
import HrDashboard from "./pages/hr/HrDashboard";
import Candidates from "./pages/Candidates";
import AssessmentsPage from "./pages/hr/AssessmentsPage";
import CreateAssessmentPage from "./pages/hr/CreateAssessmentPage";
import CreateQuestionPage from "./pages/hr/CreateQuestionPage";
import AssessmentManagePage from "./pages/hr/AssessmentManagePage";
import ReportsPage from "./pages/hr/ReportsPage";
import ExamResultDetailPage from "./pages/hr/ExamResultDetailPage";

// =========================
// SUPER ADMIN PAGES
// =========================
import Dashboard from "./pages/admin/Dashboard";
import HrManagement from "./pages/admin/HrManagement";
import Organizations from "./pages/admin/Organizations";
import PaymentMonitoring from "./pages/admin/PaymentMonitoring";
import SubscriptionManagement from "./pages/admin/SubscriptionManagement";

// =========================
// OTHER PAGES / COMPONENTS
// =========================
import SubscriptionPlans from "./pages/SubscriptionPlans";
import CodeEvaluation from "./components/editor/CodeEvaluation";

// =========================
// LAYOUTS
// =========================
import AdminLayout from "./layouts/AdminLayout";

// =========================
// ROUTE SECURITY
// =========================
import ProtectedRoute from "./components/common/ProtectedRoute";
import { useAuth } from "./context/AuthContext";


// ============================================================
// CANDIDATE ASSESSMENT ROUTE
// ============================================================
//
// This wrapper gets:
//
// 1. Real assessment ID from URL
// 2. Real candidate ID from logged-in user
//
// Example:
//
// /candidate/assessment/3
//
// candidateId = 13
// examId      = 3
//
// ============================================================

function CandidateAssessmentRoute() {

    const { assessmentId } = useParams();

    const { user } = useAuth();

    const candidateId =
        user?.userId ||
        user?.id ||
        null;

    if (!candidateId) {

        return (
            <div
                style={{
                    minHeight: "100vh",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    fontFamily: "Arial, sans-serif"
                }}
            >

                <h2>
                    Candidate information not available.
                </h2>

            </div>
        );
    }

    return (
        <TestProctoringView
            candidateId={String(candidateId)}
            examId={String(assessmentId)}
        />
    );
}


// ============================================================
// APP
// ============================================================

function App() {

    return (

        <Routes>

            {/* =========================
                PUBLIC ROUTES
               ========================= */}

            <Route
                path="/"
                element={<Home />}
            />

            <Route
                path="/about"
                element={<About />}
            />

            <Route
                path="/login"
                element={<Login />}
            />

            <Route
                path="/register"
                element={<Register />}
            />

            <Route
                path="/forgot-password"
                element={<ForgotPassword />}
            />

            <Route
                path="/verify-otp"
                element={<VerifyOtp />}
            />

            <Route
                path="/reset-password"
                element={<ResetPassword />}
            />

            <Route
                path="/verify-email"
                element={<VerifyEmail />}
            />

            <Route
                path="/access-denied"
                element={<AccessDenied />}
            />


            {/* =========================
                CANDIDATE ROUTES
               ========================= */}

            <Route
                path="/candidate"
                element={
                    <ProtectedRoute
                        allowedRoles={["CANDIDATE"]}
                    >
                        <CandidateDashboard />
                    </ProtectedRoute>
                }
            />


            {/* =========================
                CANDIDATE ASSESSMENT
               ========================= */}

            <Route
                path="/candidate/assessment/:assessmentId"
                element={
                    <ProtectedRoute
                        allowedRoles={["CANDIDATE"]}
                    >
                        <CandidateAssessmentRoute />
                    </ProtectedRoute>
                }
            />


            {/* =========================
                ACTUAL CANDIDATE EXAM
               =========================
               
               Flow:
               
               Candidate Dashboard
                       ↓
               /candidate/assessment/3
                       ↓
               TestProctoringView
                       ↓
               POST /assessment-submission/start/3
                       ↓
               Submission created
                       ↓
               /candidate/exam/3
                       ↓
               CandidateExamPage
               
               ========================= */}

            <Route
                path="/candidate/exam/:examId"
                element={
                    <ProtectedRoute
                        allowedRoles={["CANDIDATE"]}
                    >
                        <CandidateExamPage />
                    </ProtectedRoute>
                }
            />


            {/* =========================
                CANDIDATE PROCTORING
               ========================= */}

            <Route
                path="/candidate/proctoring"
                element={
                    <ProtectedRoute
                        allowedRoles={["CANDIDATE"]}
                    >
                        <TestProctoringView />
                    </ProtectedRoute>
                }
            />


            {/* =========================
                HR ROUTES
               ========================= */}

            <Route
                path="/hr"
                element={
                    <ProtectedRoute
                        allowedRoles={["HR"]}
                    >
                        <HrDashboard />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/hr/candidates"
                element={
                    <ProtectedRoute
                        allowedRoles={["HR"]}
                    >
                        <Candidates />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/hr/assessments"
                element={
                    <ProtectedRoute
                        allowedRoles={["HR"]}
                    >
                        <AssessmentsPage />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/hr/assessments/create"
                element={
                    <ProtectedRoute
                        allowedRoles={["HR"]}
                    >
                        <CreateAssessmentPage />
                    </ProtectedRoute>
                }
            />


            {/* =========================
                QUESTION BANK
               ========================= */}

            <Route
                path="/hr/questions/create"
                element={
                    <ProtectedRoute
                        allowedRoles={["HR"]}
                    >
                        <CreateQuestionPage />
                    </ProtectedRoute>
                }
            />


            {/* =========================
                ASSESSMENT MANAGEMENT
               ========================= */}

            <Route
                path="/hr/assessments/:examId/manage"
                element={
                    <ProtectedRoute
                        allowedRoles={["HR"]}
                    >
                        <AssessmentManagePage />
                    </ProtectedRoute>
                }
            />


            {/* =========================
                REPORTS
               ========================= */}

            <Route
                path="/hr/reports"
                element={
                    <ProtectedRoute
                        allowedRoles={["HR"]}
                    >
                        <ReportsPage />
                    </ProtectedRoute>
                }
            />


            {/* =========================
                EXAM RESULT
               ========================= */}

            <Route
                path="/hr/results/:examId"
                element={
                    <ProtectedRoute
                        allowedRoles={["HR"]}
                    >
                        <ExamResultDetailPage />
                    </ProtectedRoute>
                }
            />


            {/* =========================
                HR SUBSCRIPTION
               ========================= */}

            <Route
                path="/hr/subscriptions"
                element={
                    <ProtectedRoute
                        allowedRoles={["HR"]}
                    >
                        <SubscriptionPlans />
                    </ProtectedRoute>
                }
            />


            {/* =========================
                SUPER ADMIN ROUTES
               ========================= */}

            <Route
                path="/admin"
                element={
                    <ProtectedRoute
                        allowedRoles={["SUPER_ADMIN"]}
                    >
                        <AdminLayout />
                    </ProtectedRoute>
                }
            >

                <Route
                    index
                    element={
                        <Navigate
                            to="dashboard"
                            replace
                        />
                    }
                />

                <Route
                    path="dashboard"
                    element={<Dashboard />}
                />

                <Route
                    path="organizations"
                    element={<Organizations />}
                />

                <Route
                    path="hrs"
                    element={<HrManagement />}
                />

                <Route
                    path="subscriptions"
                    element={<SubscriptionManagement />}
                />

                <Route
                    path="payments"
                    element={<PaymentMonitoring />}
                />

            </Route>


            {/* =========================
                CODE EVALUATION
               ========================= */}

            <Route
                path="/code-evaluation"
                element={<CodeEvaluation />}
            />


            {/* =========================
                404
               ========================= */}

            <Route
                path="*"
                element={<NotFound />}
            />

        </Routes>
    );
}

export default App;