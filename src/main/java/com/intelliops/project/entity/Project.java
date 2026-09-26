package com.intelliops.project.entity;

import com.intelliops.common.entity.TenantEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project extends TenantEntity {

    @Column(name = "team_id")
    private UUID teamId;

    @Column(name = "key", nullable = false, length = 20)
    private String key;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status = ProjectStatus.ACTIVE;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectHealth health = ProjectHealth.ON_TRACK;

    @Column(name = "owner_id")
    private UUID ownerId;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @Column(name = "repository_url")
    private String repositoryUrl;

    public enum ProjectStatus {
        PLANNING, ACTIVE, PAUSED, COMPLETED, ARCHIVED
    }

    public enum ProjectHealth {
        ON_TRACK, AT_RISK, CRITICAL
    }
}
