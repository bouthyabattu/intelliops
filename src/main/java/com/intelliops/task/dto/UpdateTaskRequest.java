package com.intelliops.task.dto;

import com.intelliops.task.entity.Task;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class UpdateTaskRequest {
    private String title;
    private String description;
    private Task.TaskStatus status;
    private Task.TaskPriority priority;
    private Task.TaskType type;
    private UUID sprintId;
    private UUID milestoneId;
    private Integer storyPoints;
    private LocalDate dueDate;
    private BigDecimal estimatedHours;
    private BigDecimal loggedHours;
}
