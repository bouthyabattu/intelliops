-- ============================================================
-- IntelliOps Platform — Complete Database Schema
-- Migration: V1__init_schema.sql
-- PostgreSQL 16 + pgvector extension
-- ============================================================

-- Enable required extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS "vector";

-- ============================================================
-- IDENTITY & ACCESS MODULE
-- ============================================================

CREATE TABLE users (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    email           VARCHAR(255) NOT NULL UNIQUE,
    username        VARCHAR(100) NOT NULL UNIQUE,
    display_name    VARCHAR(200),
    avatar_url      TEXT,
    is_active       BOOLEAN     NOT NULL DEFAULT true,
    is_email_verified BOOLEAN   NOT NULL DEFAULT false,
    last_login_at   TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by      UUID,
    last_modified_by UUID
);

CREATE TABLE user_credentials (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID        NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    password_hash   VARCHAR(255) NOT NULL,
    mfa_enabled     BOOLEAN     NOT NULL DEFAULT false,
    mfa_secret      VARCHAR(100),
    failed_attempts INTEGER     NOT NULL DEFAULT 0,
    locked_until    TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================
-- ORGANIZATION MODULE
-- ============================================================

CREATE TABLE organizations (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(255) NOT NULL,
    slug            VARCHAR(100) NOT NULL UNIQUE,
    logo_url        TEXT,
    plan_tier       VARCHAR(50)  NOT NULL DEFAULT 'ENTERPRISE',
    is_active       BOOLEAN     NOT NULL DEFAULT true,
    settings        JSONB,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by      UUID,
    last_modified_by UUID
);

CREATE TABLE organization_members (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID        NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    user_id         UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role            VARCHAR(50)  NOT NULL DEFAULT 'MEMBER',
    joined_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    invited_by      UUID        REFERENCES users(id),
    UNIQUE (organization_id, user_id)
);

CREATE TABLE teams (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID        NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    name            VARCHAR(255) NOT NULL,
    description     TEXT,
    avatar_url      TEXT,
    is_active       BOOLEAN     NOT NULL DEFAULT true,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by      UUID,
    last_modified_by UUID,
    UNIQUE (organization_id, name)
);

CREATE TABLE team_members (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    team_id         UUID        NOT NULL REFERENCES teams(id) ON DELETE CASCADE,
    user_id         UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role            VARCHAR(50)  NOT NULL DEFAULT 'MEMBER',
    joined_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (team_id, user_id)
);

-- ============================================================
-- PROJECT MODULE
-- ============================================================

CREATE TABLE projects (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID        NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    team_id         UUID        REFERENCES teams(id) ON DELETE SET NULL,
    owner_id        UUID        REFERENCES users(id) ON DELETE SET NULL,
    key             VARCHAR(20)  NOT NULL,
    name            VARCHAR(255) NOT NULL,
    description     TEXT,
    status          VARCHAR(30)  NOT NULL DEFAULT 'ACTIVE',
    health          VARCHAR(20)  NOT NULL DEFAULT 'ON_TRACK',
    repository_url  TEXT,
    start_date      DATE,
    target_date     DATE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by      UUID,
    last_modified_by UUID,
    UNIQUE (organization_id, key)
);

CREATE TABLE project_members (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id      UUID        NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    user_id         UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role            VARCHAR(50)  NOT NULL DEFAULT 'MEMBER',
    added_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (project_id, user_id)
);

CREATE TABLE milestones (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID        NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    project_id      UUID        NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    name            VARCHAR(255) NOT NULL,
    description     TEXT,
    target_date     DATE,
    status          VARCHAR(30)  NOT NULL DEFAULT 'OPEN',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by      UUID,
    last_modified_by UUID
);

CREATE TABLE sprints (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID        NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    project_id      UUID        NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    name            VARCHAR(255) NOT NULL,
    goal            TEXT,
    status          VARCHAR(20)  NOT NULL DEFAULT 'PLANNING',
    start_date      DATE,
    end_date        DATE,
    velocity_points INTEGER,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by      UUID,
    last_modified_by UUID
);

-- ============================================================
-- TASK MODULE
-- ============================================================

CREATE TABLE tasks (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID        NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    project_id      UUID        NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    sprint_id       UUID        REFERENCES sprints(id) ON DELETE SET NULL,
    milestone_id    UUID        REFERENCES milestones(id) ON DELETE SET NULL,
    parent_task_id  UUID        REFERENCES tasks(id) ON DELETE CASCADE,
    task_number     INTEGER     NOT NULL,
    title           VARCHAR(300) NOT NULL,
    description     TEXT,
    status          VARCHAR(30)  NOT NULL DEFAULT 'BACKLOG',
    priority        VARCHAR(20)  NOT NULL DEFAULT 'MEDIUM',
    type            VARCHAR(20)  NOT NULL DEFAULT 'TASK',
    story_points    INTEGER,
    reporter_id     UUID        REFERENCES users(id) ON DELETE SET NULL,
    due_date        DATE,
    estimated_hours NUMERIC(6,2),
    logged_hours    NUMERIC(6,2) NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by      UUID,
    last_modified_by UUID,
    UNIQUE (project_id, task_number)
);

CREATE TABLE task_assignees (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    task_id         UUID        NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    user_id         UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    assigned_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    assigned_by     UUID        REFERENCES users(id),
    UNIQUE (task_id, user_id)
);

CREATE TABLE task_labels (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    task_id         UUID        NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    label           VARCHAR(100) NOT NULL,
    color           VARCHAR(20),
    UNIQUE (task_id, label)
);

CREATE TABLE task_dependencies (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    task_id         UUID        NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    depends_on_id   UUID        NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    dependency_type VARCHAR(30)  NOT NULL DEFAULT 'BLOCKS',
    UNIQUE (task_id, depends_on_id)
);

CREATE TABLE task_comments (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    task_id         UUID        NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    organization_id UUID        NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    author_id       UUID        REFERENCES users(id) ON DELETE SET NULL,
    body            TEXT        NOT NULL,
    is_edited       BOOLEAN     NOT NULL DEFAULT false,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================
-- DOCUMENT MODULE
-- ============================================================

CREATE TABLE documents (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID        NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    project_id      UUID        REFERENCES projects(id) ON DELETE SET NULL,
    name            VARCHAR(255) NOT NULL,
    file_type       VARCHAR(20)  NOT NULL,
    size_bytes      BIGINT,
    storage_key     TEXT,
    status          VARCHAR(30)  NOT NULL DEFAULT 'UPLOADING',
    description     TEXT,
    tags            TEXT[],
    uploaded_by     UUID        REFERENCES users(id) ON DELETE SET NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by      UUID,
    last_modified_by UUID
);

CREATE TABLE document_versions (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    document_id     UUID        NOT NULL REFERENCES documents(id) ON DELETE CASCADE,
    version_number  INTEGER     NOT NULL,
    storage_key     TEXT        NOT NULL,
    size_bytes      BIGINT,
    change_summary  TEXT,
    created_by      UUID        REFERENCES users(id) ON DELETE SET NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Vector chunks for RAG
CREATE TABLE document_chunks (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    document_id     UUID        NOT NULL REFERENCES documents(id) ON DELETE CASCADE,
    organization_id UUID        NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    chunk_index     INTEGER     NOT NULL,
    content         TEXT        NOT NULL,
    token_count     INTEGER,
    page_number     INTEGER,
    embedding       vector(1536),
    metadata        JSONB,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================
-- KNOWLEDGE / AI CHAT MODULE
-- ============================================================

CREATE TABLE conversations (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID        NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    user_id         UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title           VARCHAR(255),
    agent_type      VARCHAR(50)  NOT NULL DEFAULT 'ASSISTANT',
    context_json    JSONB,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE messages (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    conversation_id UUID        NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    role            VARCHAR(20)  NOT NULL,         -- USER | ASSISTANT | TOOL
    content         TEXT        NOT NULL,
    tool_calls      JSONB,
    citations       JSONB,
    model           VARCHAR(80),
    input_tokens    INTEGER,
    output_tokens   INTEGER,
    latency_ms      INTEGER,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================
-- AI AGENT MODULE
-- ============================================================

CREATE TABLE ai_agents (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID        NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    name            VARCHAR(200) NOT NULL,
    description     TEXT,
    agent_type      VARCHAR(50)  NOT NULL,
    model           VARCHAR(80)  NOT NULL DEFAULT 'gpt-4o',
    temperature     NUMERIC(3,2) NOT NULL DEFAULT 0.2,
    system_prompt   TEXT,
    tools           TEXT[],
    is_active       BOOLEAN     NOT NULL DEFAULT true,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by      UUID
);

CREATE TABLE agent_runs (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID        NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    agent_id        UUID        REFERENCES ai_agents(id) ON DELETE SET NULL,
    user_id         UUID        REFERENCES users(id) ON DELETE SET NULL,
    status          VARCHAR(30)  NOT NULL DEFAULT 'RUNNING',
    input_query     TEXT        NOT NULL,
    output_summary  TEXT,
    trace_steps     JSONB,
    confidence      NUMERIC(4,3),
    input_tokens    INTEGER,
    output_tokens   INTEGER,
    latency_ms      INTEGER,
    error_message   TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    completed_at    TIMESTAMPTZ
);

-- ============================================================
-- WORKFLOW AUTOMATION MODULE
-- ============================================================

CREATE TABLE workflows (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID        NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    project_id      UUID        REFERENCES projects(id) ON DELETE SET NULL,
    name            VARCHAR(255) NOT NULL,
    description     TEXT,
    is_active       BOOLEAN     NOT NULL DEFAULT true,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by      UUID,
    last_modified_by UUID
);

CREATE TABLE workflow_triggers (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    workflow_id     UUID        NOT NULL REFERENCES workflows(id) ON DELETE CASCADE,
    trigger_type    VARCHAR(50)  NOT NULL,   -- EVENT | SCHEDULE | WEBHOOK
    event_type      VARCHAR(100),
    cron_expression VARCHAR(100),
    config          JSONB,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE workflow_conditions (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    workflow_id     UUID        NOT NULL REFERENCES workflows(id) ON DELETE CASCADE,
    field_path      VARCHAR(200) NOT NULL,
    operator        VARCHAR(30)  NOT NULL,
    value           TEXT,
    condition_order INTEGER     NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE workflow_actions (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    workflow_id     UUID        NOT NULL REFERENCES workflows(id) ON DELETE CASCADE,
    action_type     VARCHAR(80)  NOT NULL,
    action_order    INTEGER     NOT NULL DEFAULT 0,
    config          JSONB,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE workflow_runs (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID        NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    workflow_id     UUID        REFERENCES workflows(id) ON DELETE SET NULL,
    status          VARCHAR(30)  NOT NULL DEFAULT 'RUNNING',
    trigger_data    JSONB,
    execution_log   JSONB,
    error_message   TEXT,
    retry_count     INTEGER     NOT NULL DEFAULT 0,
    started_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    completed_at    TIMESTAMPTZ
);

-- ============================================================
-- INTEGRATION MODULE
-- ============================================================

CREATE TABLE integrations (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID        NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    provider        VARCHAR(50)  NOT NULL,   -- GITHUB | SLACK | JIRA | GOOGLE_DRIVE
    display_name    VARCHAR(200),
    status          VARCHAR(30)  NOT NULL DEFAULT 'ACTIVE',
    config          JSONB,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by      UUID,
    UNIQUE (organization_id, provider)
);

CREATE TABLE integration_credentials (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    integration_id  UUID        NOT NULL REFERENCES integrations(id) ON DELETE CASCADE,
    credential_type VARCHAR(50)  NOT NULL,
    encrypted_value TEXT        NOT NULL,
    expires_at      TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE webhook_events (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID        NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    integration_id  UUID        REFERENCES integrations(id) ON DELETE SET NULL,
    provider        VARCHAR(50)  NOT NULL,
    event_type      VARCHAR(100) NOT NULL,
    payload         JSONB       NOT NULL,
    processed       BOOLEAN     NOT NULL DEFAULT false,
    processing_error TEXT,
    received_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    processed_at    TIMESTAMPTZ
);

-- ============================================================
-- NOTIFICATION MODULE
-- ============================================================

CREATE TABLE notifications (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID        NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    user_id         UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type            VARCHAR(60)  NOT NULL,
    title           VARCHAR(255) NOT NULL,
    body            TEXT,
    resource_type   VARCHAR(50),
    resource_id     UUID,
    resource_url    TEXT,
    is_read         BOOLEAN     NOT NULL DEFAULT false,
    metadata        JSONB,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    read_at         TIMESTAMPTZ
);

-- ============================================================
-- ANALYTICS MODULE
-- ============================================================

CREATE TABLE kpi_definitions (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID        NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    name            VARCHAR(200) NOT NULL,
    description     TEXT,
    metric_type     VARCHAR(50)  NOT NULL,
    unit            VARCHAR(50),
    target_value    NUMERIC(12,4),
    is_active       BOOLEAN     NOT NULL DEFAULT true,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by      UUID
);

CREATE TABLE kpi_measurements (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID        NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    kpi_id          UUID        NOT NULL REFERENCES kpi_definitions(id) ON DELETE CASCADE,
    project_id      UUID        REFERENCES projects(id) ON DELETE SET NULL,
    value           NUMERIC(12,4) NOT NULL,
    period_start    TIMESTAMPTZ NOT NULL,
    period_end      TIMESTAMPTZ NOT NULL,
    dimensions      JSONB,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================
-- AUDIT LOG MODULE
-- ============================================================

CREATE TABLE audit_logs (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID        NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    actor_id        UUID        REFERENCES users(id) ON DELETE SET NULL,
    actor_email     VARCHAR(255),
    action          VARCHAR(100) NOT NULL,
    resource_type   VARCHAR(50)  NOT NULL,
    resource_id     UUID,
    resource_name   VARCHAR(255),
    old_values      JSONB,
    new_values      JSONB,
    ip_address      VARCHAR(45),
    user_agent      TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================
-- PERFORMANCE INDEXES
-- ============================================================

-- Users
CREATE INDEX idx_users_email ON users(email);

-- Org members
CREATE INDEX idx_org_members_org ON organization_members(organization_id);
CREATE INDEX idx_org_members_user ON organization_members(user_id);

-- Teams
CREATE INDEX idx_teams_org ON teams(organization_id);
CREATE INDEX idx_team_members_team ON team_members(team_id);
CREATE INDEX idx_team_members_user ON team_members(user_id);

-- Projects
CREATE INDEX idx_projects_org ON projects(organization_id);
CREATE INDEX idx_projects_status ON projects(organization_id, status);

-- Milestones & Sprints
CREATE INDEX idx_milestones_project ON milestones(project_id);
CREATE INDEX idx_sprints_project ON sprints(project_id);
CREATE INDEX idx_sprints_status ON sprints(project_id, status);

-- Tasks (critical for multi-tenant queries)
CREATE INDEX idx_tasks_org_status ON tasks(organization_id, status);
CREATE INDEX idx_tasks_project ON tasks(project_id);
CREATE INDEX idx_tasks_sprint ON tasks(sprint_id);
CREATE INDEX idx_tasks_assignee ON task_assignees(user_id);
CREATE INDEX idx_tasks_due_date ON tasks(due_date) WHERE due_date IS NOT NULL;

-- Documents
CREATE INDEX idx_documents_org ON documents(organization_id);
CREATE INDEX idx_documents_project ON documents(project_id);

-- Document chunks (HNSW index for fast vector search)
CREATE INDEX idx_chunks_org ON document_chunks(organization_id);
CREATE INDEX idx_chunks_document ON document_chunks(document_id);
CREATE INDEX idx_chunks_embedding ON document_chunks USING hnsw (embedding vector_cosine_ops)
    WITH (m = 16, ef_construction = 64);

-- Conversations & Messages
CREATE INDEX idx_conversations_user ON conversations(user_id, organization_id);
CREATE INDEX idx_messages_conversation ON messages(conversation_id);

-- Agent runs
CREATE INDEX idx_agent_runs_org ON agent_runs(organization_id, created_at DESC);

-- Workflows
CREATE INDEX idx_workflows_org ON workflows(organization_id, is_active);
CREATE INDEX idx_workflow_runs_org ON workflow_runs(organization_id, started_at DESC);

-- Integrations
CREATE INDEX idx_integrations_org ON integrations(organization_id);
CREATE INDEX idx_webhook_events_unprocessed ON webhook_events(organization_id, processed, received_at)
    WHERE processed = false;

-- Notifications
CREATE INDEX idx_notifications_user_unread ON notifications(user_id, is_read, created_at DESC)
    WHERE is_read = false;

-- Analytics
CREATE INDEX idx_kpi_measurements_kpi ON kpi_measurements(kpi_id, period_start DESC);

-- Audit Logs
CREATE INDEX idx_audit_logs_org ON audit_logs(organization_id, created_at DESC);
CREATE INDEX idx_audit_logs_actor ON audit_logs(actor_id, created_at DESC);
CREATE INDEX idx_audit_logs_resource ON audit_logs(resource_type, resource_id);
