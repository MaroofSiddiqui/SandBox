import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import {
  CheckCircle2,
  Eye,
  EyeOff,
  LockKeyhole,
  ShieldCheck,
} from "lucide-react";

import axiosInstance from "../api/axiosInstance";
import "./ResetPassword.css";

function ResetPassword() {
  const navigate = useNavigate();
  const location = useLocation();

  /*
   * Email received from VerifyOtp.jsx
   */
  const email = location.state?.email || "";

  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] =
    useState(false);

  const [errors, setErrors] = useState({});
  const [serverError, setServerError] = useState("");
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  /*
   * Prevent direct access to this page.
   *
   * User must first complete the forgot-password flow.
   */
  useEffect(() => {
    if (!email) {
      navigate("/forgot-password", {
        replace: true,
      });
    }
  }, [email, navigate]);

  /*
   * Same password rules used during registration.
   */
  const passwordChecks = {
    length: newPassword.length >= 8,
    uppercase: /[A-Z]/.test(newPassword),
    lowercase: /[a-z]/.test(newPassword),
    number: /\d/.test(newPassword),
    special: /[^A-Za-z0-9\s]/.test(newPassword),
    noSpaces: !/\s/.test(newPassword),
  };

  const validateForm = () => {
    const newErrors = {};

    if (!newPassword) {
      newErrors.newPassword =
        "New password is required.";
    } else if (
      !Object.values(passwordChecks).every(Boolean)
    ) {
      newErrors.newPassword =
        "Password does not meet the security requirements.";
    }

    if (!confirmPassword) {
      newErrors.confirmPassword =
        "Please confirm your new password.";
    } else if (newPassword !== confirmPassword) {
      newErrors.confirmPassword =
        "Passwords do not match.";
    }

    setErrors(newErrors);

    return Object.keys(newErrors).length === 0;
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
      await axiosInstance.post(
        "/api/auth/password/reset-password",
        {
          email: email,
          newPassword: newPassword,
        }
      );

      setSuccess(true);
    } catch (err) {
      setServerError(
        err.response?.data?.message ||
          err.response?.data ||
          "Unable to reset your password. Please try again."
      );
    } finally {
      setLoading(false);
    }
  };

  /*
   * SUCCESS SCREEN
   */
  if (success) {
    return (
      <main className="reset-page">
        <div className="reset-card reset-success-card">

          <div className="reset-success-icon">
            <CheckCircle2 size={32} />
          </div>

          <p className="reset-eyebrow">
            PASSWORD UPDATED
          </p>

          <h1>Password reset successfully</h1>

          <p>
            Your password has been changed successfully.
            You can now sign in using your new password.
          </p>

          <button
            type="button"
            className="reset-submit-button"
            onClick={() =>
              navigate("/login", {
                replace: true,
              })
            }
          >
            Continue to Sign In
          </button>

        </div>

        <p className="reset-copyright">
          © 2026 SandBox. All rights reserved.
        </p>
      </main>
    );
  }

  return (
    <main className="reset-page">

      <div className="reset-card">

        {/* ICON */}
        <div className="reset-logo">
          <ShieldCheck size={27} />
        </div>


        {/* HEADING */}
        <div className="reset-heading">

          <p className="reset-eyebrow">
            SECURE YOUR ACCOUNT
          </p>

          <h1>Create a new password</h1>

          <p>
            Choose a strong password that you haven't
            used before.
          </p>

        </div>


        {/* FORM */}
        <form
          onSubmit={handleSubmit}
          noValidate
        >

          {/* NEW PASSWORD */}
          <div className="reset-form-group">

            <label htmlFor="newPassword">
              New password
            </label>

            <div
              className={`reset-input-wrapper ${
                errors.newPassword
                  ? "reset-input-error"
                  : ""
              }`}
            >
              <LockKeyhole size={18} />

              <input
                id="newPassword"
                type={
                  showPassword
                    ? "text"
                    : "password"
                }
                value={newPassword}
                onChange={(e) => {
                  setNewPassword(e.target.value);

                  if (errors.newPassword) {
                    setErrors((prev) => ({
                      ...prev,
                      newPassword: "",
                    }));
                  }

                  setServerError("");
                }}
                placeholder="Enter new password"
                autoComplete="new-password"
                disabled={loading}
              />

              <button
                type="button"
                className="reset-password-toggle"
                onClick={() =>
                  setShowPassword(
                    (prev) => !prev
                  )
                }
                disabled={loading}
                aria-label={
                  showPassword
                    ? "Hide password"
                    : "Show password"
                }
              >
                {showPassword ? (
                  <EyeOff size={18} />
                ) : (
                  <Eye size={18} />
                )}
              </button>
            </div>

            {errors.newPassword && (
              <p className="reset-field-error">
                {errors.newPassword}
              </p>
            )}


            {/* PASSWORD RULES */}
            {newPassword && (
              <div className="reset-password-rules">

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


          {/* CONFIRM PASSWORD */}
          <div className="reset-form-group">

            <label htmlFor="confirmPassword">
              Confirm new password
            </label>

            <div
              className={`reset-input-wrapper ${
                errors.confirmPassword
                  ? "reset-input-error"
                  : ""
              }`}
            >
              <LockKeyhole size={18} />

              <input
                id="confirmPassword"
                type={
                  showConfirmPassword
                    ? "text"
                    : "password"
                }
                value={confirmPassword}
                onChange={(e) => {
                  setConfirmPassword(
                    e.target.value
                  );

                  if (errors.confirmPassword) {
                    setErrors((prev) => ({
                      ...prev,
                      confirmPassword: "",
                    }));
                  }

                  setServerError("");
                }}
                placeholder="Re-enter new password"
                autoComplete="new-password"
                disabled={loading}
              />

              <button
                type="button"
                className="reset-password-toggle"
                onClick={() =>
                  setShowConfirmPassword(
                    (prev) => !prev
                  )
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
              <p className="reset-field-error">
                {errors.confirmPassword}
              </p>
            )}

          </div>


          {/* SERVER ERROR */}
          {serverError && (
            <div
              className="reset-server-error"
              role="alert"
            >
              {serverError}
            </div>
          )}


          {/* SUBMIT */}
          <button
            type="submit"
            className="reset-submit-button"
            disabled={loading}
          >
            {loading ? (
              <>
                <span className="reset-spinner"></span>
                Updating password...
              </>
            ) : (
              "Reset Password"
            )}
          </button>

        </form>


        <div className="reset-security">
          <ShieldCheck size={14} />

          <span>
            Your new password will be securely encrypted
          </span>
        </div>

      </div>


      <p className="reset-copyright">
        © 2026 SandBox. All rights reserved.
      </p>

    </main>
  );
}

function PasswordRule({ valid, text }) {
  return (
    <span
      className={
        valid
          ? "reset-password-rule valid"
          : "reset-password-rule"
      }
    >
      <CheckCircle2 size={13} />
      {text}
    </span>
  );
}

export default ResetPassword;