// src/App.js
import { Routes, Route, Navigate } from "react-router-dom";

import Login from "./pages/Login";
import Patients from "./pages/Patients";
import Notes from "./pages/Notes";
import Risk from "./pages/Risk";

import AppPage from "./pages/App";
import ProtectedRoute from "./pages/ProtectedRoute";

function App() {
  return (
    <Routes>
      {/* ✅ Default route */}
      <Route path="/" element={<Navigate to="/login" replace />} />

      {/* Public */}
      <Route path="/login" element={<Login />} />

      {/* Protected */}
      <Route element={<ProtectedRoute />}>
        <Route element={<AppPage />}>
          <Route path="/patients" element={<Patients />} />
          <Route path="/notes" element={<Notes />} />
          <Route path="/risk" element={<Risk />} />
        </Route>
      </Route>
    </Routes>
  );
}

export default App;
