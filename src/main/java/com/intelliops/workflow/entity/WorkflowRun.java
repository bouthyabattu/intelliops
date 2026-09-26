package com.intelliops.workflow.entity;

import com.intelliops.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "workflow_runs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WorkflowRun extends BaseEntity {

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "workflow_id")
    private UUID workflowId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RunStatus status = RunStatus.RUNNING;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "trigger_data", columnDefinition = "jsonb")
    private Map<String, Object> triggerData;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "execution_log", columnDefinition = "jsonb")
    private List<Map<String, Object>> executionLog;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "retry_count", nullable = false)
    private int retryCount = 0;

    @Column(name = "completed_at")
    private Instant completedAt;

    public enum RunStatus { RUNNING, COMPLETED, FAILED, RETRYING }
}
