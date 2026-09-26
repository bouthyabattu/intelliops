package com.intelliops.analytics.repository;

import com.intelliops.analytics.entity.KpiMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface KpiMeasurementRepository extends JpaRepository<KpiMeasurement, UUID> {
    List<KpiMeasurement> findByKpiIdOrderByMeasuredAtDesc(UUID kpiId);

    @Query("SELECT m FROM KpiMeasurement m WHERE m.kpiId = :kpiId AND m.measuredAt >= :since ORDER BY m.measuredAt ASC")
    List<KpiMeasurement> findByKpiIdSince(@Param("kpiId") UUID kpiId, @Param("since") Instant since);
}
