-- V9: External Integrations & Developer Intelligence Schema
-- GitHub repositories, PRs, CI workflow runs, and webhook events

CREATE TABLE IF NOT EXISTS integrations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    provider VARCHAR(50) NOT NULL, -- GITHUB, SLACK, JIRA, GOOGLE_DRIVE, CICD
    name VARCHAR(150) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    config JSONB DEFAULT '{}'::jsonb NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uq_integrations_org_provider UNIQUE (organization_id, provider, name)
);

CREATE TABLE IF NOT EXISTS integration_credentials (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    integration_id UUID NOT NULL REFERENCES integrations(id) ON DELETE CASCADE,
    encrypted_token TEXT NOT NULL,
    refresh_token TEXT,
    expires_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uq_integration_creds UNIQUE (integration_id)
);

CREATE TABLE IF NOT EXISTS webhook_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    source VARCHAR(50) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    payload JSONB NOT NULL,
    processed BOOLEAN DEFAULT FALSE NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_webhook_events_source ON webhook_events(source, event_type);

-- GitHub Intelligence Domain Tables
CREATE TABLE IF NOT EXISTS github_repositories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    repo_name VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    default_branch VARCHAR(100) DEFAULT 'main' NOT NULL,
    html_url TEXT NOT NULL,
    is_private BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS github_pull_requests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    repo_id UUID REFERENCES github_repositories(id) ON DELETE CASCADE,
    pr_number INTEGER NOT NULL,
    title VARCHAR(300) NOT NULL,
    author VARCHAR(150) NOT NULL,
    state VARCHAR(50) NOT NULL, -- OPEN, CLOSED, MERGED
    merge_commit_sha VARCHAR(100),
    merged_at TIMESTAMP WITH TIME ZONE,
    associated_jira_issue VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_github_prs_repo ON github_pull_requests(repo_id);
CREATE INDEX IF NOT EXISTS idx_github_prs_number ON github_pull_requests(pr_number);

CREATE TABLE IF NOT EXISTS ci_workflow_runs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    repo_id UUID REFERENCES github_repositories(id) ON DELETE CASCADE,
    pr_id UUID REFERENCES github_pull_requests(id) ON DELETE SET NULL,
    run_number INTEGER NOT NULL,
    workflow_name VARCHAR(150) NOT NULL,
    commit_sha VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL, -- QUEUED, IN_PROGRESS, COMPLETED
    conclusion VARCHAR(50), -- SUCCESS, FAILURE, TIMED_OUT, CANCELLED
    duration_seconds INTEGER,
    logs_summary TEXT,
    started_at TIMESTAMP WITH TIME ZONE NOT NULL,
    completed_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_ci_runs_repo ON ci_workflow_runs(repo_id);
CREATE INDEX IF NOT EXISTS idx_ci_runs_conclusion ON ci_workflow_runs(conclusion);

CREATE TABLE IF NOT EXISTS incidents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    incident_number VARCHAR(50) NOT NULL, -- e.g. "INC-421"
    title VARCHAR(300) NOT NULL,
    severity VARCHAR(50) NOT NULL, -- P1_CRITICAL, P2_HIGH, P3_MEDIUM, P4_LOW
    status VARCHAR(50) DEFAULT 'OPEN' NOT NULL, -- OPEN, INVESTIGATING, MITIGATED, RESOLVED
    affected_service VARCHAR(150) NOT NULL,
    root_cause TEXT,
    lead_responder UUID REFERENCES users(id) ON DELETE SET NULL,
    started_at TIMESTAMP WITH TIME ZONE NOT NULL,
    resolved_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uq_incidents_org_num UNIQUE (organization_id, incident_number)
);

CREATE INDEX IF NOT EXISTS idx_incidents_org ON incidents(organization_id);
CREATE INDEX IF NOT EXISTS idx_incidents_status ON incidents(status);
