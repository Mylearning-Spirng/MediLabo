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

  // Pagination state
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const sortedPatients = useMemo(() => {
    return [...patients].sort((a, b) => (a.id ?? 0) - (b.id ?? 0));
  }, [patients]);

  async function loadPatients(currentPage = page, currentSize = pageSize) {
    setError("");
    setLoading(true);
    try {
      const res = await api.get("/api/patients/paged", {
        params: {
          page: currentPage,
          size: currentSize,
          sortBy: "id",
          sortDir: "asc"
        }
      });
      setPatients(res.data.content || []);
      setTotalPages(res.data.totalPages || 0);
      setTotalElements(res.data.totalElements || 0);
    } catch (e) {
      setError("Failed to load patients.");
      console.error(e);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadPatients();
  }, [page, pageSize]);

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
      await loadPatients(page, pageSize);
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
      await loadPatients(page, pageSize);
    } catch (e) {
      setError("Delete failed.");
      console.error(e);
    }
  }

  function handlePageSizeChange(newSize) {
    setPageSize(newSize);
    setPage(0); // Reset to first page when changing page size
  }

  function handlePrevPage() {
    if (page > 0) {
      setPage(page - 1);
    }
  }

  function handleNextPage() {
    if (page < totalPages - 1) {
      setPage(page + 1);
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
            </div>
          </form>
        </div>

        <div className="card">
          <div className="row spaceBetween">
            <h2>Patients</h2>
            {loading && <span className="pill">Loading…</span>}
          </div>

          {/* Pagination Controls - Top */}
          <div className="row spaceBetween" style={{ marginBottom: 12 }}>
            <label style={{ display: "flex", alignItems: "center", gap: 8 }}>
              Page size
              <select
                value={pageSize}
                onChange={(e) => handlePageSizeChange(Number(e.target.value))}
                style={{ width: 70 }}
              >
                <option value={5}>5</option>
                <option value={10}>10</option>
                <option value={20}>20</option>
                <option value={50}>50</option>
              </select>
            </label>
            <span className="muted small">
              Page {totalPages > 0 ? page + 1 : 0} of {totalPages} — {totalElements} items
            </span>
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

          {/* Pagination Controls - Bottom */}
          <div className="row spaceBetween" style={{ marginTop: 12 }}>
            <div className="row">
              <button
                className="btn"
                onClick={handlePrevPage}
                disabled={page === 0}
              >
                Prev
              </button>
              <button
                className="btn"
                onClick={handleNextPage}
                disabled={page >= totalPages - 1}
              >
                Next
              </button>
            </div>
            <button className="btn btnGhost" onClick={() => loadPatients(page, pageSize)}>
              Refresh
            </button>
          </div>

          <p className="muted small" style={{ marginTop: 12 }}>
            Tip: Go to the <b>Risk</b> tab to fetch diabetes risk for a patient ID.
          </p>
        </div>
      </div>
    </div>
  );
}
