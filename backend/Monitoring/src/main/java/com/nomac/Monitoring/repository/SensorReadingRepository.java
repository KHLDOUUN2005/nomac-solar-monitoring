package com.nomac.Monitoring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nomac.Monitoring.model.SensorReading;

@Repository
public interface SensorReadingRepository extends JpaRepository<SensorReading, Long>{

	List<SensorReading> findByIsAnomalyTrue();

}
