package com.intelliops.team.dto;

import com.intelliops.team.entity.Team;

import java.time.Instant;
import java.util.UUID;

public record TeamResponse(
        UUID id,
        UUID organizationId,
        String name,
        String description,
        String avatarUrl,
        boolean active,
        int memberCount,
        Instant createdAt
) {
    public static TeamResponse from(Team team, int memberCount) {
        return new TeamResponse(
                team.getId(),
                team.getOrganizationId(),
                team.getName(),
                team.getDescription(),
                team.getAvatarUrl(),
                team.isActive(),
                memberCount,
                team.getCreatedAt()
        );
    }
}
