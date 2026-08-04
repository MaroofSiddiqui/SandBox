// Icons used in the sidebar
import {
    LayoutDashboard,
    Building2,
    Users,
    ShieldCheck,
    Briefcase,
    CalendarCheck,
    ClipboardList,
    BarChart3,
    LogOut
} from "lucide-react";

// Used for navigation
import { NavLink } from "react-router-dom";

// Used to logout
import { useAuth } from "../context/AuthContext";

// CSS
import "../styles/sidebar.css";

function Sidebar() {

    // Logout function comes from AuthContext
    const { logout } = useAuth();

    return (

        <aside className="sidebar">

            {/* Project Logo */}
            <div className="sidebar-logo">

                <h2>SandBox</h2>

            </div>

            {/* Navigation Menu */}
            <nav>

                <NavLink to="/hr">

                    <LayoutDashboard size={20} />

                    Dashboard

                </NavLink>

                <NavLink to="/hr/candidates">

                    <Users size={20} />

                    Candidates

                </NavLink>

                <NavLink to="/hr/jobs">

                    <Briefcase size={20} />

                    Jobs

                </NavLink>

                <NavLink to="/hr/interviews">

                    <CalendarCheck size={20} />

                    Interviews

                </NavLink>

                <NavLink to="/hr/assessments">

                    <ClipboardList size={20} />

                    Assessments

                </NavLink>

                <NavLink to="/hr/reports">

                    <BarChart3 size={20} />

                    Reports

                </NavLink>

            </nav>

            {/* Logout Button */}
            <button
                className="logout-btn"
                onClick={logout}
            >

                <LogOut size={18} />

                Logout

            </button>

        </aside>

    );

}

export default Sidebar;