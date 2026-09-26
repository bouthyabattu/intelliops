package com.intelliops.organization.entity;

import com.intelliops.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "organizations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(name = "logo_url")
    private String logoUrl;

    @Builder.Default
    @Column(name = "plan_tier", nullable = false)
    private String planTier = "ENTERPRISE";

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
}
