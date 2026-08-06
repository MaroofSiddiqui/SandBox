import { useAuth } from "../../context/AuthContext";

function CandidateDashboard() {

  const { user, logout } = useAuth();

  return (
    <div
      style={{
        minHeight: "100vh",
        padding: "40px",
        background: "#f5f7fb"
      }}
    >

      <h1>Candidate Dashboard</h1>

      <p>
        Welcome, {user?.name || "Candidate"}!
      </p>

      <p>
        Email: {user?.email}
      </p>

      <p>
        Role: {user?.role}
      </p>

      <button onClick={logout}>
        Logout
      </button>

    </div>
  );
}

export default CandidateDashboard;