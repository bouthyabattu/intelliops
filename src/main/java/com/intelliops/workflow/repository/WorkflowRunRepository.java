package com.intelliops.workflow.repository;

import com.intelliops.workflow.entity.WorkflowRun;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WorkflowRunRepository extends JpaRepository<WorkflowRun, UUID> {
    Page<WorkflowRun> findByOrganizationIdAndWorkflowIdOrderByCreatedAtDesc(
            UUID organizationId, UUID workflowId, Pageable pageable);
    Page<WorkflowRun> findByOrganizationIdOrderByCreatedAtDesc(UUID organizationId, Pageable pageable);
    long countByWorkflowIdAndStatus(UUID workflowId, WorkflowRun.RunStatus status);
}
