-- V8: Workflow Automation Engine Schema
-- Triggers, conditions, actions, and execution audit runs

CREATE TABLE IF NOT EXISTS workflows (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    created_by UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_workflows_org ON workflows(organization_id);

CREATE TABLE IF NOT EXISTS workflow_triggers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    workflow_id UUID NOT NULL REFERENCES workflows(id) ON DELETE CASCADE,
    trigger_type VARCHAR(100) NOT NULL, -- EVENT, SCHEDULE, WEBHOOK, MANUAL
    event_name VARCHAR(150), -- e.g. "github.pr_merged", "task.completed", "ci.failed"
    cron_expression VARCHAR(100),
    config JSONB DEFAULT '{}'::jsonb NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS workflow_conditions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    workflow_id UUID NOT NULL REFERENCES workflows(id) ON DELETE CASCADE,
    field_path VARCHAR(150) NOT NULL, -- e.g. "data.status", "data.ci_outcome"
    operator VARCHAR(50) NOT NULL, -- EQUALS, NOT_EQUALS, CONTAINS, GREATER_THAN
    expected_value TEXT NOT NULL,
    step_order INTEGER DEFAULT 1 NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS workflow_actions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    workflow_id UUID NOT NULL REFERENCES workflows(id) ON DELETE CASCADE,
    action_type VARCHAR(100) NOT NULL, -- NOTIFY_SLACK, CREATE_TASK, INVOKE_AI_AGENT, SEND_WEBHOOK
    config JSONB DEFAULT '{}'::jsonb NOT NULL,
    step_order INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS workflow_runs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    workflow_id UUID NOT NULL REFERENCES workflows(id) ON DELETE CASCADE,
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    triggered_by VARCHAR(100) NOT NULL,
    trigger_payload JSONB,
    status VARCHAR(50) DEFAULT 'RUNNING' NOT NULL, -- RUNNING, SUCCESS, FAILED, RETRYING
    started_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    completed_at TIMESTAMP WITH TIME ZONE,
    error_message TEXT
);

CREATE INDEX IF NOT EXISTS idx_workflow_runs_org ON workflow_runs(organization_id);

CREATE TABLE IF NOT EXISTS workflow_run_steps (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    run_id UUID NOT NULL REFERENCES workflow_runs(id) ON DELETE CASCADE,
    step_number INTEGER NOT NULL,
    action_type VARCHAR(100) NOT NULL,
    status VARCHAR(50) DEFAULT 'SUCCESS' NOT NULL,
    output_data JSONB,
    execution_time_ms INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);
