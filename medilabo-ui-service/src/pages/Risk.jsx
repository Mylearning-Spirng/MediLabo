import { useEffect, useState } from "react";
import api from "../api";

export default function RiskPage() {
  const [patients, setPatients] = useState([]);
  const [patientId, setPatientId] = useState("");
  const [risk, setRisk] = useState(null);
  const [error, setError] = useState("");
  const [loadingPatients, setLoadingPatients] = useState(false);
  const [loadingRisk, setLoadingRisk] = useState(false);

  async function loadPatients() {
    setLoadingPatients(true);
    setError("");
    try {
      const res = await api.get("/api/patients");
      const list = res.data || [];
      setPatients(list);
      if (list.length > 0 && !patientId) {
        setPatientId(String(list[0].id));
      }
    } catch (e) {
      setError("Failed to load patients for Risk page.");
      console.error(e);
    } finally {
      setLoadingPatients(false);
    }
  }

  useEffect(() => {
    loadPatients();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  async function fetchRisk() {
    if (!patientId) {
      setError("Please select a patient.");
      return;
    }
    setLoadingRisk(true);
    setError("");
    setRisk(null);

    try {
      const res = await api.get(`/api/risk/${patientId}`);

      const data = res.data || {};
      const riskValue =
        data.riskLevel ?? data.risk ?? data.level ?? (typeof data === "string" ? data : null);

      if (!riskValue) {
        setRisk({ display: "Unknown (check response)", raw: data });
      } else {
        setRisk({ display: String(riskValue), raw: data });
      }
    } catch (e) {
      setError("Failed to fetch risk. Check gateway route + risk service.");
      console.error(e);
    } finally {
      setLoadingRisk(false);
    }
  }

  const selected = patients.find((p) => String(p.id) === String(patientId));

  return (
    <div className="page">
      <div className="card">
        <h2>Diabetes Risk Report</h2>
        <p className="muted">
          Select a patient and fetch the risk calculated by your Risk microservice.
        </p>

        <div className="row">
          <label className="grow">
            Patient
            <select
              value={patientId}
              onChange={(e) => setPatientId(e.target.value)}
              disabled={loadingPatients}
            >
              {patients.map((p) => (
                <option key={p.id} value={p.id}>
                  #{p.id} — {p.firstname} {p.lastname}
                </option>
              ))}
            </select>
          </label>

          <button className="btn" onClick={fetchRisk} disabled={loadingRisk || !patientId}>
            {loadingRisk ? "Fetching…" : "Get Risk"}
          </button>

          <button className="btn btnGhost" onClick={loadPatients} disabled={loadingPatients}>
            Refresh Patients
          </button>
        </div>

        {error && <div className="error">{error}</div>}

        <div className="riskBox">
          <div className="row spaceBetween">
            <h3>Result</h3>
            {selected && (
              <span className="pill">
                #{selected.id} — {selected.firstname} {selected.lastname}
              </span>
            )}
          </div>

          {!risk && !error && <div className="muted">No result yet. Click “Get Risk”.</div>}

          {risk && (
            <>
              <div className="riskValue">{risk.display}</div>
              <details className="details">
                <summary>Raw response</summary>
                <pre>{JSON.stringify(risk.raw, null, 2)}</pre>
              </details>
            </>
          )}
        </div>
      </div>
    </div>
  );
}
