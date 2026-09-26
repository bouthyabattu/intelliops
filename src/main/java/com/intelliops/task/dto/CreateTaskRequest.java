package com.intelliops.task.dto;

import com.intelliops.task.entity.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateTaskRequest {
    @NotNull
    private UUID organizationId;

    @NotNull
    private UUID projectId;

    private UUID sprintId;
    private UUID milestoneId;
    private UUID parentTaskId;

    @NotBlank
    @Size(max = 300)
    private String title;

    private String description;
    private Task.TaskStatus status = Task.TaskStatus.TODO;
    private Task.TaskPriority priority = Task.TaskPriority.MEDIUM;
    private Task.TaskType type = Task.TaskType.TASK;

    private Integer storyPoints;
    private UUID reporterId;
    private LocalDate dueDate;
    private BigDecimal estimatedHours;
}
