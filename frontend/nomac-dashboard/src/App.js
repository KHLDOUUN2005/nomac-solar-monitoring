import { useState, useEffect } from "react";
import axios from "axios";
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from "recharts";

const API = "http://localhost:8081/api";

export default function App() {
  const [readings, setReadings] = useState([]);
  const [anomalies, setAnomalies] = useState([]);
  const [form, setForm] = useState({
    dcPower: "", acPower: "", irradiation: "",
    ambientTemperature: "", moduleTemperature: "", efficiency: ""
  });
  const [result, setResult] = useState(null);

  const fetchData = async () => {
    const r1 = await axios.get(`${API}/sensors`);
    const r2 = await axios.get(`${API}/sensors/anomalies`);
    setReadings(r1.data);
    setAnomalies(r2.data);
  };

  useEffect(() => { fetchData(); }, []);

  const handleSubmit = async () => {
    const payload = {
      dcPower: parseFloat(form.dcPower),
      acPower: parseFloat(form.acPower),
      irradiation: parseFloat(form.irradiation),
      ambientTemperature: parseFloat(form.ambientTemperature),
      moduleTemperature: parseFloat(form.moduleTemperature),
      efficiency: parseFloat(form.efficiency)
    };
    const res = await axios.post(`${API}/sensor`, payload);
    setResult(res.data);
    fetchData();
  };

  return (
    <div style={{ padding: "20px", fontFamily: "Arial" }}>
      <h1>Nomac Solar Monitoring</h1>

      <div style={{ marginBottom: "30px", padding: "20px", border: "1px solid #ccc", borderRadius: "8px" }}>
        <h2>Submit Sensor Reading</h2>
        {["dcPower", "acPower", "irradiation", "ambientTemperature", "moduleTemperature", "efficiency"].map(field => (
          <input
            key={field}
            placeholder={field}
            value={form[field]}
            onChange={e => setForm({ ...form, [field]: e.target.value })}
            style={{ margin: "5px", padding: "8px", width: "180px" }}
          />
        ))}
        <br />
        <button onClick={handleSubmit} style={{ marginTop: "10px", padding: "10px 20px" }}>
          Analyze
        </button>
        {result && (
          <div style={{ marginTop: "10px", padding: "10px", background: result.isAnomaly ? "#ffe0e0" : "#e0ffe0", borderRadius: "5px" }}>
            <strong>Status:</strong> {result.isAnomaly ? "ANOMALY" : "NORMAL"} | <strong>Score:</strong> {result.anomalyScore?.toFixed(4)}
          </div>
        )}
      </div>

      <div style={{ marginBottom: "30px" }}>
        <h2>DC Power Over Time</h2>
        <ResponsiveContainer width="100%" height={300}>
          <LineChart data={readings.slice(-50)}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="timestamp" hide />
            <YAxis />
            <Tooltip />
            <Legend />
            <Line type="monotone" dataKey="dcPower" stroke="#8884d8" dot={false} />
            <Line type="monotone" dataKey="acPower" stroke="#82ca9d" dot={false} />
          </LineChart>
        </ResponsiveContainer>
      </div>

      <div>
        <h2>Anomalies ({anomalies.length})</h2>
        <table style={{ width: "100%", borderCollapse: "collapse" }}>
          <thead>
            <tr style={{ background: "#f0f0f0" }}>
              <th style={{ padding: "8px", border: "1px solid #ccc" }}>ID</th>
              <th style={{ padding: "8px", border: "1px solid #ccc" }}>Timestamp</th>
              <th style={{ padding: "8px", border: "1px solid #ccc" }}>DC Power</th>
              <th style={{ padding: "8px", border: "1px solid #ccc" }}>Score</th>
            </tr>
          </thead>
          <tbody>
            {anomalies.map(a => (
              <tr key={a.id} style={{ background: "#ffe0e0" }}>
                <td style={{ padding: "8px", border: "1px solid #ccc" }}>{a.id}</td>
                <td style={{ padding: "8px", border: "1px solid #ccc" }}>{a.timestamp}</td>
                <td style={{ padding: "8px", border: "1px solid #ccc" }}>{a.dcPower}</td>
                <td style={{ padding: "8px", border: "1px solid #ccc" }}>{a.anomalyScore?.toFixed(4)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}