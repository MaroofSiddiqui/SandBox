import { Navigate, Route, Routes } from "react-router-dom";

import Login from "./pages/Login";
import HrDashboard from "./pages/HrDashboard";
import SuperAdminDashboard from "./pages/SuperAdminDashboard";
import CandidateDashboard from "./pages/CandidateDashboard";
import VerifyEmail from "./pages/VerifyEmail";

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
        path="/verify-email"
        element={<VerifyEmail />}
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