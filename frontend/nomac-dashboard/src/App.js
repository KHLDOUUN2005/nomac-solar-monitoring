import { useState, useEffect, useCallback } from "react";
import axios from "axios";
import {
  LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
} from "recharts";
import "./App.css";

const API = "http://localhost:8081/api";

const FIELDS = [
  { key: "dcPower", label: "DC Power (W)" },
  { key: "acPower", label: "AC Power (W)" },
  { key: "irradiation", label: "Irradiation (kW/m²)" },
  { key: "ambientTemperature", label: "Ambient Temp (°C)" },
  { key: "moduleTemperature", label: "Module Temp (°C)" },
  { key: "efficiency", label: "Efficiency (%)" },
];

function formatTimestamp(value) {
  if (!value) return "—";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return date.toLocaleString(undefined, {
    month: "short", day: "numeric", hour: "2-digit", minute: "2-digit", second: "2-digit",
  });
}

export default function App() {
  const [readings, setReadings] = useState([]);
  const [anomalies, setAnomalies] = useState([]);
  const [form, setForm] = useState(
    Object.fromEntries(FIELDS.map(f => [f.key, ""]))
  );
  const [result, setResult] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [connected, setConnected] = useState(true);

  const fetchData = useCallback(async () => {
    try {
      const [r1, r2] = await Promise.all([
        axios.get(`${API}/sensors`),
        axios.get(`${API}/sensors/anomalies`),
      ]);
      setReadings(r1.data);
      setAnomalies(r2.data);
      setConnected(true);
    } catch (e) {
      setConnected(false);
    }
  }, []);

  useEffect(() => { fetchData(); }, [fetchData]);

  const handleSubmit = async () => {
    setError(null);
    setSubmitting(true);
    try {
      const payload = Object.fromEntries(
        FIELDS.map(f => [f.key, parseFloat(form[f.key])])
      );
      const res = await axios.post(`${API}/sensor`, payload);
      setResult(res.data);
      await fetchData();
    } catch (e) {
      const message = e.response?.data?.message || "Could not analyze this reading. Check the values and try again.";
      setError(message);
    } finally {
      setSubmitting(false);
    }
  };

  const chartData = readings.slice(-50);

  return (
    <div className="dashboard">
      <div className="dashboard__header">
        <h1 className="dashboard__title">Nomac Solar Monitoring</h1>
        <span className="status-pill">
          <span className={`status-dot ${connected ? "" : "status-dot--error"}`} />
          {connected ? "Connected" : "Connection lost"}
        </span>
      </div>

      <div className="panel panel--accent-dc">
        <h2 className="panel__title">Submit sensor reading</h2>
        <div className="field-grid">
          {FIELDS.map(({ key, label }) => (
            <div className="field" key={key}>
              <label htmlFor={key}>{label}</label>
              <input
                id={key}
                type="number"
                step="any"
                value={form[key]}
                onChange={e => setForm({ ...form, [key]: e.target.value })}
              />
            </div>
          ))}
        </div>

        {error && <div className="error-banner">{error}</div>}

        <button className="btn-primary" onClick={handleSubmit} disabled={submitting}>
          {submitting ? "Analyzing…" : "Analyze"}
        </button>

        {result && (
          <div className={`result-readout ${result.isAnomaly ? "result-readout--anomaly" : "result-readout--normal"}`}>
            <span className="result-readout__status">
              {result.isAnomaly ? "ANOMALY DETECTED" : "NORMAL"}
            </span>
            <span className="result-readout__score">
              score {result.anomalyScore?.toFixed(4)}
            </span>
          </div>
        )}
      </div>

      <div className="panel">
        <h2 className="panel__title">DC / AC power over time</h2>
        <div className="legend-row">
          <span className="legend-item">
            <span className="legend-swatch" style={{ background: "var(--accent-dc)" }} />
            DC power
          </span>
          <span className="legend-item">
            <span className="legend-swatch" style={{ background: "var(--accent-ac)" }} />
            AC power
          </span>
        </div>
        {chartData.length === 0 ? (
          <div className="empty-state">No readings yet — submit one above to see the trend.</div>
        ) : (
          <ResponsiveContainer width="100%" height={280}>
            <LineChart data={chartData}>
              <CartesianGrid stroke="#2a323b" strokeDasharray="3 3" />
              <XAxis
                dataKey="timestamp"
                tickFormatter={formatTimestamp}
                stroke="#8a94a0"
                fontSize={11}
                minTickGap={40}
              />
              <YAxis stroke="#8a94a0" fontSize={11} />
              <Tooltip
                contentStyle={{ background: "#1a2027", border: "1px solid #2a323b", borderRadius: 4 }}
                labelStyle={{ color: "#8a94a0" }}
                labelFormatter={formatTimestamp}
              />
              <Line type="monotone" dataKey="dcPower" stroke="#f2a93b" strokeWidth={2} dot={false} name="DC Power" />
              <Line type="monotone" dataKey="acPower" stroke="#3fc7b8" strokeWidth={2} dot={false} name="AC Power" />
            </LineChart>
          </ResponsiveContainer>
        )}
      </div>

      <div className="panel">
        <h2 className="panel__title">Anomalies ({anomalies.length})</h2>
        {anomalies.length === 0 ? (
          <div className="empty-state">No anomalies detected yet.</div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Timestamp</th>
                <th className="numeric">DC Power</th>
                <th className="numeric">Score</th>
              </tr>
            </thead>
            <tbody>
              {anomalies.map(a => (
                <tr key={a.id}>
                  <td>{a.id}</td>
                  <td>{formatTimestamp(a.timestamp)}</td>
                  <td className="numeric">{a.dcPower}</td>
                  <td className="numeric">{a.anomalyScore?.toFixed(4)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}