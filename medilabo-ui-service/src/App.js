import { useEffect, useState } from "react";
import "./App.css";
import api from "./api";

const emptyForm = {
  firstname: "",
  lastname: "",
  gender: "F",
  birthdate: "",
  address: "",
  phone: "",
};

export default function App() {
  const [patients, setPatients] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    loadPatients();
  }, []);

  async function loadPatients() {
    try {
      const res = await api.get("/api/patients");
      setPatients(res.data);
    } catch (e) {
      setError(errMsg(e));
    }
  }

  function onChange(e) {
    setForm((f) => ({ ...f, [e.target.name]: e.target.value }));
  }

  async function onSubmit(e) {
    e.preventDefault();
    setError("");

    if (!form.firstname.trim() || !form.lastname.trim() || !form.gender || !form.birthdate.trim()) {
      setError("Please fill Firstname, Lastname, Gender, Birthdate.");
      return;
    }

    const payload = {
      ...form,
      firstname: form.firstname.trim(),
      lastname: form.lastname.trim(),
      address: form.address?.trim() || null,
      phone: form.phone?.trim() || null,
    };

    try {
      if (editingId) {
        await api.put(`/api/patients/${editingId}`, payload);
      } else {
        await api.post("/api/patients", payload);
      }
      setForm(emptyForm);
      setEditingId(null);
      await loadPatients();
    } catch (e) {
      setError(errMsg(e));
    }
  }

  function startEdit(p) {
    setEditingId(p.id);
    setForm({
      firstname: p.firstname ?? "",
      lastname: p.lastname ?? "",
      gender: p.gender ?? "F",
      birthdate: p.birthdate ?? "",
      address: p.address ?? "",
      phone: p.phone ?? "",
    });
  }

  async function remove(id) {
    if (!window.confirm("Delete this patient?")) return;
    setError("");
    try {
      await api.delete(`/api/patients/${id}`);
      if (editingId === id) {
        setEditingId(null);
        setForm(emptyForm);
      }
      await loadPatients();
    } catch (e) {
      setError(errMsg(e));
    }
  }

  return (
    <div className="container">
      <h1>MediLabo – Patient Management</h1>

      <div className="card">
        <h2>{editingId ? `Edit Patient #${editingId}` : "Create Patient"}</h2>
        {error && <p className="error">{error}</p>}

        <form onSubmit={onSubmit}>
          <input name="firstname" placeholder="First name *" value={form.firstname} onChange={onChange} />
          <input name="lastname" placeholder="Last name *" value={form.lastname} onChange={onChange} />

          <select name="gender" value={form.gender} onChange={onChange}>
            <option value="F">Female (F)</option>
            <option value="M">Male (M)</option>
          </select>

          <input
            name="birthdate"
            placeholder="Birthdate * (YYYY-MM-DD)"
            value={form.birthdate}
            onChange={onChange}
          />
          <input name="address" placeholder="Address (optional)" value={form.address} onChange={onChange} />
          <input name="phone" placeholder="Phone (optional)" value={form.phone} onChange={onChange} />

          <div className="actions">
            <button type="submit">{editingId ? "Update" : "Create"}</button>

            {editingId && (
              <button
                type="button"
                onClick={() => {
                  setEditingId(null);
                  setForm(emptyForm);
                  setError("");
                }}
              >
                Cancel
              </button>
            )}
          </div>
        </form>
      </div>

      <div className="card">
        <div className="row">
          <h2>Patients</h2>
          <button type="button" onClick={loadPatients}>
            Refresh
          </button>
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
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {patients.map((p) => (
                <tr key={p.id}>
                  <td>{p.id}</td>
                  <td>{p.firstname}</td>
                  <td>{p.lastname}</td>
                  <td>{p.gender}</td>
                  <td>{p.birthdate}</td>
                  <td>
                    <button onClick={() => startEdit(p)}>Edit</button>{" "}
                    <button onClick={() => remove(p.id)}>Delete</button>
                  </td>
                </tr>
              ))}
              {patients.length === 0 && (
                <tr>
                  <td colSpan="6">No patients found.</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

function errMsg(e) {
  if (e?.response) return `API ${e.response.status}: ${JSON.stringify(e.response.data)}`;
  return e?.message || "Something went wrong";
}