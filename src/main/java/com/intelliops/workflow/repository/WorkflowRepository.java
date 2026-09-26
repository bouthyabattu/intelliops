package com.intelliops.workflow.repository;

import com.intelliops.workflow.entity.Workflow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, UUID> {
    Page<Workflow> findByOrganizationId(UUID organizationId, Pageable pageable);
    List<Workflow> findByOrganizationIdAndActiveTrue(UUID organizationId);
}
