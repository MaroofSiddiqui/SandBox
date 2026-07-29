import { Navigate } from "react-router-dom";

function ProtectedRoute({ children, allowedRoles }) {
    const token = localStorage.getItem("token");
    const user = JSON.parse(localStorage.getItem("user"));

    // User is not logged in
    if (!token || !user) {
        return <Navigate to="/login" replace />;
    }

    // User is logged in but does not have permission for this route
    if (allowedRoles && !allowedRoles.includes(user.role)) {
        if (user.role === "SUPER_ADMIN") {
            return <Navigate to="/super-admin" replace />;
        }

        if (user.role === "HR") {
            return <Navigate to="/hr" replace />;
        }

        return <Navigate to="/login" replace />;
    }

    return children;
}

export default ProtectedRoute;