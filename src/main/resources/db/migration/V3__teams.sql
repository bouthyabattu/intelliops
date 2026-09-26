-- V3: Team Management Schema
-- Teams within organizations and member assignments

CREATE TABLE IF NOT EXISTS teams (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    lead_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uq_team_org_name UNIQUE (organization_id, name)
);

CREATE INDEX IF NOT EXISTS idx_teams_org ON teams(organization_id);

CREATE TABLE IF NOT EXISTS team_members (
    team_id UUID NOT NULL REFERENCES teams(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(50) DEFAULT 'MEMBER' NOT NULL, -- LEAD, MEMBER
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (team_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_team_members_user ON team_members(user_id);
