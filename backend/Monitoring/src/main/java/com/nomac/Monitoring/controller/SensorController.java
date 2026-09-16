package com.nomac.Monitoring.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nomac.Monitoring.dto.SensorReadingRequest;
import com.nomac.Monitoring.dto.SensorReadingResponse;
import com.nomac.Monitoring.model.SensorReading;
import com.nomac.Monitoring.service.AnomalyService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class SensorController {

    private final AnomalyService anomalyService;

    public SensorController(AnomalyService anomalyService) {
        this.anomalyService = anomalyService;
    }

    @PostMapping("/sensor")
    public SensorReadingResponse analyze(@Valid @RequestBody SensorReadingRequest request) {
        SensorReading reading = new SensorReading();
        reading.setDcPower(request.dcPower());
        reading.setAcPower(request.acPower());
        reading.setIrradiation(request.irradiation());
        reading.setAmbientTemperature(request.ambientTemperature());
        reading.setModuleTemperature(request.moduleTemperature());
        reading.setEfficiency(request.efficiency());

        SensorReading analyzed = anomalyService.analyzeSensorData(reading);
        return SensorReadingResponse.fromEntity(analyzed);
    }

    @GetMapping("/sensors")
    public List<SensorReadingResponse> getAllReadings() {
        return anomalyService.getAllReadings().stream()
                .map(SensorReadingResponse::fromEntity)
                .toList();
    }

    @GetMapping("/sensors/anomalies")
    public List<SensorReadingResponse> getAnomalies() {
        return anomalyService.getAnomalies().stream()
                .map(SensorReadingResponse::fromEntity)
                .toList();
    }
}