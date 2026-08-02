import { Navigate, Route, Routes } from "react-router-dom";

import Login from "./pages/Login";
import Register from "./pages/Register";
import ForgotPassword from "./pages/ForgotPassword";
import VerifyOtp from "./pages/VerifyOtp";
import ResetPassword from "./pages/ResetPassword";
import VerifyEmail from "./pages/VerifyEmail";

import HrDashboard from "./pages/HrDashboard";
import SuperAdminDashboard from "./pages/SuperAdminDashboard";
import CandidateDashboard from "./pages/CandidateDashboard";

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
        path="/verify-email"
        element={<VerifyEmail />}
      />

      <Route
  path="/verify-otp"
  element={<VerifyOtp />}
/>

<Route
  path="/verify-email"
  element={<VerifyEmail />}
/>

      <Route
  path="/reset-password"
  element={<ResetPassword />}
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


      {/* =========================
          SUPER ADMIN ROUTES
         ========================= */}

      <Route
        path="/super-admin"
        element={
          <ProtectedRoute allowedRoles={["SUPER_ADMIN"]}>
            <SuperAdminDashboard />
          </ProtectedRoute>
        }
      />


      {/* =========================
          DEFAULT ROUTE
         ========================= */}

      <Route
        path="/"
        element={
          <Navigate
            to="/login"
            replace
          />
        }
      />


      {/* =========================
          UNKNOWN ROUTES
         ========================= */}

      <Route
        path="*"
        element={
          <Navigate
            to="/login"
            replace
          />
        }
      />

    </Routes>
  );
}

export default App;