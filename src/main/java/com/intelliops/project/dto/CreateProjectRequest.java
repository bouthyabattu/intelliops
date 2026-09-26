package com.intelliops.project.dto;

import com.intelliops.project.entity.Project;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateProjectRequest {

    @NotNull
    private UUID organizationId;

    @NotBlank
    @Size(min = 2, max = 20)
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Project key must be uppercase alphanumeric")
    private String key;

    @NotBlank
    @Size(min = 2, max = 255)
    private String name;

    private String description;

    private Project.ProjectStatus status = Project.ProjectStatus.ACTIVE;

    private UUID ownerId;

    private LocalDate startDate;

    private LocalDate targetDate;

    private String repositoryUrl;
}
