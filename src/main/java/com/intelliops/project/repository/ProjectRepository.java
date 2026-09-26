package com.intelliops.project.repository;

import com.intelliops.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    Page<Project> findByOrganizationId(UUID organizationId, Pageable pageable);
    Optional<Project> findByOrganizationIdAndKey(UUID organizationId, String key);
    List<Project> findByOrganizationIdAndStatus(UUID organizationId, Project.ProjectStatus status);
    boolean existsByOrganizationIdAndKey(UUID organizationId, String key);
}
