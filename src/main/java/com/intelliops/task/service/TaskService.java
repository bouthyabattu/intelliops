package com.intelliops.task.service;

import com.intelliops.common.exception.ResourceNotFoundException;
import com.intelliops.task.dto.CreateTaskRequest;
import com.intelliops.task.dto.TaskResponse;
import com.intelliops.task.dto.UpdateTaskRequest;
import com.intelliops.task.entity.Task;
import com.intelliops.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;

    @Transactional
    public TaskResponse create(CreateTaskRequest request) {
        Integer maxNumber = taskRepository.findMaxTaskNumberByProjectId(request.getProjectId());
        int nextNumber = (maxNumber == null ? 0 : maxNumber) + 1;

        Task task = Task.builder()
                .projectId(request.getProjectId())
                .sprintId(request.getSprintId())
                .milestoneId(request.getMilestoneId())
                .parentTaskId(request.getParentTaskId())
                .taskNumber(nextNumber)
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : Task.TaskStatus.TODO)
                .priority(request.getPriority() != null ? request.getPriority() : Task.TaskPriority.MEDIUM)
                .type(request.getType() != null ? request.getType() : Task.TaskType.TASK)
                .storyPoints(request.getStoryPoints())
                .reporterId(request.getReporterId())
                .dueDate(request.getDueDate())
                .estimatedHours(request.getEstimatedHours())
                .build();
        task.setOrganizationId(request.getOrganizationId());

        task = taskRepository.save(task);
        log.info("Task created: #{}-{} in org {}", task.getTaskNumber(), task.getTitle(), task.getOrganizationId());
        return toResponse(task);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> listByProject(UUID projectId, Pageable pageable) {
        return taskRepository.findByProjectId(projectId, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> listByProjectAndStatus(UUID projectId, Task.TaskStatus status) {
        return taskRepository.findByProjectIdAndStatus(projectId, status).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> listByProjectAndSprint(UUID projectId, UUID sprintId) {
        return taskRepository.findByProjectIdAndSprintId(projectId, sprintId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> listByOrganization(UUID organizationId) {
        return taskRepository.findByOrganizationId(organizationId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaskResponse getById(UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));
        return toResponse(task);
    }

    @Transactional
    public TaskResponse update(UUID id, UpdateTaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));

        if (request.getTitle() != null) task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getStatus() != null) task.setStatus(request.getStatus());
        if (request.getPriority() != null) task.setPriority(request.getPriority());
        if (request.getType() != null) task.setType(request.getType());
        if (request.getSprintId() != null) task.setSprintId(request.getSprintId());
        if (request.getMilestoneId() != null) task.setMilestoneId(request.getMilestoneId());
        if (request.getStoryPoints() != null) task.setStoryPoints(request.getStoryPoints());
        if (request.getDueDate() != null) task.setDueDate(request.getDueDate());
        if (request.getEstimatedHours() != null) task.setEstimatedHours(request.getEstimatedHours());
        if (request.getLoggedHours() != null) task.setLoggedHours(request.getLoggedHours());

        task = taskRepository.save(task);
        log.info("Task updated: {}", id);
        return toResponse(task);
    }

    @Transactional
    public TaskResponse updateStatus(UUID id, Task.TaskStatus status) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));
        task.setStatus(status);
        task = taskRepository.save(task);
        return toResponse(task);
    }

    @Transactional
    public void delete(UUID id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task", id);
        }
        taskRepository.deleteById(id);
        log.info("Task deleted: {}", id);
    }

    private TaskResponse toResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .organizationId(task.getOrganizationId())
                .projectId(task.getProjectId())
                .sprintId(task.getSprintId())
                .milestoneId(task.getMilestoneId())
                .parentTaskId(task.getParentTaskId())
                .taskNumber(task.getTaskNumber())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .type(task.getType())
                .storyPoints(task.getStoryPoints())
                .reporterId(task.getReporterId())
                .dueDate(task.getDueDate())
                .estimatedHours(task.getEstimatedHours())
                .loggedHours(task.getLoggedHours())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
