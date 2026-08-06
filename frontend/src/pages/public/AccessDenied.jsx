import {
  ArrowLeft,
  Home,
  ShieldAlert,
  ShieldCheck,
} from "lucide-react";

import { useNavigate } from "react-router-dom";
import "./AccessDenied.css";

function AccessDenied() {
  const navigate = useNavigate();

  const user = JSON.parse(
    localStorage.getItem("user")
  );

  /*
   * Send the logged-in user back to
   * their correct dashboard.
   */
  const goToDashboard = () => {

    if (user?.role === "SUPER_ADMIN") {
      navigate("/super-admin", {
        replace: true,
      });
      return;
    }

    if (user?.role === "HR") {
      navigate("/hr", {
        replace: true,
      });
      return;
    }

    if (user?.role === "CANDIDATE") {
      navigate("/candidate", {
        replace: true,
      });
      return;
    }

    navigate("/login", {
      replace: true,
    });
  };

  return (
    <main className="access-denied-page">

      <div className="access-denied-card">

        {/* BRAND */}
        <div className="access-denied-brand">
          <ShieldCheck size={20} />
          <span>SandBox</span>
        </div>


        {/* ICON */}
        <div className="access-denied-icon">
          <ShieldAlert size={35} />
        </div>


        {/* ERROR CODE */}
        <p className="access-denied-code">
          ERROR 403
        </p>


        {/* HEADING */}
        <h1>Access denied</h1>


        {/* DESCRIPTION */}
        <p className="access-denied-description">
          You don't have permission to access this page.
          Your account does not have the required role
          or privileges for this resource.
        </p>


        {/* ACTIONS */}
        <div className="access-denied-actions">

          <button
            type="button"
            className="access-denied-primary"
            onClick={goToDashboard}
          >
            <Home size={17} />
            Go to Dashboard
          </button>

          <button
            type="button"
            className="access-denied-secondary"
            onClick={() => navigate(-1)}
          >
            <ArrowLeft size={17} />
            Go Back
          </button>

        </div>

      </div>


      <p className="access-denied-copyright">
        © 2026 SandBox. All rights reserved.
      </p>

    </main>
  );
}

export default AccessDenied;