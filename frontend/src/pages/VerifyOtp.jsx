import { useEffect, useRef, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import {
  ArrowLeft,
  KeyRound,
  Mail,
  ShieldCheck,
} from "lucide-react";

import axiosInstance from "../api/axiosInstance";
import "./VerifyOtp.css";

function VerifyOtp() {
  const navigate = useNavigate();
  const location = useLocation();

  /*
   * Email is received from ForgotPassword.jsx:
   *
   * navigate("/verify-otp", {
   *   state: { email }
   * });
   */
  const email = location.state?.email || "";

  const [otp, setOtp] = useState([
    "",
    "",
    "",
    "",
    "",
    "",
  ]);

  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [loading, setLoading] = useState(false);
  const [resending, setResending] = useState(false);

  const inputRefs = useRef([]);

  /*
   * If somebody manually opens /verify-otp without
   * first entering their email, send them back.
   */
  useEffect(() => {
    if (!email) {
      navigate("/forgot-password", {
        replace: true,
      });
    }
  }, [email, navigate]);

  /*
   * Update individual OTP box.
   */
  const handleChange = (index, value) => {
    // Allow numbers only
    const digit = value.replace(/\D/g, "");

    if (!digit) {
      const updatedOtp = [...otp];
      updatedOtp[index] = "";

      setOtp(updatedOtp);
      setError("");
      return;
    }

    const updatedOtp = [...otp];

    // Take last entered digit
    updatedOtp[index] = digit.slice(-1);

    setOtp(updatedOtp);
    setError("");
    setSuccessMessage("");

    // Automatically move to next box
    if (index < 5) {
      inputRefs.current[index + 1]?.focus();
    }
  };

  /*
   * Handle Backspace navigation.
   */
  const handleKeyDown = (index, e) => {
    if (
      e.key === "Backspace" &&
      !otp[index] &&
      index > 0
    ) {
      inputRefs.current[index - 1]?.focus();
    }
  };

  /*
   * Allow user to paste complete OTP.
   *
   * Example:
   * 123456
   */
  const handlePaste = (e) => {
    e.preventDefault();

    const pastedValue = e.clipboardData
      .getData("text")
      .replace(/\D/g, "")
      .slice(0, 6);

    if (!pastedValue) {
      return;
    }

    const updatedOtp = [
      "",
      "",
      "",
      "",
      "",
      "",
    ];

    pastedValue.split("").forEach((digit, index) => {
      updatedOtp[index] = digit;
    });

    setOtp(updatedOtp);
    setError("");

    const nextIndex = Math.min(
      pastedValue.length,
      5
    );

    inputRefs.current[nextIndex]?.focus();
  };

  /*
   * VERIFY OTP
   */
  const handleSubmit = async (e) => {
    e.preventDefault();

    if (loading) return;

    const otpValue = otp.join("");

    if (otpValue.length !== 6) {
      setError(
        "Please enter the complete 6-digit verification code."
      );
      return;
    }

    setLoading(true);
    setError("");
    setSuccessMessage("");

    try {
      await axiosInstance.post(
        "/api/auth/password/verify-otp",
        {
          email: email,
          otp: otpValue,
        }
      );

      /*
       * OTP verified.
       *
       * Pass email + OTP to Reset Password page.
       *
       * This allows the reset-password request to use
       * the information required by the backend.
       */
      navigate("/reset-password", {
        state: {
          email: email,
          otp: otpValue,
        },
      });
    } catch (err) {
      setError(
        err.response?.data?.message ||
          err.response?.data ||
          "Invalid or expired verification code."
      );
    } finally {
      setLoading(false);
    }
  };

  /*
   * RESEND OTP
   *
   * Reuses existing forgot-password endpoint.
   */
  const handleResendOtp = async () => {
    if (resending || loading) return;

    setResending(true);
    setError("");
    setSuccessMessage("");

    try {
      await axiosInstance.post(
        "/api/auth/password/forgot",
        {
          email: email,
        }
      );

      setOtp([
        "",
        "",
        "",
        "",
        "",
        "",
      ]);

      setSuccessMessage(
        "A new verification code has been sent to your email."
      );

      inputRefs.current[0]?.focus();
    } catch (err) {
      setError(
        err.response?.data?.message ||
          err.response?.data ||
          "Unable to resend verification code."
      );
    } finally {
      setResending(false);
    }
  };

  return (
    <main className="otp-page">

      <div className="otp-card">

        {/* ICON */}
        <div className="otp-logo">
          <KeyRound size={27} />
        </div>


        {/* HEADING */}
        <div className="otp-heading">

          <p className="otp-eyebrow">
            VERIFY YOUR IDENTITY
          </p>

          <h1>Enter verification code</h1>

          <p>
            We've sent a 6-digit verification code to
          </p>

          <div className="otp-email">
            <Mail size={14} />

            <span>
              {email}
            </span>
          </div>

        </div>


        {/* OTP FORM */}
        <form
          onSubmit={handleSubmit}
          noValidate
        >

          <div className="otp-input-container">

            {otp.map((digit, index) => (
              <input
                key={index}
                ref={(element) => {
                  inputRefs.current[index] =
                    element;
                }}
                className={`otp-input ${
                  error ? "otp-input-error" : ""
                }`}
                type="text"
                inputMode="numeric"
                autoComplete={
                  index === 0
                    ? "one-time-code"
                    : "off"
                }
                maxLength={1}
                value={digit}
                onChange={(e) =>
                  handleChange(
                    index,
                    e.target.value
                  )
                }
                onKeyDown={(e) =>
                  handleKeyDown(index, e)
                }
                onPaste={handlePaste}
                disabled={loading}
                aria-label={`OTP digit ${
                  index + 1
                }`}
              />
            ))}

          </div>


          {/* ERROR */}
          {error && (
            <div
              className="otp-error"
              role="alert"
            >
              {error}
            </div>
          )}


          {/* SUCCESS */}
          {successMessage && (
            <div className="otp-success">
              {successMessage}
            </div>
          )}


          {/* VERIFY BUTTON */}
          <button
            type="submit"
            className="otp-submit-button"
            disabled={loading || resending}
          >
            {loading ? (
              <>
                <span className="otp-spinner"></span>
                Verifying...
              </>
            ) : (
              "Verify code"
            )}
          </button>

        </form>


        {/* RESEND */}
        <div className="otp-resend">

          <span>
            Didn't receive the code?
          </span>

          <button
            type="button"
            onClick={handleResendOtp}
            disabled={resending || loading}
          >
            {resending
              ? "Sending..."
              : "Resend code"}
          </button>

        </div>


        {/* BACK */}
        <button
          type="button"
          className="otp-back-button"
          onClick={() =>
            navigate("/forgot-password")
          }
          disabled={loading || resending}
        >
          <ArrowLeft size={16} />
          Change email
        </button>


        {/* SECURITY */}
        <div className="otp-security">
          <ShieldCheck size={14} />

          <span>
            Never share your verification code with anyone
          </span>
        </div>

      </div>


      <p className="otp-copyright">
        © 2026 SandBox. All rights reserved.
      </p>

    </main>
  );
}

export default VerifyOtp;