import { useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  CheckCircle2,
  Eye,
  EyeOff,
  LockKeyhole,
  Mail,
  ShieldCheck,
  User,
} from "lucide-react";

import axiosInstance from "../api/axiosInstance";
import "./Login.css";
import "./Register.css";

function Register() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
    confirmPassword: "",
  });

  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const [errors, setErrors] = useState({});
  const [serverError, setServerError] = useState("");
  const [loading, setLoading] = useState(false);
  const [registered, setRegistered] = useState(false);

  const passwordChecks = {
    length: formData.password.length >= 8,
    uppercase: /[A-Z]/.test(formData.password),
    lowercase: /[a-z]/.test(formData.password),
    number: /\d/.test(formData.password),
    special: /[^A-Za-z0-9\s]/.test(formData.password),
    noSpaces: !/\s/.test(formData.password),
  };

  const validateForm = () => {
    const newErrors = {};

    const cleanName = formData.name.trim();
    const cleanEmail = formData.email.trim();

    if (!cleanName) {
      newErrors.name = "Full name is required.";
    } else if (cleanName.length > 100) {
      newErrors.name = "Name cannot exceed 100 characters.";
    }

    if (!cleanEmail) {
      newErrors.email = "Email address is required.";
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(cleanEmail)) {
      newErrors.email = "Enter a valid email address.";
    }

    if (!formData.password) {
      newErrors.password = "Password is required.";
    } else if (!Object.values(passwordChecks).every(Boolean)) {
      newErrors.password = "Password does not meet the security requirements.";
    }

    if (!formData.confirmPassword) {
      newErrors.confirmPassword = "Please confirm your password.";
    } else if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = "Passwords do not match.";
    }

    setErrors(newErrors);

    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));

    if (errors[name]) {
      setErrors((prev) => ({
        ...prev,
        [name]: "",
      }));
    }

    if (serverError) {
      setServerError("");
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (loading) return;

    setServerError("");

    if (!validateForm()) {
      return;
    }

    setLoading(true);

    try {
      await axiosInstance.post("/api/auth/register", {
        name: formData.name.trim(),
        email: formData.email.trim(),
        password: formData.password,
      });

      setRegistered(true);
    } catch (error) {
      const responseData = error.response?.data;

      if (responseData?.errors) {
        setErrors((prev) => ({
          ...prev,
          ...responseData.errors,
        }));
      } else {
        setServerError(
          responseData?.message ||
            responseData ||
            "Unable to create your account. Please try again."
        );
      }
    } finally {
      setLoading(false);
    }
  };

  if (registered) {
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
          <div className="register-success">
            <div className="register-success-icon">
              <CheckCircle2 size={32} />
            </div>

            <p className="login-eyebrow">ACCOUNT CREATED</p>

            <h2>Verify your email</h2>

            <p>
              We've sent a verification link to
              <strong> {formData.email.trim()}</strong>.
            </p>

            <p className="register-success-help">
              Open the email and click the verification link before signing
              in to your account.
            </p>

            <button
              className="login-button"
              type="button"
              onClick={() => navigate("/login")}
            >
              Continue to Login
            </button>
          </div>

          <p className="login-copyright">
            © 2026 SandBox. All rights reserved.
          </p>
        </section>
      </main>
    );
  }

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
            Create your candidate account and access secure recruitment,
            assessment, and hiring workflows.
          </div>
        </div>

        <p className="brand-footer">
          Secure Recruitment Management Platform
        </p>
      </section>

      <section className="login-form-section register-form-section">
        <div className="login-container register-container">
          <div className="mobile-brand">
            <ShieldCheck size={25} />
            <span>SandBox</span>
          </div>

          <div className="login-heading register-heading">
            <p className="login-eyebrow">GET STARTED</p>

            <h2>Create your account</h2>

            <p>
              Register as a candidate to continue to SandBox.
            </p>
          </div>

          <form onSubmit={handleSubmit} noValidate>
            <div className="form-group">
              <label htmlFor="name">Full name</label>

              <div
                className={`input-wrapper ${
                  errors.name ? "input-error" : ""
                }`}
              >
                <User size={18} />

                <input
                  id="name"
                  name="name"
                  type="text"
                  value={formData.name}
                  onChange={handleChange}
                  placeholder="Enter your full name"
                  autoComplete="name"
                  disabled={loading}
                />
              </div>

              {errors.name && (
                <p className="field-error">{errors.name}</p>
              )}
            </div>

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
                  name="email"
                  type="email"
                  value={formData.email}
                  onChange={handleChange}
                  placeholder="name@example.com"
                  autoComplete="email"
                  disabled={loading}
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
                  name="password"
                  type={showPassword ? "text" : "password"}
                  value={formData.password}
                  onChange={handleChange}
                  placeholder="Create a secure password"
                  autoComplete="new-password"
                  disabled={loading}
                />

                <button
                  type="button"
                  className="password-toggle"
                  onClick={() => setShowPassword((prev) => !prev)}
                  disabled={loading}
                  aria-label={
                    showPassword ? "Hide password" : "Show password"
                  }
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

              {formData.password && (
                <div className="password-requirements">
                  <PasswordRule
                    valid={passwordChecks.length}
                    text="8+ characters"
                  />

                  <PasswordRule
                    valid={
                      passwordChecks.uppercase &&
                      passwordChecks.lowercase
                    }
                    text="Upper & lowercase"
                  />

                  <PasswordRule
                    valid={passwordChecks.number}
                    text="One number"
                  />

                  <PasswordRule
                    valid={passwordChecks.special}
                    text="One special character"
                  />

                  <PasswordRule
                    valid={passwordChecks.noSpaces}
                    text="No spaces"
                  />
                </div>
              )}
            </div>

            <div className="form-group">
              <label htmlFor="confirmPassword">
                Confirm password
              </label>

              <div
                className={`input-wrapper ${
                  errors.confirmPassword ? "input-error" : ""
                }`}
              >
                <LockKeyhole size={18} />

                <input
                  id="confirmPassword"
                  name="confirmPassword"
                  type={showConfirmPassword ? "text" : "password"}
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  placeholder="Re-enter your password"
                  autoComplete="new-password"
                  disabled={loading}
                />

                <button
                  type="button"
                  className="password-toggle"
                  onClick={() =>
                    setShowConfirmPassword((prev) => !prev)
                  }
                  disabled={loading}
                  aria-label={
                    showConfirmPassword
                      ? "Hide password"
                      : "Show password"
                  }
                >
                  {showConfirmPassword ? (
                    <EyeOff size={18} />
                  ) : (
                    <Eye size={18} />
                  )}
                </button>
              </div>

              {errors.confirmPassword && (
                <p className="field-error">
                  {errors.confirmPassword}
                </p>
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
                  Creating account...
                </>
              ) : (
                "Create Account"
              )}
            </button>
          </form>

          <div className="register-login-link">
            Already have an account?{" "}
            <button
              type="button"
              onClick={() => navigate("/login")}
            >
              Sign in
            </button>
          </div>

          <div className="security-message">
            <ShieldCheck size={15} />
            <span>Your account is protected by secure authentication</span>
          </div>
        </div>

        <p className="login-copyright">
          © 2026 SandBox. All rights reserved.
        </p>
      </section>
    </main>
  );
}

function PasswordRule({ valid, text }) {
  return (
    <span className={valid ? "password-rule valid" : "password-rule"}>
      <CheckCircle2 size={13} />
      {text}
    </span>
  );
}

export default Register;