from fastapi import FastAPI
from pydantic import BaseModel
import joblib
import numpy as np

app = FastAPI(title="Nomac Anomaly Detection API")

model = joblib.load("../models/isolation_forest.pkl")
scaler = joblib.load("../models/scaler.pkl")

class SensorData(BaseModel):
    dc_power: float
    ac_power: float
    irradiation: float
    ambient_temperature: float
    module_temperature: float
    efficiency: float

@app.get("/")
def root():
    return {"message": "Nomac Anomaly Detection API is running"}

@app.post("/predict")
def predict(data: SensorData):
    features = np.array([[
        data.dc_power,
        data.ac_power,
        data.irradiation,
        data.ambient_temperature,
        data.module_temperature,
        data.efficiency
    ]])
    scaled = scaler.transform(features)
    prediction = model.predict(scaled)
    score = model.decision_function(scaled)
    return {
        "anomaly": int(prediction[0] == -1),
        "score": float(score[0]),
        "status": "anomaly" if prediction[0] == -1 else "normal"
    }