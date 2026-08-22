package com.nomac.Monitoring.controller;

import com.nomac.Monitoring.model.SensorReading;
import com.nomac.Monitoring.service.AnomalyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class SensorController {

    @Autowired
    private AnomalyService anomalyService;

    @PostMapping("/sensor")
    public SensorReading analyze(@RequestBody SensorReading reading) {
        return anomalyService.analyzeSensorData(reading);
    }

    @GetMapping("/sensors")
    public List<SensorReading> getAllReadings() {
        return anomalyService.getAllReadings();
    }

    @GetMapping("/sensors/anomalies")
    public List<SensorReading> getAnomalies() {
        return anomalyService.getAnomalies();
    }
}