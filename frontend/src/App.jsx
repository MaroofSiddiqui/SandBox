import { Navigate, Route, Routes } from "react-router-dom";
import Login from "./pages/Login";
import HrDashboard from "./pages/HrDashboard";
import SuperAdminDashboard from "./pages/SuperAdminDashboard";
import ProtectedRoute from "./components/ProtectedRoute";

function App() {
  return (
    <Routes>
      {/* Public Route */}
      <Route path="/login" element={<Login />} />

      {/* HR Only */}
      <Route
        path="/hr"
        element={
          <ProtectedRoute allowedRoles={["HR"]}>
            <HrDashboard />
          </ProtectedRoute>
        }
      />

      {/* Super Admin Only */}
      <Route
        path="/super-admin"
        element={
          <ProtectedRoute allowedRoles={["SUPER_ADMIN"]}>
            <SuperAdminDashboard />
          </ProtectedRoute>
        }
      />

      {/* Default */}
      <Route path="/" element={<Navigate to="/login" replace />} />

      {/* Unknown Routes */}
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}

export default App;