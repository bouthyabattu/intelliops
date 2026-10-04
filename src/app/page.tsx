'use client';

import React, { useState } from 'react';
import { Sidebar } from '@/components/layout/Sidebar';
import { Header } from '@/components/layout/Header';
import { DoraMetricsView } from '@/components/dashboard/DoraMetricsView';
import { KanbanBoard } from '@/components/kanban/KanbanBoard';
import { IncidentCopilot } from '@/components/copilot/IncidentCopilot';
import { KnowledgeBaseView } from '@/components/documents/KnowledgeBaseView';
import { WorkflowBuilder } from '@/components/workflows/WorkflowBuilder';
import { IntegrationsDirectory } from '@/components/integrations/IntegrationsDirectory';
import { ShieldCheck, Key, Lock, Users, Database } from 'lucide-react';

export default function Home() {
  const [currentTab, setCurrentTab] = useState('dashboard');

  return (
    <div className="flex min-h-screen bg-background text-slate-100">
      {/* Fixed Sidebar Navigation */}
      <Sidebar currentTab={currentTab} onSelectTab={setCurrentTab} />

      {/* Main Content Area */}
      <div className="flex-1 ml-64 flex flex-col min-w-0">
        <Header onOpenCopilot={() => setCurrentTab('copilot')} activeIncidentsCount={1} />

        <main className="flex-1 p-8 max-w-7xl w-full mx-auto">
          {currentTab === 'dashboard' && (
            <DoraMetricsView onTriageIncident={() => setCurrentTab('copilot')} />
          )}

          {currentTab === 'kanban' && <KanbanBoard />}

          {currentTab === 'copilot' && <IncidentCopilot />}

          {currentTab === 'documents' && <KnowledgeBaseView />}

          {currentTab === 'workflows' && <WorkflowBuilder />}

          {currentTab === 'integrations' && <IntegrationsDirectory />}

          {currentTab === 'analytics' && (
            <DoraMetricsView onTriageIncident={() => setCurrentTab('copilot')} />
          )}

          {currentTab === 'settings' && (
            <div className="space-y-6">
              <div>
                <h2 className="text-lg font-bold text-white tracking-tight">Tenant & Security Architecture</h2>
                <p className="text-xs text-slate-400">
                  Role-Based Access Control (RBAC), Keycloak OIDC configuration, and audit logging
                </p>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="glass-panel p-5 rounded-xl border border-surface-border">
                  <h3 className="text-xs font-bold text-white flex items-center gap-2 mb-3">
                    <ShieldCheck className="w-4 h-4 text-accent-emerald" />
                    <span>Multi-Tenant Isolation Guarantees</span>
                  </h3>
                  <div className="space-y-2 text-xs text-slate-300">
                    <p>• <strong>Strict Schema / ID Scoping:</strong> Every database query automatically binds <code className="text-accent-cyan">organization_id = :currentTenantId</code>.</p>
                    <p>• <strong>Cross-Tenant Guard:</strong> Spring Security filter chain immediately aborts queries attempting cross-tenant access with HTTP 403.</p>
                    <p>• <strong>Keycloak Realm Sync:</strong> Enterprise SSO configured with OAuth2 Bearer Tokens and JWT claims.</p>
                  </div>
                </div>

                <div className="glass-panel p-5 rounded-xl border border-surface-border">
                  <h3 className="text-xs font-bold text-white flex items-center gap-2 mb-3">
                    <Lock className="w-4 h-4 text-accent-indigo" />
                    <span>Agent Security & Blast Radius Controls</span>
                  </h3>
                  <div className="space-y-2 text-xs text-slate-300">
                    <p>• <strong>Read-Only Tools:</strong> Pod logs and Prometheus queries execute with zero side-effects.</p>
                    <p>• <strong>Write Tools:</strong> Task status updates and circuit breaker activation require tenant member session.</p>
                    <p>• <strong>Destructive Actions:</strong> Kubernetes rollbacks and pod deletions require explicit Human Authorization Gate.</p>
                  </div>
                </div>
              </div>
            </div>
          )}
        </main>
      </div>
    </div>
  );
}
