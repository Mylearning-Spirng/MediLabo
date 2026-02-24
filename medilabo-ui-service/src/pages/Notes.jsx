import { useMemo, useState } from "react";
import api from "../api";

const emptyForm = {
  patientId: "",
  patientLastName: "",
  note: "",
};

export default function Notes() {
  const [notes, setNotes] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);

  const sortedNotes = useMemo(() => {
    return [...notes].sort((a, b) => String(a.id ?? "").localeCompare(String(b.id ?? "")));
  }, [notes]);

  function onChange(key, value) {
    setForm((p) => ({ ...p, [key]: value }));
  }

  async function fetchNotes() {
    setError("");

    if (!form.patientId) {
      setError("Patient ID is required to fetch notes.");
      return;
    }

    setLoading(true);
    try {
      const res = await api.get(`/api/notes/patient/${form.patientId}`);
      setNotes(res.data || []);
    } catch (e) {
      setNotes([]);
      setError("Failed to load notes for this patient.");
      console.error(e);
    } finally {
      setLoading(false);
    }
  }

  async function addNote(e) {
    e.preventDefault();
    setError("");

    if (!form.patientId) {
      setError("Patient ID is required.");
      return;
    }
    if (!form.note.trim()) {
      setError("Note text is required.");
      return;
    }

    const payload = {
      patientId: Number(form.patientId),
      patientLastName: form.patientLastName?.trim() || null,
      note: form.note,
    };

    setSaving(true);
    try {
      await api.post("/api/notes", payload);

      // Clear only the note text after saving (keep patientId for convenience)
      setForm((p) => ({ ...p, note: "" }));

      // Refresh list for that patient
      await fetchNotes();
    } catch (e2) {
      setError("Create note failed.");
      console.error(e2);
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="page">
      <div className="grid2">
        {/* Left: Fetch + Add */}
        <div className="card">
          <h2>Patient Notes</h2>
          <p className="muted small">
            This service supports: <b>GET /api/notes/patient/&lt;id&gt;</b> and <b>POST /api/notes</b>.
          </p>

          <div className="formGrid" style={{ marginTop: 12 }}>
            <label>
              Patient ID
              <input
                value={form.patientId}
                onChange={(e) => onChange("patientId", e.target.value)}
                placeholder="e.g. 1"
              />
            </label>

            <label>
              Patient Last Name (optional)
              <input
                value={form.patientLastName}
                onChange={(e) => onChange("patientLastName", e.target.value)}
                placeholder="e.g. Smith"
              />
            </label>

            <div className="row">
              <button className="btn" type="button" onClick={fetchNotes} disabled={loading}>
                {loading ? "Loading..." : "Fetch Notes"}
              </button>

              <button
                className="btn btnGhost"
                type="button"
                onClick={() => {
                  setNotes([]);
                  setError("");
                  setForm(emptyForm);
                }}
              >
                Clear
              </button>
            </div>
          </div>

          <hr style={{ margin: "16px 0" }} />

          <h3 style={{ marginBottom: 8 }}>Add a Note</h3>

          <form onSubmit={addNote} className="formGrid">
            <label>
              Note (formatting preserved)
              <textarea
                rows={7}
                value={form.note}
                onChange={(e) => onChange("note", e.target.value)}
                placeholder="Type the note text here…"
              />
            </label>

            {error && <div className="error">{error}</div>}

            <button className="btn" type="submit" disabled={saving}>
              {saving ? "Saving..." : "Add Note"}
            </button>
          </form>
        </div>

        {/* Right: Notes list */}
        <div className="card">
          <div className="row spaceBetween">
            <h2>Notes List</h2>
            {loading && <span className="pill">Loading…</span>}
          </div>

          <div className="tableWrap">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Patient</th>
                  <th>Last Name</th>
                  <th>Note</th>
                </tr>
              </thead>
              <tbody>
                {sortedNotes.map((n) => (
                  <tr key={n.id}>
                    <td>{n.id}</td>
                    <td>{n.patientId}</td>
                    <td>{n.patientLastName}</td>
                    <td style={{ whiteSpace: "pre-wrap" }}>{n.note}</td>
                  </tr>
                ))}

                {sortedNotes.length === 0 && !loading && (
                  <tr>
                    <td colSpan="4" className="muted">
                      No notes loaded. Enter a Patient ID and click <b>Fetch Notes</b>.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>

          <p className="muted small">
            Tip: Add a note with symptoms keywords (e.g., “smoker”, “cholesterol”, “dizziness”) to affect risk score.
          </p>
        </div>
      </div>
    </div>
  );
}
