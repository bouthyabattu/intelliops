package com.intelliops.workflow.entity;

import com.intelliops.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "workflow_triggers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WorkflowTrigger extends BaseEntity {

    @Column(name = "workflow_id", nullable = false)
    private UUID workflowId;

    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_type", nullable = false, length = 50)
    private TriggerType triggerType;

    @Column(name = "event_type", length = 100)
    private String eventType;       // e.g. TASK_STATUS_CHANGED, PR_MERGED

    @Column(name = "cron_expression", length = 100)
    private String cronExpression;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> config;

    public enum TriggerType { EVENT, SCHEDULE, WEBHOOK }
}
