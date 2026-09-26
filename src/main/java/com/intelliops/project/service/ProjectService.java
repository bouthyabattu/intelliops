package com.intelliops.project.service;

import com.intelliops.common.exception.BusinessException;
import com.intelliops.common.exception.ResourceNotFoundException;
import com.intelliops.project.dto.CreateProjectRequest;
import com.intelliops.project.dto.ProjectResponse;
import com.intelliops.project.entity.Project;
import com.intelliops.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional
    public ProjectResponse create(CreateProjectRequest request) {
        if (projectRepository.existsByOrganizationIdAndKey(request.getOrganizationId(), request.getKey())) {
            throw new BusinessException(
                "A project with key '" + request.getKey() + "' already exists in this organization",
                "PROJECT_KEY_EXISTS"
            );
        }

        Project project = Project.builder()
                .key(request.getKey())
                .name(request.getName())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : Project.ProjectStatus.ACTIVE)
                .health(Project.ProjectHealth.ON_TRACK)
                .ownerId(request.getOwnerId())
                .startDate(request.getStartDate())
                .targetDate(request.getTargetDate())
                .repositoryUrl(request.getRepositoryUrl())
                .build();
        project.setOrganizationId(request.getOrganizationId());
        project = projectRepository.save(project);
        log.info("Project created: {} in org {}", project.getKey(), project.getOrganizationId());
        return toResponse(project);
    }

    @Transactional(readOnly = true)
    public Page<ProjectResponse> listByOrganization(UUID organizationId, Pageable pageable) {
        return projectRepository.findByOrganizationId(organizationId, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ProjectResponse getById(UUID id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));
        return toResponse(project);
    }

    @Transactional
    public ProjectResponse updateHealth(UUID id, Project.ProjectHealth health) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));
        project.setHealth(health);
        project = projectRepository.save(project);
        return toResponse(project);
    }

    @Transactional
    public ProjectResponse updateStatus(UUID id, Project.ProjectStatus status) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));
        project.setStatus(status);
        project = projectRepository.save(project);
        return toResponse(project);
    }

    @Transactional
    public void delete(UUID id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project", id);
        }
        projectRepository.deleteById(id);
        log.info("Project deleted: {}", id);
    }

    private ProjectResponse toResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .organizationId(project.getOrganizationId())
                .key(project.getKey())
                .name(project.getName())
                .description(project.getDescription())
                .status(project.getStatus())
                .health(project.getHealth())
                .ownerId(project.getOwnerId())
                .startDate(project.getStartDate())
                .targetDate(project.getTargetDate())
                .repositoryUrl(project.getRepositoryUrl())
                .openTaskCount(0) // enriched by analytics layer
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }
}
