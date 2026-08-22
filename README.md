# Nomac Solar Monitoring System

An AI-based intelligent monitoring system for anomaly detection in a solar power plant, developed during an internship at ACWA Operations / Nomac Ouarzazate.

## Architecture

- **AI/ML Engine** — Python, Scikit-learn (Isolation Forest), FastAPI
- **Backend** — Java 17, Spring Boot, PostgreSQL
- **Frontend** — React, Recharts
- **DevOps** — Docker, Docker Compose

## How to Run

Make sure Docker Desktop is running, then:

```bash
docker-compose up --build
```

- Frontend: http://localhost:3000
- Backend API: http://localhost:8081
- AI Engine: http://localhost:8000

## Dataset

Solar power generation data from Kaggle:
https://www.kaggle.com/datasets/anikannal/solar-power-generation-data

## Author

Khalid El-Harmoud — INPT, Software Engineering Student