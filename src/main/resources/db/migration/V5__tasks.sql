-- V5: Task & Issue Management Schema
-- Tasks, subtasks, priorities, labels, assignments, dependencies, and audit history

CREATE TABLE IF NOT EXISTS tasks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    sprint_id UUID REFERENCES sprints(id) ON DELETE SET NULL,
    milestone_id UUID REFERENCES milestones(id) ON DELETE SET NULL,
    parent_task_id UUID REFERENCES tasks(id) ON DELETE CASCADE,
    task_number INTEGER NOT NULL,
    title VARCHAR(300) NOT NULL,
    description TEXT,
    status VARCHAR(50) DEFAULT 'BACKLOG' NOT NULL, -- BACKLOG, TODO, IN_PROGRESS, IN_REVIEW, DONE, BLOCKED
    priority VARCHAR(50) DEFAULT 'MEDIUM' NOT NULL, -- LOW, MEDIUM, HIGH, URGENT, CRITICAL
    type VARCHAR(50) DEFAULT 'TASK' NOT NULL, -- TASK, BUG, STORY, EPIC, INCIDENT
    story_points INTEGER,
    reporter_id UUID REFERENCES users(id) ON DELETE SET NULL,
    due_date DATE,
    estimated_hours NUMERIC(6, 2),
    logged_hours NUMERIC(6, 2) DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uq_tasks_project_number UNIQUE (project_id, task_number)
);

CREATE INDEX IF NOT EXISTS idx_tasks_org ON tasks(organization_id);
CREATE INDEX IF NOT EXISTS idx_tasks_project ON tasks(project_id);
CREATE INDEX IF NOT EXISTS idx_tasks_status ON tasks(status);
CREATE INDEX IF NOT EXISTS idx_tasks_priority ON tasks(priority);
CREATE INDEX IF NOT EXISTS idx_tasks_parent ON tasks(parent_task_id);

CREATE TABLE IF NOT EXISTS task_assignees (
    task_id UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    assigned_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (task_id, user_id)
);

CREATE TABLE IF NOT EXISTS task_labels (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    color_hex VARCHAR(20) DEFAULT '#6366F1' NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uq_task_labels_org_name UNIQUE (organization_id, name)
);

CREATE TABLE IF NOT EXISTS task_label_assignments (
    task_id UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    label_id UUID NOT NULL REFERENCES task_labels(id) ON DELETE CASCADE,
    PRIMARY KEY (task_id, label_id)
);

CREATE TABLE IF NOT EXISTS task_dependencies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    task_id UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    depends_on_task_id UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    type VARCHAR(50) DEFAULT 'BLOCKS' NOT NULL, -- BLOCKS, RELATES_TO, DUPLICATES
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uq_task_dependencies UNIQUE (task_id, depends_on_task_id)
);

CREATE TABLE IF NOT EXISTS task_comments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    task_id UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    content TEXT NOT NULL,
    is_internal BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_task_comments_task ON task_comments(task_id);

CREATE TABLE IF NOT EXISTS task_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    task_id UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    field_changed VARCHAR(100) NOT NULL,
    old_value TEXT,
    new_value TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_task_history_task ON task_history(task_id);
