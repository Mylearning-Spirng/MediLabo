import { useEffect, useMemo, useState } from "react";
import api from "../api";

const emptyForm = {
  firstname: "",
  lastname: "",
  gender: "F",
  birthdate: "",
  address: "",
  phone: "",
};

export default function PatientsPage() {
  const [patients, setPatients] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const sortedPatients = useMemo(() => {
    return [...patients].sort((a, b) => (a.id ?? 0) - (b.id ?? 0));
  }, [patients]);

  async function loadPatients() {
    setError("");
    setLoading(true);
    try {
      const res = await api.get("/api/patients");
      setPatients(res.data || []);
    } catch (e) {
      setError("Failed to load patients.");
      console.error(e);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadPatients();
  }, []);

  function onChange(key, value) {
    setForm((p) => ({ ...p, [key]: value }));
  }

  function startEdit(p) {
    setEditingId(p.id);
    setForm({
      firstname: p.firstname ?? "",
      lastname: p.lastname ?? "",
      gender: p.gender ?? "F",
      birthdate: (p.birthdate ?? "").slice(0, 10),
      address: p.address ?? "",
      phone: p.phone ?? "",
    });
  }

  function resetForm() {
    setEditingId(null);
    setForm(emptyForm);
  }

  async function onSubmit(e) {
    e.preventDefault();
    setError("");

    const payload = {
      ...form,
      // ensure backend receives ISO date if needed
      birthdate: form.birthdate || null,
    };

    try {
      if (editingId) {
        await api.put(`/api/patients/${editingId}`, payload);
      } else {
        await api.post("/api/patients", payload);
      }
      resetForm();
      await loadPatients();
    } catch (e2) {
      setError(editingId ? "Update failed." : "Create failed.");
      console.error(e2);
    }
  }

  async function onDelete(id) {
    if (!window.confirm("Delete this patient?")) return;
    setError("");
    try {
      await api.delete(`/api/patients/${id}`);
      await loadPatients();
    } catch (e) {
      setError("Delete failed.");
      console.error(e);
    }
  }

  return (
    <div className="page">
      <div className="grid2">
        <div className="card">
          <h2>{editingId ? `Edit Patient #${editingId}` : "Add Patient"}</h2>

          <form onSubmit={onSubmit} className="formGrid">
            <label>
              First name
              <input value={form.firstname} onChange={(e) => onChange("firstname", e.target.value)} />
            </label>

            <label>
              Last name
              <input value={form.lastname} onChange={(e) => onChange("lastname", e.target.value)} />
            </label>

            <label>
              Gender
              <select value={form.gender} onChange={(e) => onChange("gender", e.target.value)}>
                <option value="F">F</option>
                <option value="M">M</option>
              </select>
            </label>

            <label>
              Birthdate
              <input
                type="date"
                value={form.birthdate}
                onChange={(e) => onChange("birthdate", e.target.value)}
              />
            </label>

            <label>
              Address
              <input value={form.address} onChange={(e) => onChange("address", e.target.value)} />
            </label>

            <label>
              Phone
              <input value={form.phone} onChange={(e) => onChange("phone", e.target.value)} />
            </label>

            {error && <div className="error">{error}</div>}

            <div className="row">
              <button className="btn" type="submit">
                {editingId ? "Update" : "Create"}
              </button>
              <button className="btn btnGhost" type="button" onClick={resetForm}>
                Clear
              </button>
              <button className="btn btnGhost" type="button" onClick={loadPatients}>
                Refresh
              </button>
            </div>
          </form>
        </div>

        <div className="card">
          <div className="row spaceBetween">
            <h2>Patients</h2>
            {loading && <span className="pill">Loading…</span>}
          </div>

          <div className="tableWrap">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>First</th>
                  <th>Last</th>
                  <th>Gender</th>
                  <th>Birthdate</th>
                  <th>Phone</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {sortedPatients.map((p) => (
                  <tr key={p.id}>
                    <td>{p.id}</td>
                    <td>{p.firstname}</td>
                    <td>{p.lastname}</td>
                    <td>{p.gender}</td>
                    <td>{(p.birthdate ?? "").slice(0, 10)}</td>
                    <td>{p.phone}</td>
                    <td className="actions">
                      <button className="btn btnSmall" onClick={() => startEdit(p)}>
                        Edit
                      </button>
                      <button className="btn btnSmall btnDanger" onClick={() => onDelete(p.id)}>
                        Delete
                      </button>
                    </td>
                  </tr>
                ))}
                {sortedPatients.length === 0 && !loading && (
                  <tr>
                    <td colSpan="7" className="muted">
                      No patients yet.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>

          <p className="muted small">
            Tip: Go to the <b>Risk</b> tab to fetch diabetes risk for a patient ID.
          </p>
        </div>
      </div>
    </div>
  );
}
