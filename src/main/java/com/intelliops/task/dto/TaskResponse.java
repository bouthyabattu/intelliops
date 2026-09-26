package com.intelliops.task.dto;

import com.intelliops.task.entity.Task;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class TaskResponse {
    private UUID id;
    private UUID organizationId;
    private UUID projectId;
    private UUID sprintId;
    private UUID milestoneId;
    private UUID parentTaskId;
    private Integer taskNumber;
    private String title;
    private String description;
    private Task.TaskStatus status;
    private Task.TaskPriority priority;
    private Task.TaskType type;
    private Integer storyPoints;
    private UUID reporterId;
    private LocalDate dueDate;
    private BigDecimal estimatedHours;
    private BigDecimal loggedHours;
    private Instant createdAt;
    private Instant updatedAt;
}
