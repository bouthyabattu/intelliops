package com.intelliops.team.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateTeamRequest(
        @NotNull UUID organizationId,
        @NotBlank String name,
        String description,
        String avatarUrl
) {}
