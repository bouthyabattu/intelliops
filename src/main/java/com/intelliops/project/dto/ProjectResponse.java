package com.intelliops.project.dto;

import com.intelliops.project.entity.Project;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class ProjectResponse {
    private UUID id;
    private UUID organizationId;
    private String key;
    private String name;
    private String description;
    private Project.ProjectStatus status;
    private Project.ProjectHealth health;
    private UUID ownerId;
    private LocalDate startDate;
    private LocalDate targetDate;
    private String repositoryUrl;
    private int openTaskCount;
    private Instant createdAt;
    private Instant updatedAt;
}
