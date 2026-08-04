import { Navigate, Route, Routes } from "react-router-dom";

import Home from "./pages/Home";
import About from "./pages/About";
import Login from "./pages/Login";
import Register from "./pages/Register";
import ForgotPassword from "./pages/ForgotPassword";
import VerifyOtp from "./pages/VerifyOtp";
import ResetPassword from "./pages/ResetPassword";
import VerifyEmail from "./pages/VerifyEmail";
import NotFound from "./pages/NotFound";
import AccessDenied from "./pages/AccessDenied";

import HrDashboard from "./pages/HrDashboard";
import SuperAdminDashboard from "./pages/SuperAdminDashboard";
import CandidateDashboard from "./pages/CandidateDashboard";

import AdminLayout from "./layouts/AdminLayout";

import Dashboard from "./pages/admin/Dashboard";
import Organizations from "./pages/admin/Organizations";
import HrManagement from "./pages/admin/HrManagement";

import SubscriptionManagement from "./pages/admin/SubscriptionManagement";
import SubscriptionPlans from "./pages/SubscriptionPlans";

import ProtectedRoute from "./components/ProtectedRoute";

function App() {
  return (
    <Routes>

      {/* =========================
          PUBLIC ROUTES
         ========================= */}

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

      <Route
        path="/about"
        element={<About />}
      />


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

        <Route
          index
          element={<Navigate to="dashboard" replace />}
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

      </Route>


      {/* =========================
          DEFAULT ROUTE
         ========================= */}

      <Route
        path="/"
        element={<Home />}
      />


      {/* =========================
          404 - UNKNOWN ROUTES
         ========================= */}

      <Route
        path="*"
        element={<NotFound />}
      />

    </Routes>
  );
}

export default App;