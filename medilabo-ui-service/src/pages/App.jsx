// src/pages/App.jsx
import { Link, Outlet, useLocation, useNavigate } from "react-router-dom";
import "../App.css";
import { logout } from "../auth";

function TopNav() {
  const navigate = useNavigate();
  const location = useLocation();

  const isActive = (path) => location.pathname === path;

  return (
    <div className="topbar">
      <div className="brand">MediLabo</div>

      <div className="navlinks">
        <Link to="/patients" aria-current={isActive("/patients") ? "page" : undefined}>
          Patients
        </Link>

        <Link to="/notes" aria-current={isActive("/notes") ? "page" : undefined}>
          Notes
        </Link>

        <Link to="/risk" aria-current={isActive("/risk") ? "page" : undefined}>
          Risk
        </Link>

        <button
          className="btn btnSmall"
          type="button"
          onClick={() => {
            logout();
            navigate("/login", { replace: true });
          }}
        >
          Logout
        </button>
      </div>
    </div>
  );
}

export default function AppPage() {
  return (
    <>
      <TopNav />
      <div className="appShell">
        <Outlet />
      </div>
    </>
  );
}
