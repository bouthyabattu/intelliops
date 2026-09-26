package com.intelliops.task.entity;

import com.intelliops.common.entity.TenantEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task extends TenantEntity {

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "sprint_id")
    private UUID sprintId;

    @Column(name = "milestone_id")
    private UUID milestoneId;

    @Column(name = "parent_task_id")
    private UUID parentTaskId;

    @Column(name = "task_number", nullable = false)
    private Integer taskNumber;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.BACKLOG;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority = TaskPriority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskType type = TaskType.TASK;

    @Column(name = "story_points")
    private Integer storyPoints;

    @Column(name = "reporter_id")
    private UUID reporterId;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "estimated_hours", precision = 6, scale = 2)
    private BigDecimal estimatedHours;

    @Column(name = "logged_hours", precision = 6, scale = 2)
    private BigDecimal loggedHours = BigDecimal.ZERO;

    public enum TaskStatus {
        BACKLOG, TODO, IN_PROGRESS, IN_REVIEW, DONE, BLOCKED
    }

    public enum TaskPriority {
        LOW, MEDIUM, HIGH, URGENT, CRITICAL
    }

    public enum TaskType {
        TASK, BUG, STORY, EPIC, INCIDENT
    }
}
