package com.intelliops.analytics.entity;

import com.intelliops.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "kpi_measurements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KpiMeasurement extends BaseEntity {

    @Column(name = "kpi_id", nullable = false)
    private UUID kpiId;

    @Column(name = "measured_at", nullable = false)
    private Instant measuredAt = Instant.now();

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal value;

    @Column(name = "dimension_label", length = 100)
    private String dimensionLabel;
}
