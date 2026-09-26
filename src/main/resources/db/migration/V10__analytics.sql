-- V10: Analytics, Dashboards & KPI Measurements Schema
-- Engineering metrics, deployment reliability, AI telemetry, and custom dashboards

CREATE TABLE IF NOT EXISTS kpi_definitions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    code VARCHAR(100) NOT NULL, -- e.g. "DORA_DEPLOY_FREQ", "CI_SUCCESS_RATE", "RAG_FAITHFULNESS"
    name VARCHAR(200) NOT NULL,
    category VARCHAR(50) NOT NULL, -- ENGINEERING, AI_OPERATIONS, BUSINESS, INCIDENT
    unit VARCHAR(50) DEFAULT 'PERCENTAGE' NOT NULL,
    target_value NUMERIC(10, 2),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uq_kpi_org_code UNIQUE (organization_id, code)
);

CREATE TABLE IF NOT EXISTS kpi_measurements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    kpi_id UUID NOT NULL REFERENCES kpi_definitions(id) ON DELETE CASCADE,
    measured_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    value NUMERIC(12, 4) NOT NULL,
    dimension_label VARCHAR(100),
    metadata JSONB DEFAULT '{}'::jsonb
);

CREATE INDEX IF NOT EXISTS idx_kpi_measurements_kpi_time ON kpi_measurements(kpi_id, measured_at);

CREATE TABLE IF NOT EXISTS dashboards (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    is_default BOOLEAN DEFAULT FALSE NOT NULL,
    created_by UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS dashboard_widgets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    dashboard_id UUID NOT NULL REFERENCES dashboards(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    widget_type VARCHAR(50) NOT NULL, -- LINE_CHART, BAR_CHART, METRIC_CARD, TABLE, GAUGE
    query_definition JSONB NOT NULL,
    grid_x INTEGER DEFAULT 0 NOT NULL,
    grid_y INTEGER DEFAULT 0 NOT NULL,
    grid_w INTEGER DEFAULT 6 NOT NULL,
    grid_h INTEGER DEFAULT 4 NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);
