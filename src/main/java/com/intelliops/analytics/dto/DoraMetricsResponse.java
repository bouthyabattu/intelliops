package com.intelliops.analytics.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class DoraMetricsResponse {
    // Deployment Frequency (e.g. 14 deploys / week, status: ELITE)
    private MetricCard deploymentFrequency;

    // Lead Time for Changes (e.g. 1.8 hours, status: HIGH)
    private MetricCard leadTimeForChanges;

    // Mean Time to Restore Service (MTTR) (e.g. 24 mins, status: ELITE)
    private MetricCard timeToRestore;

    // Change Failure Rate (e.g. 4.2%, status: HIGH)
    private MetricCard changeFailureRate;

    // Engineering Health Score (0-100)
    private int overallHealthScore;

    // Time series history for charts
    private List<Map<String, Object>> trendHistory;

    @Data
    @Builder
    public static class MetricCard {
        private String title;
        private String value;
        private String unit;
        private String tier; // ELITE, HIGH, MEDIUM, LOW
        private BigDecimal changePercentage;
        private boolean isPositiveTrend;
    }
}
