package com.intelliops.team.repository;

import com.intelliops.team.entity.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TeamRepository extends JpaRepository<Team, UUID> {
    Page<Team> findByOrganizationIdAndActiveTrue(UUID organizationId, Pageable pageable);
    boolean existsByOrganizationIdAndName(UUID organizationId, String name);
}
