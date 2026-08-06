import { useAuth } from "../../context/AuthContext";

function SuperAdminDashboard() {
  const { user, logout } = useAuth();

  return (
    <div>
      <h1>Super Admin Dashboard</h1>

      <p>Welcome, {user?.name}</p>
      <p>Email: {user?.email}</p>
      <p>Role: {user?.role}</p>

      <button onClick={logout}>Logout</button>
    </div>
  );
}

export default SuperAdminDashboard;