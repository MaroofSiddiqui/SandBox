import { NavLink, useNavigate } from "react-router-dom";
import {
  LayoutDashboard,
  Building2,
  Users,
  LogOut,
  ShieldCheck,
} from "lucide-react";

function Sidebar() {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/login");
  };

  return (
    <aside className="admin-sidebar">
      <div className="sidebar-logo">
        <ShieldCheck size={28} />
        <span>SandBox ATS</span>
      </div>

      <nav className="sidebar-nav">
        <NavLink to="/admin/dashboard" className="sidebar-link">
          <LayoutDashboard size={20} />
          Dashboard
        </NavLink>

        <NavLink to="/admin/organizations" className="sidebar-link">
          <Building2 size={20} />
          Organizations
        </NavLink>

        <NavLink to="/admin/hrs" className="sidebar-link">
          <Users size={20} />
          HR Management
        </NavLink>
      </nav>

      <button className="logout-button" onClick={handleLogout}>
        <LogOut size={20} />
        Logout
      </button>
    </aside>
  );
}

export default Sidebar;