import { useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  ArrowLeft,
  Mail,
  ShieldCheck,
} from "lucide-react";
import axiosInstance from "../api/axiosInstance";
import "./ForgotPassword.css";

function ForgotPassword() {
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const validateEmail = () => {
    const cleanEmail = email.trim();

    if (!cleanEmail) {
      setError("Email address is required.");
      return false;
    }

    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(cleanEmail)) {
      setError("Enter a valid email address.");
      return false;
    }

    setError("");
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (loading) return;

    if (!validateEmail()) {
      return;
    }

    setLoading(true);
    setError("");

    try {
      await axiosInstance.post(
        "/api/auth/password/forgot",
        {
          email: email.trim(),
        }
      );

      // Send email to OTP page
      navigate("/verify-otp", {
        state: {
          email: email.trim(),
        },
      });
    } catch (err) {
      setError(
        err.response?.data?.message ||
          err.response?.data ||
          "Unable to send password reset code. Please try again."
      );
    } finally {
      setLoading(false);
    }
  };

  const handleEmailChange = (e) => {
    setEmail(e.target.value);

    if (error) {
      setError("");
    }
  };

  return (
    <main className="forgot-page">
      <div className="forgot-card">

        <div className="forgot-logo">
          <ShieldCheck size={27} />
        </div>

        <div className="forgot-heading">
          <p className="forgot-eyebrow">
            PASSWORD RECOVERY
          </p>

          <h1>Forgot your password?</h1>

          <p>
            Enter the email address associated with your
            account and we'll send you a verification code.
          </p>
        </div>

        <form onSubmit={handleSubmit} noValidate>

          <div className="forgot-form-group">
            <label htmlFor="email">
              Email address
            </label>

            <div
              className={`forgot-input-wrapper ${
                error ? "forgot-input-error" : ""
              }`}
            >
              <Mail size={18} />

              <input
                id="email"
                type="email"
                value={email}
                onChange={handleEmailChange}
                placeholder="name@company.com"
                autoComplete="email"
                disabled={loading}
              />
            </div>

            {error && (
              <p className="forgot-error">
                {error}
              </p>
            )}
          </div>

          <button
            type="submit"
            className="forgot-submit-button"
            disabled={loading}
          >
            {loading ? (
              <>
                <span className="forgot-spinner"></span>
                Sending code...
              </>
            ) : (
              "Send verification code"
            )}
          </button>

        </form>

        <button
          type="button"
          className="back-login-button"
          onClick={() => navigate("/login")}
          disabled={loading}
        >
          <ArrowLeft size={16} />
          Back to sign in
        </button>

      </div>

      <p className="forgot-copyright">
        © 2026 SandBox. All rights reserved.
      </p>
    </main>
  );
}

export default ForgotPassword;