import { useEffect, useRef, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import axiosInstance from "../../api/axiosInstance";

function VerifyEmail() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  /*
   * Prevent duplicate verification requests.
   *
   * React StrictMode may execute effects more than once
   * during development.
   */
  const verificationStarted = useRef(false);

  // Get verification token from URL
  const token = searchParams.get("token");

  /*
   * Possible status values:
   *
   * verifying
   * success
   * error
   */
  const [status, setStatus] = useState("verifying");

  const [message, setMessage] = useState(
    "Please wait while we verify your email..."
  );

  /*
   * VERIFY EMAIL
   *
   * Example URL:
   * http://localhost:5173/verify-email?token=abc123
   *
   * The token is sent to the backend:
   * GET /api/auth/email/verify?token=abc123
   */
  useEffect(() => {

    /*
     * Prevent duplicate request.
     */
    if (verificationStarted.current) {
      return;
    }

    verificationStarted.current = true;

    /*
     * Token must exist in URL.
     */
    if (!token) {
      queueMicrotask(() => {
        setStatus("error");
        setMessage("Verification token is missing.");
      });
      return;
    }

    /*
     * Call backend verification API.
     */
    const verifyEmail = async () => {
      try {
        const response = await axiosInstance.get(
          "/api/auth/email/verify",
          {
            params: {
              token: token
            }
          }
        );

        /*
         * Verification successful.
         */
        setStatus("success");
        setMessage(
          response.data || "Email verified successfully."
        );

      } catch (error) {
        console.error("Email verification failed:", error);

        /*
         * Verification failed.
         */
        setStatus("error");
        setMessage(
          error.response?.data?.message ||
            error.response?.data ||
            "Unable to verify your email."
        );
      }
    };

    verifyEmail();

  }, [token]);

  /*
   * Navigate back to login page.
   */
  const goToLogin = () => {
    navigate("/login");
  };

  return (
    <div
      style={{
        minHeight: "100vh",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        background: "#f5f7fb"
      }}
    >
      <div
        style={{
          width: "420px",
          padding: "40px",
          background: "white",
          borderRadius: "12px",
          textAlign: "center",
          boxShadow: "0 10px 30px rgba(0,0,0,0.08)"
        }}
      >
        {/* VERIFYING */}
        {status === "verifying" && (
          <>
            <h2>Verifying Email</h2>
            <p>{message}</p>
          </>
        )}

        {/* SUCCESS */}
        {status === "success" && (
          <>
            <h2>Email Verified!</h2>
            <p>{message}</p>
            <button onClick={goToLogin}>Continue to Login</button>
          </>
        )}

        {/* ERROR */}
        {status === "error" && (
          <>
            <h2>Verification Failed</h2>
            <p>{message}</p>
            <button onClick={goToLogin}>Back to Login</button>
          </>
        )}
      </div>
    </div>
  );
}

export default VerifyEmail;