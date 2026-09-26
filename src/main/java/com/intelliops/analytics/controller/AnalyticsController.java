package com.intelliops.analytics.controller;

import com.intelliops.analytics.dto.DoraMetricsResponse;
import com.intelliops.analytics.entity.KpiDefinition;
import com.intelliops.analytics.service.AnalyticsService;
import com.intelliops.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Engineering metrics, DORA benchmarks, and operational telemetry")
@SecurityRequirement(name = "bearerAuth")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dora/{organizationId}")
    @Operation(summary = "Get DORA metrics", description = "Returns industry benchmark DORA metrics and trend timeline for tenant")
    public ResponseEntity<ApiResponse<DoraMetricsResponse>> getDoraMetrics(@PathVariable UUID organizationId) {
        DoraMetricsResponse response = analyticsService.getDoraMetrics(organizationId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/kpis/{organizationId}")
    @Operation(summary = "List KPI definitions", description = "Returns configured engineering & AI operations KPIs")
    public ResponseEntity<ApiResponse<List<KpiDefinition>>> listKpis(@PathVariable UUID organizationId) {
        List<KpiDefinition> response = analyticsService.listKpis(organizationId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
