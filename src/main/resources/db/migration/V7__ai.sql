-- V7: AI Conversations, Agents & Execution Runs Schema
-- Configurable AI agents, tool registry, execution history, and trace steps

CREATE TABLE IF NOT EXISTS conversations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(300) DEFAULT 'New Operational Inquiry' NOT NULL,
    model VARCHAR(100) DEFAULT 'gpt-4o' NOT NULL,
    system_prompt TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_conversations_org ON conversations(organization_id);
CREATE INDEX IF NOT EXISTS idx_conversations_user ON conversations(user_id);

CREATE TABLE IF NOT EXISTS messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    conversation_id UUID NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    sender_type VARCHAR(50) NOT NULL, -- USER, ASSISTANT, SYSTEM, TOOL
    content TEXT NOT NULL,
    citations JSONB DEFAULT '[]'::jsonb, -- Array of referenced chunks/PRs/incidents
    tool_calls JSONB DEFAULT '[]'::jsonb,
    tokens_used INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_messages_conversation ON messages(conversation_id);

CREATE TABLE IF NOT EXISTS ai_agents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    name VARCHAR(150) NOT NULL,
    slug VARCHAR(100) NOT NULL,
    description TEXT,
    system_prompt TEXT NOT NULL,
    model VARCHAR(100) DEFAULT 'gpt-4o' NOT NULL,
    temperature NUMERIC(3, 2) DEFAULT 0.20 NOT NULL,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uq_ai_agents_org_slug UNIQUE (organization_id, slug)
);

CREATE TABLE IF NOT EXISTS agent_tools (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    agent_id UUID NOT NULL REFERENCES ai_agents(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    parameters_schema JSONB NOT NULL,
    classification VARCHAR(50) DEFAULT 'READ' NOT NULL, -- READ, WRITE, DESTRUCTIVE
    is_enabled BOOLEAN DEFAULT TRUE NOT NULL,
    requires_approval BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uq_agent_tools UNIQUE (agent_id, name)
);

CREATE TABLE IF NOT EXISTS agent_runs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    agent_id UUID NOT NULL REFERENCES ai_agents(id) ON DELETE CASCADE,
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    input_query TEXT NOT NULL,
    status VARCHAR(50) DEFAULT 'RUNNING' NOT NULL, -- RUNNING, COMPLETED, FAILED, REQUIRES_APPROVAL
    final_output TEXT,
    total_tokens INTEGER DEFAULT 0,
    total_latency_ms INTEGER DEFAULT 0,
    error_message TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    completed_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_agent_runs_org ON agent_runs(organization_id);
CREATE INDEX IF NOT EXISTS idx_agent_runs_agent ON agent_runs(agent_id);

CREATE TABLE IF NOT EXISTS agent_steps (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    run_id UUID NOT NULL REFERENCES agent_runs(id) ON DELETE CASCADE,
    step_number INTEGER NOT NULL,
    tool_name VARCHAR(100),
    tool_input JSONB,
    tool_output JSONB,
    observation TEXT,
    latency_ms INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_agent_steps_run ON agent_steps(run_id);
