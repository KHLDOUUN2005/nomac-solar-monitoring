package com.nomac.Monitoring.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class SensorReading {
	@Id
	@GeneratedValue
	private Long id;
	private LocalDateTime timestamp;
	private Double dcPower;
	private Double acPower;
	private Double irradiation;
	private Double ambientTemperature;
	private Double moduleTemperature;
	private Double efficiency;
	private Boolean isAnomaly;
	private Double anomalyScore;
	
	public SensorReading(){}
	
	public SensorReading(Long id, LocalDateTime timestamp, Double dcPower, Double acPower, Double irradiation,
			Double ambientTemperature, Double moduleTemperature, Double efficiency, Boolean isAnomaly,
			Double anomalyScore) {
		super();
		this.id = id;
		this.timestamp = timestamp;
		this.dcPower = dcPower;
		this.acPower = acPower;
		this.irradiation = irradiation;
		this.ambientTemperature = ambientTemperature;
		this.moduleTemperature = moduleTemperature;
		this.efficiency = efficiency;
		this.isAnomaly = isAnomaly;
		this.anomalyScore = anomalyScore;
	}



	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public LocalDateTime getTimestamp() {
		return timestamp;
	}



	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}



	public Double getDcPower() {
		return dcPower;
	}



	public void setDcPower(Double dcPower) {
		this.dcPower = dcPower;
	}



	public Double getAcPower() {
		return acPower;
	}



	public void setAcPower(Double acPower) {
		this.acPower = acPower;
	}



	public Double getIrradiation() {
		return irradiation;
	}



	public void setIrradiation(Double irradiation) {
		this.irradiation = irradiation;
	}



	public Double getAmbientTemperature() {
		return ambientTemperature;
	}



	public void setAmbientTemperature(Double ambientTemperature) {
		this.ambientTemperature = ambientTemperature;
	}



	public Double getModuleTemperature() {
		return moduleTemperature;
	}



	public void setModuleTemperature(Double moduleTemperature) {
		this.moduleTemperature = moduleTemperature;
	}



	public Double getEfficiency() {
		return efficiency;
	}



	public void setEfficiency(Double efficiency) {
		this.efficiency = efficiency;
	}



	public Boolean getIsAnomaly() {
		return isAnomaly;
	}



	public void setIsAnomaly(Boolean isAnomaly) {
		this.isAnomaly = isAnomaly;
	}



	public Double getAnomalyScore() {
		return anomalyScore;
	}



	public void setAnomalyScore(Double anomalyScore) {
		this.anomalyScore = anomalyScore;
	}



	@Override
	public String toString() {
		return "SensorReading [id=" + id + ", timestamp=" + timestamp + ", dcPower=" + dcPower + ", acPower=" + acPower
				+ ", irradiation=" + irradiation + ", ambientTemperature=" + ambientTemperature + ", moduleTemperature="
				+ moduleTemperature + ", efficiency=" + efficiency + ", isAnomaly=" + isAnomaly + ", anomalyScore="
				+ anomalyScore + "]";
	}
}
