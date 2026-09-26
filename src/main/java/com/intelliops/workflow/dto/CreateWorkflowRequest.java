package com.intelliops.workflow.dto;

import com.intelliops.workflow.entity.WorkflowAction;
import com.intelliops.workflow.entity.WorkflowTrigger;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record CreateWorkflowRequest(
        @NotNull UUID organizationId,
        UUID projectId,
        @NotBlank String name,
        String description,
        @NotNull TriggerConfig trigger,
        List<ActionConfig> actions
) {
    public record TriggerConfig(
            WorkflowTrigger.TriggerType triggerType,
            String eventType,
            String cronExpression,
            Map<String, Object> config
    ) {}

    public record ActionConfig(
            WorkflowAction.ActionType actionType,
            int order,
            Map<String, Object> config
    ) {}
}
