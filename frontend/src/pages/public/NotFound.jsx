import { ArrowLeft, Home, SearchX, ShieldCheck } from "lucide-react";
import { useNavigate } from "react-router-dom";
import "./NotFound.css";

function NotFound() {
  const navigate = useNavigate();

  return (
    <main className="not-found-page">

      <div className="not-found-card">

        <div className="not-found-brand">
          <ShieldCheck size={20} />
          <span>SandBox</span>
        </div>

        <div className="not-found-icon">
          <SearchX size={34} />
        </div>

        <p className="not-found-code">
          ERROR 404
        </p>

        <h1>Page not found</h1>

        <p className="not-found-description">
          The page you're looking for doesn't exist,
          may have been moved, or the address may be incorrect.
        </p>

        <div className="not-found-actions">

          <button
            type="button"
            className="not-found-primary"
            onClick={() => navigate("/")}
          >
            <Home size={17} />
            Go to Home
          </button>

          <button
            type="button"
            className="not-found-secondary"
            onClick={() => navigate(-1)}
          >
            <ArrowLeft size={17} />
            Go Back
          </button>

        </div>

      </div>

      <p className="not-found-copyright">
        © 2026 SandBox. All rights reserved.
      </p>

    </main>
  );
}

export default NotFound;