'use client';

import React from 'react';
import { 
  Rocket, 
  Clock, 
  Activity, 
  ShieldAlert, 
  CheckCircle2, 
  TrendingUp, 
  TrendingDown, 
  AlertTriangle,
  Server,
  Database,
  Radio,
  Cpu,
  ArrowRight
} from 'lucide-react';

interface DoraMetricsViewProps {
  onTriageIncident: () => void;
}

export function DoraMetricsView({ onTriageIncident }: DoraMetricsViewProps) {
  const doraCards = [
    {
      title: 'Deployment Frequency',
      value: '4.2 / day',
      tier: 'ELITE',
      change: '+18.5%',
      isPositive: true,
      description: 'Production releases per calendar day across services',
      icon: Rocket,
      color: 'from-accent-indigo to-blue-600',
    },
    {
      title: 'Lead Time for Changes',
      value: '1.4 hours',
      tier: 'ELITE',
      change: '-24.0%',
      isPositive: true,
      description: 'Time from commit push to successful prod verification',
      icon: Clock,
      color: 'from-accent-cyan to-teal-600',
    },
    {
      title: 'Mean Time to Restore (MTTR)',
      value: '18 mins',
      tier: 'ELITE',
      change: '-35.2%',
      isPositive: true,
      description: 'Average time to remediate and close P0/P1 incidents',
      icon: Activity,
      color: 'from-accent-emerald to-green-600',
    },
    {
      title: 'Change Failure Rate',
      value: '3.4%',
      tier: 'HIGH',
      change: '-1.2%',
      isPositive: true,
      description: 'Percentage of deployments requiring hotfix or rollback',
      icon: ShieldAlert,
      color: 'from-accent-amber to-orange-600',
    },
  ];

  const serviceNodes = [
    { name: 'core-service', type: 'Spring Boot 3.2', status: 'WARN', latency: '48ms', memory: '512MB / 1GB', icon: Server, alert: 'HikariCP pool near capacity (18/20)' },
    { name: 'ai-service', type: 'FastAPI / LangGraph', status: 'HEALTHY', latency: '112ms', memory: '340MB / 1GB', icon: Cpu },
    { name: 'realtime-service', type: 'Node.js WebSocket', status: 'HEALTHY', latency: '6ms', memory: '128MB / 512MB', icon: Radio },
    { name: 'postgres-pgvector', type: 'PostgreSQL 16', status: 'HEALTHY', latency: '2ms', memory: '780MB / 2GB', icon: Database },
  ];

  const weeklyTrend = [
    { day: 'Mon', deploys: 3, leadTime: 2.4, failure: 0 },
    { day: 'Tue', deploys: 5, leadTime: 1.8, failure: 5.2 },
    { day: 'Wed', deploys: 4, leadTime: 1.5, failure: 0 },
    { day: 'Thu', deploys: 6, leadTime: 2.1, failure: 4.1 },
    { day: 'Fri', deploys: 8, leadTime: 1.2, failure: 0 },
    { day: 'Sat', deploys: 2, leadTime: 0.8, failure: 0 },
    { day: 'Sun', deploys: 1, leadTime: 1.0, failure: 3.8 },
  ];

  return (
    <div className="space-y-6">
      {/* Active Incident Warning Alert Banner */}
      <div className="p-4 rounded-xl bg-gradient-to-r from-red-950/40 via-red-900/20 to-surface-elevated border border-accent-rose/40 flex items-center justify-between shadow-glow">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-lg bg-accent-rose/20 border border-accent-rose/40 flex items-center justify-center text-accent-rose">
            <AlertTriangle className="w-5 h-5 animate-pulse" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <span className="text-xs font-bold px-2 py-0.5 rounded bg-accent-rose text-white uppercase tracking-wider">
                P1 Incident Active
              </span>
              <span className="text-xs text-slate-400 font-mono">INC-8492</span>
              <span className="text-xs text-slate-400">• Detected 12 mins ago</span>
            </div>
            <h3 className="text-sm font-semibold text-white mt-1">
              Surge in HTTP 504 Gateway Timeouts on /api/v1/tasks (HikariPool-1 exhaustion)
            </h3>
          </div>
        </div>

        <button
          onClick={onTriageIncident}
          className="flex items-center gap-2 px-4 py-2 rounded-lg bg-accent-rose hover:bg-accent-rose/90 text-white text-xs font-semibold shadow-glow transition-all"
        >
          <span>Run AI Root Cause Triage</span>
          <ArrowRight className="w-3.5 h-3.5" />
        </button>
      </div>

      {/* DORA 4 Metric Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {doraCards.map((card, idx) => {
          const Icon = card.icon;
          return (
            <div
              key={idx}
              className="glass-panel p-5 rounded-xl border border-surface-border relative overflow-hidden glow-card"
            >
              <div className="flex items-center justify-between mb-3">
                <span className="text-xs font-medium text-slate-400">{card.title}</span>
                <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-accent-emerald/20 text-accent-emerald border border-accent-emerald/30 font-mono">
                  {card.tier}
                </span>
              </div>

              <div className="flex items-baseline justify-between mb-2">
                <span className="text-2xl font-bold tracking-tight text-white font-mono">{card.value}</span>
                <span className={`text-xs font-semibold flex items-center gap-0.5 ${card.isPositive ? 'text-accent-emerald' : 'text-accent-rose'}`}>
                  {card.isPositive ? <TrendingUp className="w-3.5 h-3.5" /> : <TrendingDown className="w-3.5 h-3.5" />}
                  {card.change}
                </span>
              </div>

              <p className="text-[11px] text-slate-400 leading-relaxed line-clamp-2">{card.description}</p>
            </div>
          );
        })}
      </div>

      {/* Two Column Layout: Velocity Chart & Microservice Cluster */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Left Column: 7-Day Velocity & Deployment Trend */}
        <div className="lg:col-span-2 glass-panel p-5 rounded-xl border border-surface-border">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h2 className="text-sm font-bold text-white">Engineering Velocity & Deployment Volume</h2>
              <p className="text-xs text-slate-400">Weekly cadence across microservice repositories</p>
            </div>
            <div className="flex items-center gap-2">
              <span className="text-xs text-slate-400 flex items-center gap-1.5">
                <span className="w-2.5 h-2.5 rounded-sm bg-accent-indigo"></span> Deploys
              </span>
              <span className="text-xs text-slate-400 flex items-center gap-1.5 ml-2">
                <span className="w-2.5 h-2.5 rounded-sm bg-accent-cyan"></span> Lead Time (hrs)
              </span>
            </div>
          </div>

          {/* Bar Chart Visualization */}
          <div className="h-56 flex items-end justify-between gap-4 pt-8 pb-2 px-2 border-b border-surface-border">
            {weeklyTrend.map((item, idx) => {
              const heightPct = Math.round((item.deploys / 8) * 100);
              return (
                <div key={idx} className="flex-1 flex flex-col items-center gap-2 group">
                  <span className="text-[10px] font-mono text-slate-400 opacity-0 group-hover:opacity-100 transition-opacity">
                    {item.deploys} dep
                  </span>
                  <div className="w-full max-w-[42px] bg-surface-elevated rounded-t-md relative flex items-end h-40 overflow-hidden">
                    <div
                      style={{ height: `${heightPct}%` }}
                      className="w-full bg-gradient-to-t from-accent-indigo to-accent-cyan rounded-t-md transition-all duration-500 group-hover:brightness-125"
                    ></div>
                  </div>
                  <span className="text-xs font-medium text-slate-400">{item.day}</span>
                </div>
              );
            })}
          </div>

          <div className="mt-4 flex items-center justify-between text-xs text-slate-400">
            <span>Weekly Total: <strong className="text-white font-mono">29 Releases</strong></span>
            <span>Avg Lead Time: <strong className="text-accent-cyan font-mono">1.43 Hours</strong></span>
            <span>Unplanned Outages: <strong className="text-accent-emerald font-mono">0.00%</strong></span>
          </div>
        </div>

        {/* Right Column: Microservice Health Nodes */}
        <div className="glass-panel p-5 rounded-xl border border-surface-border flex flex-col">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-sm font-bold text-white">Cluster Health</h2>
            <span className="text-xs text-accent-emerald font-medium flex items-center gap-1">
              <CheckCircle2 className="w-3.5 h-3.5" /> 3 of 4 Nominal
            </span>
          </div>

          <div className="space-y-3 flex-1">
            {serviceNodes.map((node, idx) => {
              const Icon = node.icon;
              const isWarning = node.status === 'WARN';
              return (
                <div
                  key={idx}
                  className={`p-3 rounded-lg border transition-all ${
                    isWarning
                      ? 'bg-amber-950/20 border-accent-amber/40'
                      : 'bg-surface-elevated/40 border-surface-border'
                  }`}
                >
                  <div className="flex items-center justify-between mb-1">
                    <div className="flex items-center gap-2">
                      <Icon className={`w-4 h-4 ${isWarning ? 'text-accent-amber' : 'text-accent-indigo'}`} />
                      <span className="text-xs font-semibold text-white font-mono">{node.name}</span>
                    </div>
                    <span
                      className={`text-[10px] font-bold px-1.5 py-0.2 rounded ${
                        isWarning
                          ? 'bg-accent-amber/20 text-accent-amber'
                          : 'bg-accent-emerald/20 text-accent-emerald'
                      }`}
                    >
                      {node.status}
                    </span>
                  </div>

                  <div className="flex items-center justify-between text-[11px] text-slate-400 mt-2">
                    <span>{node.type}</span>
                    <span className="font-mono">{node.latency} • {node.memory}</span>
                  </div>

                  {node.alert && (
                    <div className="mt-2 text-[10px] text-accent-amber font-medium bg-accent-amber/10 px-2 py-0.5 rounded">
                      ⚠️ {node.alert}
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        </div>
      </div>
    </div>
  );
}
