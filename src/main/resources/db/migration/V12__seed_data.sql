-- V12: Production Seed Data for End-to-End Scenario
-- Correlated dataset supporting Incident Investigation Agent & RAG demo

-- 1. Default Organization
INSERT INTO organizations (id, name, slug, plan_tier, is_active)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'Acme Cloud Technologies',
    'acme-cloud',
    'ENTERPRISE',
    TRUE
) ON CONFLICT DO NOTHING;

-- 2. Default Users
INSERT INTO users (id, email, first_name, last_name, is_active, is_email_verified)
VALUES 
(
    '10000000-0000-0000-0000-000000000001',
    'admin@intelliops.ai',
    'Alex',
    'Vance',
    TRUE,
    TRUE
),
(
    '10000000-0000-0000-0000-000000000002',
    'sarah.connor@acme.com',
    'Sarah',
    'Connor',
    TRUE,
    TRUE
),
(
    '10000000-0000-0000-0000-000000000003',
    'david.bowman@acme.com',
    'David',
    'Bowman',
    TRUE,
    TRUE
) ON CONFLICT DO NOTHING;

-- 3. Credentials (BCrypt hash for "admin" and "password")
INSERT INTO user_credentials (user_id, password_hash, is_mfa_enabled)
VALUES 
('10000000-0000-0000-0000-000000000001', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', FALSE),
('10000000-0000-0000-0000-000000000002', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', FALSE),
('10000000-0000-0000-0000-000000000003', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', FALSE)
ON CONFLICT (user_id) DO NOTHING;

-- 4. Organization Roles
INSERT INTO roles (id, organization_id, name, description, is_system_role)
VALUES
('20000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'ADMIN', 'Full Organization Administrator', TRUE),
('20000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 'ENGINEER', 'Software & Infrastructure Engineer', TRUE),
('20000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', 'MANAGER', 'Product & Engineering Manager', TRUE)
ON CONFLICT DO NOTHING;

-- Assign Org Membership
INSERT INTO organization_members (organization_id, user_id, role_id, title)
VALUES 
('00000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', 'VP of Engineering'),
('00000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000002', 'Staff Backend Engineer'),
('00000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000003', '20000000-0000-0000-0000-000000000002', 'Principal DevOps Architect')
ON CONFLICT DO NOTHING;

-- 5. Teams
INSERT INTO teams (id, organization_id, name, description, lead_user_id)
VALUES 
('30000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'Core Platform Engineering', 'Mission-critical services and distributed transactions', '10000000-0000-0000-0000-000000000002'),
('30000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 'Reliability & Infrastructure', 'Kubernetes, CI/CD pipelines, and observability', '10000000-0000-0000-0000-000000000003')
ON CONFLICT DO NOTHING;

-- 6. Projects
INSERT INTO projects (id, organization_id, team_id, key, name, description, status, health, owner_id)
VALUES 
('40000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000001', 'CHECKOUT', 'Checkout Service Architecture', 'High-throughput payment and checkout order flow', 'ACTIVE', 'AT_RISK', '10000000-0000-0000-0000-000000000002'),
('40000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000001', 'PAYMENT', 'Payment Gateway v3', 'Integration with Stripe, Adyen, and idempotency engine', 'ACTIVE', 'ON_TRACK', '10000000-0000-0000-0000-000000000002'),
('40000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000002', 'INTEL', 'IntelliOps Platform', 'Centralized AI-powered operational intelligence layer', 'ACTIVE', 'ON_TRACK', '10000000-0000-0000-0000-000000000001')
ON CONFLICT DO NOTHING;

-- 7. Tasks
INSERT INTO tasks (id, organization_id, project_id, task_number, title, description, status, priority, type, reporter_id)
VALUES 
('50000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000001', 101, 'Investigate checkout-service CI timeout in payment capture', 'CI pipeline timeout observed after PR #842 was merged.', 'IN_PROGRESS', 'CRITICAL', 'INCIDENT', '10000000-0000-0000-0000-000000000001'),
('50000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000001', 102, 'Increase HikariCP connection pool timeout to 45s', 'Hotfix to mitigate thread pool exhaustion during peak load.', 'TODO', 'HIGH', 'BUG', '10000000-0000-0000-0000-000000000002'),
('50000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000002', 201, 'Upgrade payment provider webhook signature verification', 'Enforce HMAC SHA-256 headers on callback endpoints.', 'DONE', 'MEDIUM', 'TASK', '10000000-0000-0000-0000-000000000002'),
('50000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000003', 301, 'Configure pgvector HNSW indexing on document chunks', 'Enable sub-50ms hybrid semantic search over architecture handbooks.', 'DONE', 'HIGH', 'TASK', '10000000-0000-0000-0000-000000000003')
ON CONFLICT DO NOTHING;

-- 8. GitHub Repositories
INSERT INTO github_repositories (id, organization_id, repo_name, full_name, default_branch, html_url)
VALUES 
('60000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'checkout-service', 'acme-cloud/checkout-service', 'main', 'https://github.com/acme-cloud/checkout-service'),
('60000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 'payment-service', 'acme-cloud/payment-service', 'main', 'https://github.com/acme-cloud/payment-service')
ON CONFLICT DO NOTHING;

-- 9. GitHub Pull Request (Directly matching Section 38: PR #842)
INSERT INTO github_pull_requests (id, organization_id, repo_id, pr_number, title, author, state, merge_commit_sha, merged_at, associated_jira_issue)
VALUES 
('70000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', '60000000-0000-0000-0000-000000000001', 842, 'Refactor payment capture retry loop & connection pool', 'sarah.connor@acme.com', 'MERGED', '8f3b219a1', CURRENT_TIMESTAMP - INTERVAL '3 days', 'CHECKOUT-101')
ON CONFLICT DO NOTHING;

-- 10. CI Workflow Runs (Matching Section 38: CI Run #1821 failure)
INSERT INTO ci_workflow_runs (id, organization_id, repo_id, pr_id, run_number, workflow_name, commit_sha, status, conclusion, duration_seconds, logs_summary, started_at, completed_at)
VALUES 
('80000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', '60000000-0000-0000-0000-000000000001', '70000000-0000-0000-0000-000000000001', 1821, 'Main Build & Integration Test', '8f3b219a1', 'COMPLETED', 'FAILURE', 1800, 'HikariPool-1 - Connection is not available, request timed out after 30000ms. Test suite CheckoutServiceIntegrationTest.testConcurrentCheckout failed.', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '3 days' + INTERVAL '30 minutes')
ON CONFLICT DO NOTHING;

-- 11. Production Incident (Matching Section 38: INC-421)
INSERT INTO incidents (id, organization_id, incident_number, title, severity, status, affected_service, root_cause, started_at)
VALUES 
('90000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'INC-421', 'Checkout Service latency spike and order submission timeout', 'P1_CRITICAL', 'INVESTIGATING', 'checkout-service', 'Correlated with PR #842 retry loop exhausting HikariCP connection pool', CURRENT_TIMESTAMP - INTERVAL '2 days')
ON CONFLICT DO NOTHING;

-- 12. Configured AI Agents
INSERT INTO ai_agents (id, organization_id, name, slug, description, system_prompt, model, temperature)
VALUES 
(
    'a0000000-0000-0000-0000-000000000001',
    '00000000-0000-0000-0000-000000000001',
    'Engineering Incident Investigator',
    'incident-agent',
    'Specialized agent that correlates PRs, CI workflow runs, commits, and incidents to determine root cause',
    'You are an elite Staff SRE & Incident Investigator for IntelliOps. When analyzing operational incidents, always inspect recent pull requests, failed CI runs, and commit logs. Every finding must include exact citations and evidence traceable to GitHub and deployment telemetry.',
    'gpt-4o',
    0.15
),
(
    'a0000000-0000-0000-0000-000000000002',
    '00000000-0000-0000-0000-000000000001',
    'Organizational Knowledge Assistant',
    'knowledge-agent',
    'Retrieves verified engineering architecture, runbooks, and task status using hybrid RAG',
    'You are the IntelliOps Knowledge Assistant. You answer technical questions by searching organizational documents and tasks. Never hallucinate; cite document titles and chunk references directly.',
    'gpt-4o',
    0.20
)
ON CONFLICT DO NOTHING;

-- 13. KPIs & Telemetry
INSERT INTO kpi_definitions (id, organization_id, code, name, category, unit, target_value)
VALUES 
('b0000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'CI_SUCCESS_RATE', 'CI Workflow Success Rate', 'ENGINEERING', 'PERCENTAGE', 95.0),
('b0000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 'RAG_FAITHFULNESS', 'AI RAG Faithfulness Score', 'AI_OPERATIONS', 'PERCENTAGE', 95.0),
('b0000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', 'RAG_CONTEXT_RECALL', 'AI Context Recall Rate', 'AI_OPERATIONS', 'PERCENTAGE', 90.0)
ON CONFLICT DO NOTHING;

-- Initial KPI Measurements
INSERT INTO kpi_measurements (kpi_id, value, dimension_label)
VALUES 
('b0000000-0000-0000-0000-000000000001', 78.4, 'checkout-service'),
('b0000000-0000-0000-0000-000000000002', 94.2, 'production-rag'),
('b0000000-0000-0000-0000-000000000003', 91.0, 'production-rag');
