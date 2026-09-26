package com.intelliops.analytics.entity;

import com.intelliops.common.entity.TenantEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "kpi_definitions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KpiDefinition extends TenantEntity {

    @Column(nullable = false, length = 100)
    private String code;

    @Column(nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private KpiCategory category;

    @Column(nullable = false, length = 50)
    private String unit = "PERCENTAGE";

    @Column(name = "target_value", precision = 10, scale = 2)
    private BigDecimal targetValue;

    public enum KpiCategory {
        ENGINEERING, AI_OPERATIONS, BUSINESS, INCIDENT
    }
}
