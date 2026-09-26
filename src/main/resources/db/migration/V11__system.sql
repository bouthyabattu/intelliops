-- V11: System Notifications, Audit Logs & Activity Feed Schema
-- Enterprise compliance audit trails, real-time user notifications, and team activity feeds

CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    recipient_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) DEFAULT 'INFO' NOT NULL, -- INFO, WARNING, ALERT, SUCCESS
    entity_type VARCHAR(50), -- TASK, INCIDENT, WORKFLOW, AGENT_RUN
    entity_id UUID,
    is_read BOOLEAN DEFAULT FALSE NOT NULL,
    read_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_notifications_recipient ON notifications(recipient_user_id, is_read);

CREATE TABLE IF NOT EXISTS audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    actor_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    actor_email VARCHAR(255),
    action VARCHAR(100) NOT NULL, -- e.g. "TASK_DELETED", "ROLE_UPDATED", "AGENT_TOOL_CALLED"
    resource_type VARCHAR(100) NOT NULL,
    resource_id VARCHAR(100) NOT NULL,
    ip_address VARCHAR(50),
    user_agent TEXT,
    old_values JSONB,
    new_values JSONB,
    status VARCHAR(50) DEFAULT 'SUCCESS' NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_audit_logs_org_time ON audit_logs(organization_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_audit_logs_resource ON audit_logs(resource_type, resource_id);

CREATE TABLE IF NOT EXISTS activity_feed (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    actor_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    action_type VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    target_type VARCHAR(50),
    target_id UUID,
    metadata JSONB DEFAULT '{}'::jsonb,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_activity_org_time ON activity_feed(organization_id, created_at DESC);
