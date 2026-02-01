import api from "./api";

const TOKEN_KEY = "token";

export function saveToken(token) {
  localStorage.setItem(TOKEN_KEY, token);
}

export function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

export function isLoggedIn() {
  return !!getToken();
}

export function logout() {
  localStorage.removeItem(TOKEN_KEY);
}

// ✅ ADD THIS
export async function login(username, password) {
  const res = await api.post("/api/auth/login", {
    username,
    password,
  });

  saveToken(res.data.token);
  return res.data.token;
}
