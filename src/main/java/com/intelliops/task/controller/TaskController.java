package com.intelliops.task.controller;

import com.intelliops.common.dto.ApiResponse;
import com.intelliops.common.dto.PageResponse;
import com.intelliops.task.dto.CreateTaskRequest;
import com.intelliops.task.dto.TaskResponse;
import com.intelliops.task.dto.UpdateTaskRequest;
import com.intelliops.task.entity.Task;
import com.intelliops.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Task and sprint backlog management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @Operation(summary = "Create task", description = "Creates a new task in a project")
    public ResponseEntity<ApiResponse<TaskResponse>> create(@Valid @RequestBody CreateTaskRequest request) {
        TaskResponse response = taskService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "List project tasks", description = "Returns a paginated list of tasks for a given project")
    public ResponseEntity<ApiResponse<PageResponse<TaskResponse>>> listByProject(
            @PathVariable UUID projectId,
            @PageableDefault(size = 20) Pageable pageable) {
        PageResponse<TaskResponse> response = PageResponse.of(taskService.listByProject(projectId, pageable));
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/project/{projectId}/status/{status}")
    @Operation(summary = "List tasks by status", description = "Returns all tasks for a project filtered by status (Kanban column)")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> listByProjectAndStatus(
            @PathVariable UUID projectId,
            @PathVariable Task.TaskStatus status) {
        List<TaskResponse> response = taskService.listByProjectAndStatus(projectId, status);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/organization/{organizationId}")
    @Operation(summary = "List tasks by organization", description = "Returns tasks across projects for an organization")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> listByOrganization(
            @PathVariable UUID organizationId) {
        List<TaskResponse> response = taskService.listByOrganization(organizationId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID", description = "Retrieves a single task by its unique UUID")
    public ResponseEntity<ApiResponse<TaskResponse>> getById(@PathVariable UUID id) {
        TaskResponse response = taskService.getById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update task", description = "Updates details of an existing task")
    public ResponseEntity<ApiResponse<TaskResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskRequest request) {
        TaskResponse response = taskService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update task status", description = "Quickly transition a task status (e.g., Kanban drag & drop)")
    public ResponseEntity<ApiResponse<TaskResponse>> updateStatus(
            @PathVariable UUID id,
            @RequestBody Map<String, Task.TaskStatus> body) {
        Task.TaskStatus status = body.get("status");
        TaskResponse response = taskService.updateStatus(id, status);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task", description = "Deletes a task by ID")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        taskService.delete(id);
        return ResponseEntity.ok(ApiResponse.okMsg("Task successfully deleted"));
    }
}
