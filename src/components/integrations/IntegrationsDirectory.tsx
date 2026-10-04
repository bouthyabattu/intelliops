'use client';

import React, { useState } from 'react';
import { 
  Plug2, 
  CheckCircle2, 
  RefreshCw, 
  ExternalLink, 
  Send, 
  ShieldCheck, 
  Terminal,
  Activity
} from 'lucide-react';

interface IntegrationItem {
  id: string;
  name: string;
  category: string;
  status: 'CONNECTED' | 'DISCONNECTED';
  eventsProcessed: string;
  lastSync: string;
}

const INTEGRATIONS: IntegrationItem[] = [
  {
    id: 'int-1',
    name: 'GitHub Enterprise',
    category: 'Source Control & CI/CD',
    status: 'CONNECTED',
    eventsProcessed: '14,280 commits & PRs',
    lastSync: 'Real-time Webhook'
  },
  {
    id: 'int-2',
    name: 'Jira Software Cloud',
    category: 'Issue Tracking',
    status: 'CONNECTED',
    eventsProcessed: '3,840 issues synced',
    lastSync: '2 mins ago'
  },
  {
    id: 'int-3',
    name: 'Slack Workspaces (#eng-alerts)',
    category: 'Collaboration & Incident Comms',
    status: 'CONNECTED',
    eventsProcessed: '890 notifications dispatched',
    lastSync: 'Active Bot Gateway'
  },
  {
    id: 'int-4',
    name: 'PagerDuty On-Call Orchestrator',
    category: 'Alert Routing',
    status: 'CONNECTED',
    eventsProcessed: '12 active escalations',
    lastSync: 'Active v2 API'
  },
  {
    id: 'int-5',
    name: 'Prometheus & OpenTelemetry Collector',
    category: 'Metrics & Distributed Tracing',
    status: 'CONNECTED',
    eventsProcessed: '1.2M metrics / min',
    lastSync: 'Live Scrape (15s)'
  },
  {
    id: 'int-6',
    name: 'PostgreSQL 16 + pgvector',
    category: 'Vector Database & Metadata',
    status: 'CONNECTED',
    eventsProcessed: '60 document vectors',
    lastSync: 'Sub-5ms query latency'
  }
];

export function IntegrationsDirectory() {
  const [testPayloadResult, setTestPayloadResult] = useState<string | null>(null);
  const [isSending, setIsSending] = useState(false);

  const handleSimulateWebhook = async () => {
    setIsSending(true);
    await new Promise(r => setTimeout(r, 600));
    setTestPayloadResult(
      `[HTTP 200 OK] Simulated webhook received from GitHub (event: 'pull_request.merged', sha: '8c92a10'). Auto-transitioned Task #104 to DONE and dispatched Slack deployment announcement.`
    );
    setIsSending(false);
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-lg font-bold text-white tracking-tight">Enterprise Integrations Hub</h2>
          <p className="text-xs text-slate-400">
            Bi-directional connectors syncing Jira, GitHub, Slack, and observability streams into IntelliOps
          </p>
        </div>

        <button
          onClick={handleSimulateWebhook}
          disabled={isSending}
          className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-surface-elevated hover:bg-slate-700 border border-surface-border text-white text-xs font-semibold transition-all"
        >
          <Send className="w-3.5 h-3.5 text-accent-cyan" />
          <span>{isSending ? 'Simulating...' : 'Test Inbound Webhook'}</span>
        </button>
      </div>

      {testPayloadResult && (
        <div className="p-3 rounded-lg bg-surface border border-accent-emerald/40 text-xs text-slate-300 font-mono flex items-start gap-2">
          <CheckCircle2 className="w-4 h-4 text-accent-emerald shrink-0 mt-0.5" />
          <span>{testPayloadResult}</span>
        </div>
      )}

      {/* Integration Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {INTEGRATIONS.map(item => (
          <div
            key={item.id}
            className="glass-panel p-5 rounded-xl border border-surface-border glow-card flex flex-col justify-between"
          >
            <div>
              <div className="flex items-center justify-between mb-3">
                <span className="text-[10px] uppercase font-bold tracking-wider text-slate-400 font-mono">
                  {item.category}
                </span>
                <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-accent-emerald/20 text-accent-emerald border border-accent-emerald/30 flex items-center gap-1">
                  <CheckCircle2 className="w-3 h-3" /> Connected
                </span>
              </div>

              <h3 className="text-sm font-bold text-white mb-1">{item.name}</h3>
              <p className="text-xs text-slate-400 font-mono mb-4">{item.eventsProcessed}</p>
            </div>

            <div className="pt-3 border-t border-surface-border/60 flex items-center justify-between text-[11px] text-slate-400">
              <span>Sync: <strong className="text-slate-300">{item.lastSync}</strong></span>
              <button className="text-accent-indigo hover:text-white flex items-center gap-1">
                <span>Manage</span>
                <ExternalLink className="w-3 h-3" />
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
