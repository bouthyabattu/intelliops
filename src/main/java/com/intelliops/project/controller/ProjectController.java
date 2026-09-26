package com.intelliops.project.controller;

import com.intelliops.common.dto.ApiResponse;
import com.intelliops.common.dto.PageResponse;
import com.intelliops.project.dto.CreateProjectRequest;
import com.intelliops.project.dto.ProjectResponse;
import com.intelliops.project.entity.Project;
import com.intelliops.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Project lifecycle management including sprints, milestones, and status")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @Operation(summary = "Create a new project in an organization")
    public ResponseEntity<ApiResponse<ProjectResponse>> create(@Valid @RequestBody CreateProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(projectService.create(request)));
    }

    @GetMapping
    @Operation(summary = "List all projects for an organization (paginated)")
    public ResponseEntity<ApiResponse<PageResponse<ProjectResponse>>> list(
            @RequestParam UUID organizationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.ok(
                PageResponse.from(projectService.listByOrganization(organizationId, pageRequest))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a project by ID")
    public ResponseEntity<ApiResponse<ProjectResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(projectService.getById(id)));
    }

    @PatchMapping("/{id}/health")
    @Operation(summary = "Update project health status (ON_TRACK / AT_RISK / CRITICAL)")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateHealth(
            @PathVariable UUID id,
            @RequestParam Project.ProjectHealth health) {
        return ResponseEntity.ok(ApiResponse.ok(projectService.updateHealth(id, health)));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update project status (PLANNING / ACTIVE / PAUSED / COMPLETED / ARCHIVED)")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateStatus(
            @PathVariable UUID id,
            @RequestParam Project.ProjectStatus status) {
        return ResponseEntity.ok(ApiResponse.ok(projectService.updateStatus(id, status)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a project and all associated resources")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        projectService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Project deleted successfully"));
    }
}
