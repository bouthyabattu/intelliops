package com.intelliops.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * TenantEntity extends BaseEntity by adding organization_id.
 * Every tenant-scoped resource must extend this class.
 * The SecurityTenantFilter enforces that queries only return records
 * matching the authenticated user's organization.
 */
@Getter
@Setter
@MappedSuperclass
public abstract class TenantEntity extends BaseEntity {

    @Column(name = "organization_id", nullable = false, updatable = false)
    private UUID organizationId;
}
