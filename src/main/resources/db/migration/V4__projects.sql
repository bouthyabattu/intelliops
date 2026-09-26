-- V4: Project Management Schema
-- Projects, Roadmaps, Milestones, Sprints, and Member assignments

CREATE TABLE IF NOT EXISTS projects (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    team_id UUID REFERENCES teams(id) ON DELETE SET NULL,
    key VARCHAR(20) NOT NULL, -- e.g. "INTEL", "CHECKOUT"
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) DEFAULT 'ACTIVE' NOT NULL, -- PLANNING, ACTIVE, PAUSED, COMPLETED, ARCHIVED
    health VARCHAR(50) DEFAULT 'ON_TRACK' NOT NULL, -- ON_TRACK, AT_RISK, CRITICAL
    owner_id UUID REFERENCES users(id) ON DELETE SET NULL,
    start_date DATE,
    target_date DATE,
    repository_url TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uq_projects_org_key UNIQUE (organization_id, key)
);

CREATE INDEX IF NOT EXISTS idx_projects_org ON projects(organization_id);
CREATE INDEX IF NOT EXISTS idx_projects_status ON projects(status);
CREATE INDEX IF NOT EXISTS idx_projects_health ON projects(health);

CREATE TABLE IF NOT EXISTS project_members (
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(50) DEFAULT 'CONTRIBUTOR' NOT NULL, -- LEAD, CONTRIBUTOR, OBSERVER
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (project_id, user_id)
);

CREATE TABLE IF NOT EXISTS milestones (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    due_date DATE NOT NULL,
    status VARCHAR(50) DEFAULT 'OPEN' NOT NULL, -- OPEN, COMPLETED, DELAYED
    progress_percentage INTEGER DEFAULT 0 NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_milestones_project ON milestones(project_id);

CREATE TABLE IF NOT EXISTS sprints (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    name VARCHAR(150) NOT NULL,
    goal TEXT,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(50) DEFAULT 'PLANNED' NOT NULL, -- PLANNED, ACTIVE, COMPLETED
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_sprints_project ON sprints(project_id);
