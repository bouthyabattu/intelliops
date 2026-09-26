package com.intelliops.workflow.entity;

import com.intelliops.common.entity.TenantEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "workflows")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Workflow extends TenantEntity {

    @Column(name = "project_id")
    private UUID projectId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
