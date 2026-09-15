from fastapi.testclient import TestClient

from main import app

client = TestClient(app)


def test_root_returns_status_message():
    response = client.get("/")
    assert response.status_code == 200
    assert response.json() == {"message": "Nomac Anomaly Detection API is running"}


def test_predict_flags_normal_reading_as_normal():
    payload = {
        "dc_power": 6366.96,
        "ac_power": 6200.0,
        "irradiation": 0.65,
        "ambient_temperature": 30.0,
        "module_temperature": 45.0,
        "efficiency": 0.0978,
    }
    response = client.post("/predict", json=payload)
    assert response.status_code == 200
    body = response.json()
    assert body["anomaly"] == 0
    assert body["status"] == "normal"
    assert isinstance(body["score"], float)


def test_predict_flags_inconsistent_reading_as_anomaly():
    payload = {
        "dc_power": 14471.0,
        "ac_power": 200.0,
        "irradiation": 0.01,
        "ambient_temperature": 5.0,
        "module_temperature": 90.0,
        "efficiency": 0.0,
    }
    response = client.post("/predict", json=payload)
    assert response.status_code == 200
    body = response.json()
    assert body["anomaly"] == 1
    assert body["status"] == "anomaly"


def test_predict_rejects_missing_field():
    payload = {
        "dc_power": 100.0,
        "ac_power": 90.0,
        "irradiation": 0.1,
        "ambient_temperature": 25.0,
        "efficiency": 0.05,
    }
    response = client.post("/predict", json=payload)
    assert response.status_code == 422


def test_predict_rejects_wrong_type():
    payload = {
        "dc_power": "not-a-number",
        "ac_power": 90.0,
        "irradiation": 0.1,
        "ambient_temperature": 25.0,
        "module_temperature": 40.0,
        "efficiency": 0.05,
    }
    response = client.post("/predict", json=payload)
    assert response.status_code == 422