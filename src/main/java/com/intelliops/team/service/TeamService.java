package com.intelliops.team.service;

import com.intelliops.common.dto.PageResponse;
import com.intelliops.common.exception.ResourceNotFoundException;
import com.intelliops.common.exception.ConflictException;
import com.intelliops.team.dto.CreateTeamRequest;
import com.intelliops.team.dto.TeamResponse;
import com.intelliops.team.entity.Team;
import com.intelliops.team.entity.TeamMember;
import com.intelliops.team.repository.TeamMemberRepository;
import com.intelliops.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional
    public TeamResponse create(CreateTeamRequest request) {
        if (teamRepository.existsByOrganizationIdAndName(request.organizationId(), request.name())) {
            throw new ConflictException("Team with name '" + request.name() + "' already exists in this organization");
        }
        Team team = Team.builder()
                .name(request.name())
                .description(request.description())
                .avatarUrl(request.avatarUrl())
                .active(true)
                .build();
        team.setOrganizationId(request.organizationId());
        team = teamRepository.save(team);
        log.info("Created team '{}' in org {}", team.getName(), team.getOrganizationId());
        return TeamResponse.from(team, 0);
    }

    @Transactional(readOnly = true)
    public PageResponse<TeamResponse> listByOrganization(UUID organizationId, Pageable pageable) {
        Page<Team> page = teamRepository.findByOrganizationIdAndActiveTrue(organizationId, pageable);
        Page<TeamResponse> responsePage = page.map(t ->
                TeamResponse.from(t, teamMemberRepository.countByTeamId(t.getId())));
        return PageResponse.from(responsePage);
    }

    @Transactional(readOnly = true)
    public TeamResponse getById(UUID id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + id));
        return TeamResponse.from(team, teamMemberRepository.countByTeamId(id));
    }

    @Transactional
    public TeamResponse addMember(UUID teamId, UUID userId, String role) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + teamId));
        if (teamMemberRepository.existsByTeamIdAndUserId(teamId, userId)) {
            throw new ConflictException("User is already a member of this team");
        }
        TeamMember member = TeamMember.builder()
                .teamId(teamId)
                .userId(userId)
                .role(role != null ? role : "MEMBER")
                .build();
        teamMemberRepository.save(member);
        log.info("Added user {} to team {} as {}", userId, teamId, role);
        return TeamResponse.from(team, teamMemberRepository.countByTeamId(teamId));
    }

    @Transactional
    public void removeMember(UUID teamId, UUID userId) {
        if (!teamMemberRepository.existsByTeamIdAndUserId(teamId, userId)) {
            throw new ResourceNotFoundException("Member not found in team");
        }
        teamMemberRepository.deleteByTeamIdAndUserId(teamId, userId);
        log.info("Removed user {} from team {}", userId, teamId);
    }

    @Transactional
    public void delete(UUID teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + teamId));
        team.setActive(false);
        teamRepository.save(team);
        log.info("Soft-deleted team {}", teamId);
    }
}
