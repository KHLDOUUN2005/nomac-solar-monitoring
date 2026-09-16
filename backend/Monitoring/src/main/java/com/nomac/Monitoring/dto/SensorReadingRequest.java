package com.nomac.Monitoring.dto;

import jakarta.validation.constraints.NotNull;

public record SensorReadingRequest(
        @NotNull(message = "dcPower is required") Double dcPower,
        @NotNull(message = "acPower is required") Double acPower,
        @NotNull(message = "irradiation is required") Double irradiation,
        @NotNull(message = "ambientTemperature is required") Double ambientTemperature,
        @NotNull(message = "moduleTemperature is required") Double moduleTemperature,
        @NotNull(message = "efficiency is required") Double efficiency
) {
}