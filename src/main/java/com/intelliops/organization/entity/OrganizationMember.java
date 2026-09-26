package com.intelliops.organization.entity;

import com.intelliops.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "organization_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationMember extends BaseEntity {

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "role_id", nullable = false)
    private UUID roleId;

    @Column
    private String title;

    @Builder.Default
    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt = Instant.now();

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
}
