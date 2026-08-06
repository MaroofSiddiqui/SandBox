import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Eye, EyeOff, LockKeyhole, Mail, ShieldCheck } from "lucide-react";
import { useAuth } from "../../context/AuthContext";
import "./Login.css";

function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  const [errors, setErrors] = useState({});
  const [serverError, setServerError] = useState("");
  const [loading, setLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();

  const validateForm = () => {
    const newErrors = {};

    const cleanEmail = email.trim();

    if (!cleanEmail) {
      newErrors.email = "Email address is required.";
    } else if (
      !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(cleanEmail)
    ) {
      newErrors.email = "Enter a valid email address.";
    }

    if (!password) {
      newErrors.password = "Password is required.";
    }

    setErrors(newErrors);

    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Prevent duplicate submissions
    if (loading) return;

    setServerError("");

    if (!validateForm()) {
      return;
    }

    setLoading(true);

    try {
      const user = await login(email.trim(), password);

      if (user.role === "SUPER_ADMIN") {
        navigate("/admin", { replace: true });
      } else if (user.role === "HR") {
        navigate("/hr", { replace: true });
      } else {
        setServerError(
          "Your account does not have access to this application."
        );
      }
    } catch (err) {
      setServerError(
        err.response?.data?.message ||
          "Unable to sign in. Please verify your credentials and try again."
      );
    } finally {
      setLoading(false);
    }
  };

  const handleEmailChange = (e) => {
    setEmail(e.target.value);

    if (errors.email) {
      setErrors((prev) => ({
        ...prev,
        email: "",
      }));
    }

    if (serverError) {
      setServerError("");
    }
  };

  const handlePasswordChange = (e) => {
    setPassword(e.target.value);

    if (errors.password) {
      setErrors((prev) => ({
        ...prev,
        password: "",
      }));
    }

    if (serverError) {
      setServerError("");
    }
  };

  return (
    <main className="login-page">
      <section className="login-brand-section">
        <div className="brand-content">
          <div className="brand-logo">
            <ShieldCheck size={30} />
          </div>

          <h1>
            Sand<span>Box</span>
          </h1>

          <p className="brand-tagline">
            Secure hiring. Structured evaluation.
            <br />
            Better decisions.
          </p>

          <div className="brand-description">
            A centralized recruitment platform built for secure,
            organization-driven hiring workflows.
          </div>
        </div>

        <p className="brand-footer">
          Secure Recruitment Management Platform
        </p>
      </section>

      <section className="login-form-section">
        <div className="login-container">
          <div className="mobile-brand">
            <ShieldCheck size={25} />
            <span>SandBox</span>
          </div>

          <div className="login-heading">
            <p className="login-eyebrow">WELCOME BACK</p>

            <h2>Sign in to your account</h2>

            <p>
              Enter your credentials to access your workspace.
            </p>
          </div>

          <form onSubmit={handleSubmit} noValidate>
            <div className="form-group">
              <label htmlFor="email">Email address</label>

              <div
                className={`input-wrapper ${
                  errors.email ? "input-error" : ""
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
                  aria-invalid={Boolean(errors.email)}
                />
              </div>

              {errors.email && (
                <p className="field-error">{errors.email}</p>
              )}
            </div>

            <div className="form-group">
              <label htmlFor="password">Password</label>

              <div
                className={`input-wrapper ${
                  errors.password ? "input-error" : ""
                }`}
              >
                <LockKeyhole size={18} />

                <input
                  id="password"
                  type={showPassword ? "text" : "password"}
                  value={password}
                  onChange={handlePasswordChange}
                  placeholder="Enter your password"
                  autoComplete="current-password"
                  disabled={loading}
                  aria-invalid={Boolean(errors.password)}
                />

                <button
                  type="button"
                  className="password-toggle"
                  onClick={() =>
                    setShowPassword((prev) => !prev)
                  }
                  aria-label={
                    showPassword ? "Hide password" : "Show password"
                  }
                  disabled={loading}
                >
                  {showPassword ? (
                    <EyeOff size={18} />
                  ) : (
                    <Eye size={18} />
                  )}
                </button>
              </div>

              {errors.password && (
                <p className="field-error">{errors.password}</p>
              )}
            </div>

            {serverError && (
              <div className="server-error" role="alert">
                {serverError}
              </div>
            )}

            <button
              className="login-button"
              type="submit"
              disabled={loading}
            >
              {loading ? (
                <>
                  <span className="spinner"></span>
                  Signing in...
                </>
              ) : (
                "Sign In"
              )}
            </button>
          </form>

          <div className="security-message">
            <ShieldCheck size={15} />
            <span>Protected access to your organization workspace</span>
          </div>
        </div>

        <p className="login-copyright">
          © 2026 SandBox. All rights reserved.
        </p>
      </section>
    </main>
  );
}

export default Login;