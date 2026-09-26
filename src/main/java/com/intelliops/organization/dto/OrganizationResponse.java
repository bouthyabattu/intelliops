package com.intelliops.organization.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class OrganizationResponse {
    private UUID id;
    private String name;
    private String slug;
    private String logoUrl;
    private String planTier;
    private boolean isActive;
    private int memberCount;
    private Instant createdAt;
}
