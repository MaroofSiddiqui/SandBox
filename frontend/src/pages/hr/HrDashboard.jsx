import { useAuth } from "../../context/AuthContext";

function HrDashboard() {
  const { user, logout } = useAuth();

  return (
    <div>
      <h1>HR Dashboard</h1>

      <p>Welcome, {user?.name}</p>
      <p>Email: {user?.email}</p>
      <p>Role: {user?.role}</p>
      <p>Organization ID: {user?.organizationId}</p>

      <button onClick={logout}>Logout</button>
    </div>
  );
}

export default HrDashboard;