package com.nomac.Monitoring.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.nomac.Monitoring.model.SensorReading;
import com.nomac.Monitoring.service.AnomalyService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(SensorController.class)
class SensorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AnomalyService anomalyService;

    private SensorReading analyzedReading() {
        SensorReading reading = new SensorReading();
        reading.setId(1L);
        reading.setTimestamp(LocalDateTime.of(2026, 1, 1, 12, 0));
        reading.setDcPower(6366.96);
        reading.setAcPower(6200.0);
        reading.setIrradiation(0.65);
        reading.setAmbientTemperature(30.0);
        reading.setModuleTemperature(45.0);
        reading.setEfficiency(0.0978);
        reading.setIsAnomaly(false);
        reading.setAnomalyScore(0.045);
        return reading;
    }

    @Test
    void postSensor_returns200AndAnalyzedReading_whenRequestIsValid() throws Exception {
        when(anomalyService.analyzeSensorData(any(SensorReading.class)))
                .thenReturn(analyzedReading());

        String requestJson = """
                {
                  "dcPower": 6366.96,
                  "acPower": 6200.0,
                  "irradiation": 0.65,
                  "ambientTemperature": 30.0,
                  "moduleTemperature": 45.0,
                  "efficiency": 0.0978
                }
                """;

        mockMvc.perform(post("/api/sensor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.isAnomaly").value(false))
                .andExpect(jsonPath("$.anomalyScore").value(0.045));
    }

    @Test
    void postSensor_returns400_whenFieldIsMissing() throws Exception {
        String requestJsonMissingEfficiency = """
                {
                  "dcPower": 6366.96,
                  "acPower": 6200.0,
                  "irradiation": 0.65,
                  "ambientTemperature": 30.0,
                  "moduleTemperature": 45.0
                }
                """;

        mockMvc.perform(post("/api/sensor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJsonMissingEfficiency))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void getSensors_returnsMappedResponseList() throws Exception {
        when(anomalyService.getAllReadings()).thenReturn(List.of(analyzedReading()));

        mockMvc.perform(get("/api/sensors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].dcPower").value(6366.96));
    }
}