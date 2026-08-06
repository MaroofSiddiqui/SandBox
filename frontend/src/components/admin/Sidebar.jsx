import { NavLink, useNavigate } from "react-router-dom";
import {
  LayoutDashboard,
  Building2,
  Users,
  LogOut,
  ShieldCheck,
  CreditCard,
  ReceiptText,
} from "lucide-react";

function Sidebar() {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    navigate("/login");
  };

  return (
    <aside className="admin-sidebar">

      {/* Logo */}
      <div className="sidebar-logo">
        <ShieldCheck size={28} />
        <span>SandBox ATS</span>
      </div>


      {/* Navigation */}
      <nav className="sidebar-nav">

        <NavLink
          to="/admin/dashboard"
          className="sidebar-link"
        >
          <LayoutDashboard size={20} />
          Dashboard
        </NavLink>


        <NavLink
          to="/admin/organizations"
          className="sidebar-link"
        >
          <Building2 size={20} />
          Organizations
        </NavLink>


        <NavLink
          to="/admin/hrs"
          className="sidebar-link"
        >
          <Users size={20} />
          HR Management
        </NavLink>


        <NavLink
          to="/admin/subscriptions"
          className="sidebar-link"
        >
          <CreditCard size={20} />
          Subscriptions
        </NavLink>


        {/* Payment Monitoring */}
        <NavLink
          to="/admin/payments"
          className="sidebar-link"
        >
          <ReceiptText size={20} />
          Payments
        </NavLink>

      </nav>


      {/* Logout */}
      <button
        className="logout-button"
        onClick={handleLogout}
      >
        <LogOut size={20} />
        Logout
      </button>

    </aside>
  );
}

export default Sidebar;