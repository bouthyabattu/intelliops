package com.intelliops.team.entity;

import com.intelliops.common.entity.TenantEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "teams",
       uniqueConstraints = @UniqueConstraint(columnNames = {"organization_id", "name"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Team extends TenantEntity {

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
