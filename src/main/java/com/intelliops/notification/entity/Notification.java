package com.intelliops.notification.entity;

import com.intelliops.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "notifications",
       indexes = @Index(name = "idx_notif_user_unread",
                        columnList = "user_id, is_read, created_at DESC"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification extends BaseEntity {

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 60)
    private NotificationType type;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(name = "resource_type", length = 50)
    private String resourceType;

    @Column(name = "resource_id")
    private UUID resourceId;

    @Column(name = "resource_url")
    private String resourceUrl;

    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    @Column(name = "read_at")
    private Instant readAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    public enum NotificationType {
        TASK_ASSIGNED,
        TASK_UPDATED,
        TASK_COMPLETED,
        TASK_COMMENTED,
        TASK_OVERDUE,
        PROJECT_CREATED,
        PROJECT_STATUS_CHANGED,
        PROJECT_HEALTH_CRITICAL,
        SPRINT_STARTED,
        SPRINT_COMPLETED,
        INCIDENT_TRIGGERED,
        INCIDENT_RESOLVED,
        WORKFLOW_COMPLETED,
        WORKFLOW_FAILED,
        DOCUMENT_UPLOADED,
        AGENT_RUN_COMPLETED,
        MENTION,
        SYSTEM
    }
}
