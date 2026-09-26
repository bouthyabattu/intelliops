package com.intelliops.workflow.entity;

import com.intelliops.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "workflow_actions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WorkflowAction extends BaseEntity {

    @Column(name = "workflow_id", nullable = false)
    private UUID workflowId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 80)
    private ActionType actionType;

    @Column(name = "action_order", nullable = false)
    private int actionOrder = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> config;

    public enum ActionType {
        CREATE_TASK,
        UPDATE_TASK_STATUS,
        SEND_NOTIFICATION,
        SEND_SLACK_MESSAGE,
        SEND_EMAIL,
        TRIGGER_AI_AGENT,
        CREATE_INCIDENT,
        WEBHOOK_CALL,
        UPDATE_PROJECT_HEALTH,
        GENERATE_AI_REPORT
    }
}
