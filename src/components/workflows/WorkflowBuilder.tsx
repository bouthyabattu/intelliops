'use client';

import React, { useState } from 'react';
import { 
  GitFork, 
  Play, 
  CheckCircle2, 
  AlertCircle, 
  ArrowRight, 
  Plus, 
  Power, 
  Sparkles,
  Clock,
  Settings2
} from 'lucide-react';

interface WorkflowRule {
  id: string;
  name: string;
  trigger: string;
  condition: string;
  action: string;
  isEnabled: boolean;
  lastExecuted: string;
  totalRuns: number;
}

const INITIAL_RULES: WorkflowRule[] = [
  {
    id: 'wf-1',
    name: 'Autonomous Incident Triage on Latency Degradation',
    trigger: 'Prometheus P99 Latency > 2500ms for 3 minutes',
    condition: 'Environment = production AND severity >= P1',
    action: 'Spawn LangGraph Triage Agent & alert #eng-incidents Slack',
    isEnabled: true,
    lastExecuted: '12 mins ago',
    totalRuns: 18
  },
  {
    id: 'wf-2',
    name: 'Canary Rollout & Automated Rollback Guard',
    trigger: 'GitHub PR merged to main branch',
    condition: 'CI test suite = PASSED AND security scan = CLEAN',
    action: 'Deploy 10% canary traffic & monitor error budget for 15 mins',
    isEnabled: true,
    lastExecuted: '18 mins ago',
    totalRuns: 42
  },
  {
    id: 'wf-3',
    name: 'Auto-Task Progression on PR Merge',
    trigger: 'GitHub PR commit references #TASK-ID',
    condition: 'PR status = MERGED',
    action: 'Transition task column to DONE and log author',
    isEnabled: true,
    lastExecuted: '1 hour ago',
    totalRuns: 135
  },
  {
    id: 'wf-4',
    name: 'Circuit Breaker Auto-Trip on Database Saturation',
    trigger: 'HikariCP Active Connections >= 95% for 60 seconds',
    condition: 'Queue length > 10 waiting threads',
    action: 'Enable rate limiting & shed non-critical batch traffic',
    isEnabled: false,
    lastExecuted: '3 days ago',
    totalRuns: 5
  }
];

export function WorkflowBuilder() {
  const [rules, setRules] = useState<WorkflowRule[]>(INITIAL_RULES);

  const toggleRule = (id: string) => {
    setRules(prev => prev.map(r => r.id === id ? { ...r, isEnabled: !r.isEnabled } : r));
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-lg font-bold text-white tracking-tight">Automation Rules & Workflow Engine</h2>
          <p className="text-xs text-slate-400">
            Event-driven operational triggers, AI agent hooks, and automated incident mitigations
          </p>
        </div>

        <button className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-accent-indigo hover:bg-accent-indigo/90 text-white text-xs font-semibold shadow-glow transition-all">
          <Plus className="w-4 h-4" />
          <span>New Rule</span>
        </button>
      </div>

      {/* Rules Flow Cards */}
      <div className="space-y-4">
        {rules.map(rule => (
          <div
            key={rule.id}
            className={`glass-panel p-5 rounded-xl border transition-all ${
              rule.isEnabled ? 'border-surface-border' : 'border-slate-800 opacity-60'
            }`}
          >
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center gap-3">
                <div
                  className={`w-8 h-8 rounded-lg flex items-center justify-center ${
                    rule.isEnabled ? 'bg-accent-indigo/20 text-accent-indigo border border-accent-indigo/30' : 'bg-slate-800 text-slate-500'
                  }`}
                >
                  <GitFork className="w-4 h-4" />
                </div>
                <div>
                  <h3 className="text-sm font-bold text-white">{rule.name}</h3>
                  <div className="flex items-center gap-3 text-[11px] text-slate-400 font-mono mt-0.5">
                    <span>Last run: {rule.lastExecuted}</span>
                    <span>•</span>
                    <span>{rule.totalRuns} total executions</span>
                  </div>
                </div>
              </div>

              <button
                onClick={() => toggleRule(rule.id)}
                className={`flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold transition-all ${
                  rule.isEnabled
                    ? 'bg-accent-emerald/20 text-accent-emerald border border-accent-emerald/30'
                    : 'bg-slate-800 text-slate-400 border border-slate-700'
                }`}
              >
                <Power className="w-3 h-3" />
                <span>{rule.isEnabled ? 'Active' : 'Disabled'}</span>
              </button>
            </div>

            {/* Visual Trigger -> Condition -> Action chain */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-3 items-center">
              {/* Trigger */}
              <div className="p-3 rounded-lg bg-surface border border-surface-border">
                <span className="text-[10px] font-bold uppercase tracking-wider text-accent-cyan block mb-1">
                  1. Trigger Event
                </span>
                <p className="text-xs text-slate-200 font-medium">{rule.trigger}</p>
              </div>

              {/* Condition */}
              <div className="p-3 rounded-lg bg-surface border border-surface-border">
                <span className="text-[10px] font-bold uppercase tracking-wider text-accent-amber block mb-1">
                  2. Condition Filter
                </span>
                <p className="text-xs text-slate-200 font-medium font-mono text-[11px]">{rule.condition}</p>
              </div>

              {/* Action */}
              <div className="p-3 rounded-lg bg-surface border border-surface-border">
                <span className="text-[10px] font-bold uppercase tracking-wider text-accent-emerald block mb-1">
                  3. Automated Action
                </span>
                <p className="text-xs text-slate-200 font-medium">{rule.action}</p>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
