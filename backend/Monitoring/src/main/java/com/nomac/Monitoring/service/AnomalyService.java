package com.nomac.Monitoring.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.nomac.Monitoring.model.SensorReading;
import com.nomac.Monitoring.repository.SensorReadingRepository;

@Service
public class AnomalyService {

    private final SensorReadingRepository repository;
    private final RestTemplate restTemplate;

    public AnomalyService(SensorReadingRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
    }

    public SensorReading analyzeSensorData(SensorReading reading) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("dc_power", reading.getDcPower());
        requestBody.put("ac_power", reading.getAcPower());
        requestBody.put("irradiation", reading.getIrradiation());
        requestBody.put("ambient_temperature", reading.getAmbientTemperature());
        requestBody.put("module_temperature", reading.getModuleTemperature());
        requestBody.put("efficiency", reading.getEfficiency());

        Map response = restTemplate.postForObject(
            "http://ai-engine:8000/predict",
            requestBody,
            Map.class
        );

        reading.setIsAnomaly((Integer) response.get("anomaly") == 1);
        reading.setAnomalyScore(((Number) response.get("score")).doubleValue());
        reading.setTimestamp(LocalDateTime.now());

        return repository.save(reading);
    }

    public List<SensorReading> getAllReadings() {
        return repository.findAll();
    }

    public List<SensorReading> getAnomalies() {
        return repository.findByIsAnomalyTrue();
    }
}