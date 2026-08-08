import { Navigate, Route, Routes } from "react-router-dom";

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

// =========================
// HR PAGES
// =========================
import HrDashboard from "./pages/hr/HrDashboard";
import Candidates from "./pages/Candidates";
import AssessmentsPage from "./pages/hr/AssessmentsPage";
import CreateAssessmentPage from "./pages/hr/CreateAssessmentPage";
// import AssessmentManagePage from "./pages/hr/AssessmentManagePage"; // We will create this next!

// =========================
// SUPER ADMIN PAGES
// =========================
import Dashboard from "./pages/admin/Dashboard";
import HrManagement from "./pages/admin/HrManagement";
import Organizations from "./pages/admin/Organizations";
import PaymentMonitoring from "./pages/admin/PaymentMonitoring";
import SubscriptionManagement from "./pages/admin/SubscriptionManagement";
import SuperAdminDashboard from "./pages/admin/SuperAdminDashboard";

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

function App() {
  return (
    <Routes>

      {/* =========================
          PUBLIC ROUTES
         ========================= */}

      <Route path="/" element={<Home />} />
      <Route path="/about" element={<About />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/forgot-password" element={<ForgotPassword />} />
      <Route path="/verify-otp" element={<VerifyOtp />} />
      <Route path="/reset-password" element={<ResetPassword />} />
      <Route path="/verify-email" element={<VerifyEmail />} />
      <Route path="/access-denied" element={<AccessDenied />} />


      {/* =========================
          CANDIDATE ROUTES
         ========================= */}

      <Route
        path="/candidate"
        element={
          <ProtectedRoute allowedRoles={["CANDIDATE"]}>
            <CandidateDashboard />
          </ProtectedRoute>
        }
      />


      {/* =========================
          HR ROUTES
         ========================= */}

      <Route
        path="/hr"
        element={
          <ProtectedRoute allowedRoles={["HR"]}>
            <HrDashboard />
          </ProtectedRoute>
        }
      />

      <Route
        path="/hr/candidates"
        element={
          <ProtectedRoute allowedRoles={["HR"]}>
            <Candidates />
          </ProtectedRoute>
        }
      />

      {/* NEW ASSESSMENT ROUTES */}
      <Route
        path="/hr/assessments"
        element={
          <ProtectedRoute allowedRoles={["HR"]}>
            <AssessmentsPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/hr/assessments/create"
        element={
          <ProtectedRoute allowedRoles={["HR"]}>
            <CreateAssessmentPage />
          </ProtectedRoute>
        }
      />

      {/* Placeholder for the Exam Management Page */}
      {/* 
      <Route
        path="/hr/assessments/:examId/manage"
        element={
          <ProtectedRoute allowedRoles={["HR"]}>
            <AssessmentManagePage />
          </ProtectedRoute>
        }
      /> 
      */}

      <Route
        path="/hr/subscriptions"
        element={
          <ProtectedRoute allowedRoles={["HR"]}>
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
          <ProtectedRoute allowedRoles={["SUPER_ADMIN"]}>
            <AdminLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<Navigate to="dashboard" replace />} />
        <Route path="dashboard" element={<Dashboard />} />
        <Route path="organizations" element={<Organizations />} />
        <Route path="hrs" element={<HrManagement />} />
        <Route path="subscriptions" element={<SubscriptionManagement />} />
        <Route path="payments" element={<PaymentMonitoring />} />
      </Route>


      {/* =========================
          CODE EVALUATION TEMPORARY TEST ROUTE
         ========================= */}

      <Route path="/code-evaluation" element={<CodeEvaluation />} />


      {/* =========================
          404
         ========================= */}

      <Route path="*" element={<NotFound />} />

    </Routes>
  );
}

export default App;