package com.nomac.Monitoring.dto;

import java.time.LocalDateTime;

import com.nomac.Monitoring.model.SensorReading;

public record SensorReadingResponse(
        Long id,
        LocalDateTime timestamp,
        Double dcPower,
        Double acPower,
        Double irradiation,
        Double ambientTemperature,
        Double moduleTemperature,
        Double efficiency,
        Boolean isAnomaly,
        Double anomalyScore
) {

    public static SensorReadingResponse fromEntity(SensorReading reading) {
        return new SensorReadingResponse(
                reading.getId(),
                reading.getTimestamp(),
                reading.getDcPower(),
                reading.getAcPower(),
                reading.getIrradiation(),
                reading.getAmbientTemperature(),
                reading.getModuleTemperature(),
                reading.getEfficiency(),
                reading.getIsAnomaly(),
                reading.getAnomalyScore()
        );
    }
}