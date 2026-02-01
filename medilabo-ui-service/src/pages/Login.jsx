// src/pages/LoginPage.jsx
import { useEffect, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { login, isLoggedIn } from "../auth";

export default function LoginPage() {
  const [username, setUsername] = useState("user");
  const [password, setPassword] = useState("password");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const nav = useNavigate();
  const location = useLocation();

  // ✅ Redirect only in useEffect (never during render)
  // ✅ If user was trying to access a protected page, send them back there
  useEffect(() => {
    if (isLoggedIn()) {
      const from = location.state?.from || "/patients";
      nav(from, { replace: true });
    }
  }, [nav, location.state]);

  const onSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const token = await login(username, password);
      console.log("Logged in, token saved:", token);

      const from = location.state?.from || "/patients";
      nav(from, { replace: true });
    } catch (err) {
      console.error("LOGIN ERROR:", err);

      const status = err?.response?.status;
      if (status === 401) {
        setError("Wrong username/password.");
      } else if (status === 403) {
        setError("Blocked by security/CORS. Check backend config.");
      } else {
        setError("Login failed. Check console/network.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page">
      <div className="card">
        <h2>Login</h2>
        <p className="muted">Authentication is on a separate page.</p>

        <form onSubmit={onSubmit} className="formGrid">
          <label>
            Username
            <input
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              autoComplete="username"
            />
          </label>

          <label>
            Password
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              autoComplete="current-password"
            />
          </label>

          {error && <div className="error">{error}</div>}

          <button className="btn" type="submit" disabled={loading}>
            {loading ? "Signing in..." : "Sign in"}
          </button>
        </form>

        <p className="muted small">JWT login enabled.</p>
      </div>
    </div>
  );
}