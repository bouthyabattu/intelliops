'use client';

import React from 'react';
import { 
  LayoutDashboard, 
  KanbanSquare, 
  Bot, 
  BookOpen, 
  GitFork, 
  Plug2, 
  BarChart3, 
  Settings, 
  Layers,
  ChevronDown,
  ShieldCheck,
  Activity
} from 'lucide-react';

interface SidebarProps {
  currentTab: string;
  onSelectTab: (tab: string) => void;
}

export function Sidebar({ currentTab, onSelectTab }: SidebarProps) {
  const navItems = [
    { id: 'dashboard', label: 'Executive Ops', icon: LayoutDashboard, badge: 'Live' },
    { id: 'kanban', label: 'Kanban & Tasks', icon: KanbanSquare, count: 24 },
    { id: 'copilot', label: 'AI Incident Copilot', icon: Bot, highlight: true },
    { id: 'documents', label: 'Knowledge & RAG', icon: BookOpen },
    { id: 'workflows', label: 'Automation Rules', icon: GitFork, count: 8 },
    { id: 'analytics', label: 'DORA & Telemetry', icon: BarChart3 },
    { id: 'integrations', label: 'Integrations Hub', icon: Plug2, count: 6 },
    { id: 'settings', label: 'Tenant & Security', icon: Settings },
  ];

  return (
    <aside className="w-64 bg-surface border-r border-surface-border flex flex-col h-screen fixed left-0 top-0 z-30 select-none">
      {/* Brand & Organization */}
      <div className="p-4 border-b border-surface-border flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-accent-indigo to-accent-cyan flex items-center justify-center shadow-glow">
            <Layers className="w-5 h-5 text-white" />
          </div>
          <div>
            <h1 className="text-sm font-bold tracking-tight text-white flex items-center gap-1.5">
              IntelliOps
              <span className="text-[10px] uppercase tracking-wider px-1.5 py-0.5 rounded bg-accent-indigo/20 text-accent-indigo font-semibold border border-accent-indigo/30">
                v1.0
              </span>
            </h1>
            <p className="text-xs text-slate-400 font-mono">Acme Corp Enterprise</p>
          </div>
        </div>
      </div>

      {/* Cluster Health Pill */}
      <div className="px-4 py-2.5 bg-surface-elevated/50 border-b border-surface-border/50 flex items-center justify-between text-xs">
        <div className="flex items-center gap-2">
          <span className="relative flex h-2 w-2">
            <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-accent-emerald opacity-75"></span>
            <span className="relative inline-flex rounded-full h-2 w-2 bg-accent-emerald"></span>
          </span>
          <span className="text-slate-300 font-medium">Cluster Healthy</span>
        </div>
        <span className="text-[11px] text-slate-400 font-mono">99.98% SLA</span>
      </div>

      {/* Navigation Links */}
      <nav className="flex-1 px-3 py-4 space-y-1.5 overflow-y-auto">
        {navItems.map((item) => {
          const Icon = item.icon;
          const isActive = currentTab === item.id;
          return (
            <button
              key={item.id}
              onClick={() => onSelectTab(item.id)}
              className={`w-full flex items-center justify-between px-3 py-2.5 rounded-lg text-xs font-medium transition-all ${
                isActive
                  ? 'bg-accent-indigo text-white shadow-glow'
                  : 'text-slate-300 hover:text-white hover:bg-surface-elevated'
              } ${item.highlight && !isActive ? 'border border-accent-cyan/30 text-accent-cyan hover:bg-accent-cyan/10' : ''}`}
            >
              <div className="flex items-center gap-3">
                <Icon className={`w-4 h-4 ${isActive ? 'text-white' : item.highlight ? 'text-accent-cyan' : 'text-slate-400'}`} />
                <span>{item.label}</span>
              </div>
              {item.badge && (
                <span className="text-[10px] font-semibold px-1.5 py-0.5 rounded-full bg-accent-emerald/20 text-accent-emerald">
                  {item.badge}
                </span>
              )}
              {item.count && (
                <span className="text-[10px] font-mono px-1.5 py-0.5 rounded bg-slate-800 text-slate-400">
                  {item.count}
                </span>
              )}
            </button>
          );
        })}
      </nav>

      {/* Tenant Context Footer */}
      <div className="p-4 border-t border-surface-border bg-surface-elevated/30">
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 rounded-full bg-slate-700 border border-slate-600 flex items-center justify-center font-bold text-xs text-accent-cyan">
            JD
          </div>
          <div className="flex-1 min-w-0">
            <p className="text-xs font-medium text-white truncate">John Doe (Lead SRE)</p>
            <p className="text-[11px] text-slate-400 truncate">john@acme-corp.internal</p>
          </div>
        </div>
      </div>
    </aside>
  );
}
