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

const emptyNoteForm = { note: "" };

export default function App() {
  // ---------------- Patients ----------------
  const [patients, setPatients] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState("");

  // ---------------- Notes ----------------
  const [selectedPatient, setSelectedPatient] = useState(null);
  const [notes, setNotes] = useState([]);
  const [noteForm, setNoteForm] = useState(emptyNoteForm);
  const [editingNoteId, setEditingNoteId] = useState(null); // kept for future (when you add PUT)
  const [notesError, setNotesError] = useState("");

  useEffect(() => {
    loadPatients();
  }, []);

  useEffect(() => {
    if (!selectedPatient?.id) {
      setNotes([]);
      setNoteForm(emptyNoteForm);
      setEditingNoteId(null);
      setNotesError("");
      return;
    }
    loadNotes(selectedPatient.id);
  }, [selectedPatient]);

  async function loadPatients() {
    try {
      const res = await api.get("/api/patients");
      setPatients(res.data);
    } catch (e) {
      setError(errMsg(e));
    }
  }

  async function loadNotes(patientId) {
    try {
      setNotesError("");
      const res = await api.get(`/api/notes/patient/${patientId}`); // ✅ matches your controller
      setNotes(res.data);
    } catch (e) {
      setNotesError(errMsg(e));
    }
  }

  function onChange(e) {
    setForm((f) => ({ ...f, [e.target.name]: e.target.value }));
  }

  function onNoteChange(e) {
    setNoteForm((n) => ({ ...n, [e.target.name]: e.target.value }));
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

  async function onSubmitNote(e) {
    e.preventDefault();
    setNotesError("");

    if (!selectedPatient?.id) {
      setNotesError("Please select a patient first.");
      return;
    }

    if (!noteForm.note.trim()) {
      setNotesError("Note cannot be empty.");
      return;
    }

    // ✅ matches CreateNoteRequest in your controller
    const payload = {
      patientId: selectedPatient.id,
      patientLastName: selectedPatient.lastname || null, // optional
      note: noteForm.note, // keep formatting; don't trim internal new lines
    };

    try {
      await api.post("/api/notes", payload); // ✅ matches your controller
      setNoteForm(emptyNoteForm);
      setEditingNoteId(null);
      await loadNotes(selectedPatient.id);
    } catch (e) {
      setNotesError(errMsg(e));
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

  function selectPatient(p) {
    setSelectedPatient(p);
  }

  async function remove(id) {
    if (!window.confirm("Delete this patient?")) return;
    setError("");
    try {
      await api.delete(`/api/patients/${id}`);

      if (selectedPatient?.id === id) {
        setSelectedPatient(null);
        setNotes([]);
        setNoteForm(emptyNoteForm);
        setEditingNoteId(null);
      }

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

      {/* Patient Create/Edit */}
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

      {/* Patients + Notes */}
      <div className="grid2">
        {/* Patients List */}
        <div className="card">
          <div className="row">
            <h2>Patients</h2>
            <button type="button" onClick={loadPatients}>
              Refresh
            </button>
          </div>

          <p className="muted">
            Tip: Click <b>Select</b> to view/add notes for that patient.
          </p>

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
                  <tr key={p.id} className={selectedPatient?.id === p.id ? "selected" : ""}>
                    <td>{p.id}</td>
                    <td>{p.firstname}</td>
                    <td>{p.lastname}</td>
                    <td>{p.gender}</td>
                    <td>{p.birthdate}</td>
                    <td>
                      <button type="button" onClick={() => selectPatient(p)}>
                        Select
                      </button>
                      <button type="button" onClick={() => startEdit(p)}>
                        Edit
                      </button>
                      <button type="button" onClick={() => remove(p.id)}>
                        Delete
                      </button>
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

        {/* Notes Panel (View + Add) */}
        <div className="card">
          <div className="row">
            <h2>Notes</h2>
            <button
              type="button"
              onClick={() => selectedPatient?.id && loadNotes(selectedPatient.id)}
              disabled={!selectedPatient?.id}
            >
              Refresh
            </button>
          </div>

          {selectedPatient ? (
            <p>
              Selected:{" "}
              <span className="badge">
                #{selectedPatient.id} {selectedPatient.firstname} {selectedPatient.lastname}
              </span>
            </p>
          ) : (
            <p className="muted">Select a patient from the left to view/add notes.</p>
          )}

          {notesError && <p className="error">{notesError}</p>}

          <form onSubmit={onSubmitNote}>
            <textarea
              name="note"
              placeholder="Write a medical note (formatting preserved)..."
              value={noteForm.note}
              onChange={onNoteChange}
              disabled={!selectedPatient?.id}
            />

            <div className="actions">
              <button type="submit" disabled={!selectedPatient?.id}>
                Add Note
              </button>
              <button
                type="button"
                onClick={() => {
                  setNoteForm(emptyNoteForm);
                  setNotesError("");
                }}
                disabled={!selectedPatient?.id}
              >
                Clear
              </button>
            </div>
          </form>

          <div className="noteList">
            {notes.map((n) => (
              <div key={n.id} className="noteItem">
                <div className="noteMeta">
                  <span>Note ID: {n.id}</span>
                  {n.createdAt ? <span>{String(n.createdAt)}</span> : null}
                </div>
                <div className="noteText">{n.note}</div>
              </div>
            ))}

            {selectedPatient?.id && notes.length === 0 && <div className="muted">No notes found for this patient.</div>}
          </div>
        </div>
      </div>
    </div>
  );
}

function errMsg(e) {
  if (e?.response) return `API ${e.response.status}: ${JSON.stringify(e.response.data)}`;
  return e?.message || "Something went wrong";
}
