package com.nomac.Monitoring.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import com.nomac.Monitoring.exception.AiEngineUnavailableException;
import com.nomac.Monitoring.model.SensorReading;
import com.nomac.Monitoring.repository.SensorReadingRepository;

@ExtendWith(MockitoExtension.class)
class AnomalyServiceTest {

    @Mock
    private SensorReadingRepository repository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AnomalyService anomalyService;

    private SensorReading newReading() {
        SensorReading reading = new SensorReading();
        reading.setDcPower(6366.96);
        reading.setAcPower(6200.0);
        reading.setIrradiation(0.65);
        reading.setAmbientTemperature(30.0);
        reading.setModuleTemperature(45.0);
        reading.setEfficiency(0.0978);
        return reading;
    }

    @Test
    void analyzeSensorData_marksNormalReading_whenAiEngineReturnsNormal() {
        Map<String, Object> aiResponse = new HashMap<>();
        aiResponse.put("anomaly", 0);
        aiResponse.put("score", 0.045);
        aiResponse.put("status", "normal");

        when(restTemplate.postForObject(anyString(), any(), eq(Map.class)))
                .thenReturn(aiResponse);
        when(repository.save(any(SensorReading.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SensorReading reading = newReading();
        SensorReading result = anomalyService.analyzeSensorData(reading);

        assertThat(result.getIsAnomaly()).isFalse();
        assertThat(result.getAnomalyScore()).isEqualTo(0.045);
        assertThat(result.getTimestamp()).isNotNull();
        verify(repository).save(reading);
    }

    @Test
    void analyzeSensorData_marksAnomalousReading_whenAiEngineReturnsAnomaly() {
        Map<String, Object> aiResponse = new HashMap<>();
        aiResponse.put("anomaly", 1);
        aiResponse.put("score", -0.106);
        aiResponse.put("status", "anomaly");

        when(restTemplate.postForObject(anyString(), any(), eq(Map.class)))
                .thenReturn(aiResponse);
        when(repository.save(any(SensorReading.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SensorReading reading = newReading();
        SensorReading result = anomalyService.analyzeSensorData(reading);

        assertThat(result.getIsAnomaly()).isTrue();
        assertThat(result.getAnomalyScore()).isEqualTo(-0.106);
    }

    @Test
    void analyzeSensorData_sendsAllSixFeatures_toAiEngine() {
        Map<String, Object> aiResponse = new HashMap<>();
        aiResponse.put("anomaly", 0);
        aiResponse.put("score", 0.01);

        when(restTemplate.postForObject(anyString(), any(), eq(Map.class)))
                .thenReturn(aiResponse);
        when(repository.save(any(SensorReading.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        anomalyService.analyzeSensorData(newReading());

        verify(restTemplate).postForObject(
                eq("http://ai-engine:8000/predict"),
                org.mockito.ArgumentMatchers.<Map<String, Object>>argThat(body ->
                        body.size() == 6
                                && body.containsKey("dc_power")
                                && body.containsKey("efficiency")
                ),
                eq(Map.class)
        );
    }

    @Test
    void getAllReadings_delegatesToRepository() {
        List<SensorReading> readings = List.of(newReading());
        when(repository.findAll()).thenReturn(readings);

        List<SensorReading> result = anomalyService.getAllReadings();

        assertThat(result).isEqualTo(readings);
    }

    @Test
    void getAnomalies_delegatesToRepositoryAnomalyQuery() {
        SensorReading anomalous = newReading();
        anomalous.setIsAnomaly(true);
        when(repository.findByIsAnomalyTrue()).thenReturn(List.of(anomalous));

        List<SensorReading> result = anomalyService.getAnomalies();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIsAnomaly()).isTrue();
    }

    @Test
    void analyzeSensorData_throwsAiEngineUnavailable_whenRestCallFails() {
        when(restTemplate.postForObject(anyString(), any(), eq(Map.class)))
                .thenThrow(new org.springframework.web.client.ResourceAccessException("connection refused"));

        SensorReading reading = newReading();

        assertThatThrownBy(() -> anomalyService.analyzeSensorData(reading))
                .isInstanceOf(AiEngineUnavailableException.class);

        verify(repository, org.mockito.Mockito.never()).save(any());
    }

    @Test
    void analyzeSensorData_throwsAiEngineUnavailable_whenResponseIsNull() {
        when(restTemplate.postForObject(anyString(), any(), eq(Map.class)))
                .thenReturn(null);

        SensorReading reading = newReading();

        assertThatThrownBy(() -> anomalyService.analyzeSensorData(reading))
                .isInstanceOf(AiEngineUnavailableException.class);

        verify(repository, org.mockito.Mockito.never()).save(any());
    }

    @Test
    void analyzeSensorData_throwsAiEngineUnavailable_whenResponseIsMissingFields() {
        Map<String, Object> incompleteResponse = new HashMap<>();
        incompleteResponse.put("status", "normal");
        // "anomaly" and "score" deliberately missing

        when(restTemplate.postForObject(anyString(), any(), eq(Map.class)))
                .thenReturn(incompleteResponse);

        SensorReading reading = newReading();

        assertThatThrownBy(() -> anomalyService.analyzeSensorData(reading))
                .isInstanceOf(AiEngineUnavailableException.class);

        verify(repository, org.mockito.Mockito.never()).save(any());
    }
}