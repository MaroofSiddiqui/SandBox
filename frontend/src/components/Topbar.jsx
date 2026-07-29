// React hook for reading logged-in user information
import { useAuth } from "../context/AuthContext";

// Icons
import {
    Bell,
    UserCircle
} from "lucide-react";

// CSS
import "../styles/topbar.css";

function Topbar() {

    // Logged in user information comes from AuthContext
    const { user } = useAuth();

    return (

        <header className="topbar">

            {/* Left Side */}
            <div>

                <h1>HR Dashboard</h1>

                <p>
                    Manage candidates, jobs and interviews
                </p>

            </div>

            {/* Right Side */}
            <div className="topbar-right">

                {/* Notification Icon */}
                <button className="notification-btn">

                    <Bell size={20} />

                </button>

                {/* Logged in User */}
                <div className="user-box">

                    <UserCircle size={42} />

                    <div>

                        <h4>{user?.name}</h4>

                        <p>{user?.role}</p>

                    </div>

                </div>

            </div>

        </header>

    );

}

export default Topbar;