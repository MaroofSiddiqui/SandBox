import { Search, UserCircle2 } from "lucide-react";

function Topbar() {
  return (
    <header className="admin-topbar">
      <div className="search-box">
        <Search size={18} />

        <input
          type="text"
          placeholder="Search..."
        />
      </div>

      <div className="admin-profile">
        <UserCircle2 size={34} />

        <div>
          <strong>Super Admin</strong>
          <p>SUPER_ADMIN</p>
        </div>
      </div>
    </header>
  );
}

export default Topbar;