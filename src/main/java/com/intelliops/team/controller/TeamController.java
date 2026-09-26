package com.intelliops.team.controller;

import com.intelliops.common.dto.ApiResponse;
import com.intelliops.common.dto.PageResponse;
import com.intelliops.team.dto.CreateTeamRequest;
import com.intelliops.team.dto.TeamResponse;
import com.intelliops.team.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/teams")
@RequiredArgsConstructor
@Tag(name = "Teams", description = "Team management — create teams, add/remove members, assign roles")
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    @Operation(summary = "Create a new team within an organization")
    public ResponseEntity<ApiResponse<TeamResponse>> create(@Valid @RequestBody CreateTeamRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(teamService.create(request)));
    }

    @GetMapping
    @Operation(summary = "List all active teams in an organization")
    public ResponseEntity<ApiResponse<PageResponse<TeamResponse>>> list(
            @RequestParam UUID organizationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("name").ascending());
        return ResponseEntity.ok(ApiResponse.ok(teamService.listByOrganization(organizationId, pageRequest)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a team by ID")
    public ResponseEntity<ApiResponse<TeamResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(teamService.getById(id)));
    }

    @PostMapping("/{teamId}/members")
    @Operation(summary = "Add a member to a team")
    public ResponseEntity<ApiResponse<TeamResponse>> addMember(
            @PathVariable UUID teamId,
            @RequestParam UUID userId,
            @RequestParam(defaultValue = "MEMBER") String role) {
        return ResponseEntity.ok(ApiResponse.ok(teamService.addMember(teamId, userId, role)));
    }

    @DeleteMapping("/{teamId}/members/{userId}")
    @Operation(summary = "Remove a member from a team")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable UUID teamId,
            @PathVariable UUID userId) {
        teamService.removeMember(teamId, userId);
        return ResponseEntity.ok(ApiResponse.ok(null, "Member removed from team"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete (deactivate) a team")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        teamService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Team deactivated successfully"));
    }
}
