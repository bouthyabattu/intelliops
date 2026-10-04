'use client';

import React from 'react';
import { Search, Bell, AlertTriangle, ShieldCheck, Terminal, Sparkles } from 'lucide-react';

interface HeaderProps {
  onOpenCopilot: () => void;
  activeIncidentsCount?: number;
}

export function Header({ onOpenCopilot, activeIncidentsCount = 1 }: HeaderProps) {
  return (
    <header className="h-14 border-b border-surface-border bg-surface/80 backdrop-blur-md px-6 flex items-center justify-between sticky top-0 z-20">
      {/* Search Input Bar */}
      <div className="flex items-center gap-3 w-96">
        <div className="relative w-full">
          <Search className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            placeholder="Search tasks, runbooks, logs, commits... (⌘K)"
            className="w-full bg-surface-elevated/70 border border-surface-border rounded-lg pl-9 pr-12 py-1.5 text-xs text-white placeholder-slate-400 focus:outline-none focus:border-accent-indigo transition-all"
          />
          <kbd className="absolute right-2.5 top-1/2 -translate-y-1/2 text-[10px] font-mono px-1.5 py-0.5 rounded bg-slate-800 text-slate-400 border border-slate-700">
            ⌘K
          </kbd>
        </div>
      </div>

      {/* Action Controls & Badges */}
      <div className="flex items-center gap-3">
        {/* Quick Launch Copilot Button */}
        <button
          onClick={onOpenCopilot}
          className="flex items-center gap-2 px-3 py-1.5 rounded-lg bg-gradient-to-r from-accent-indigo to-accent-cyan text-white text-xs font-semibold shadow-glow hover:opacity-95 transition-all"
        >
          <Sparkles className="w-3.5 h-3.5" />
          <span>Ask Incident Copilot</span>
        </button>

        {/* Active Incident Warning Badge */}
        {activeIncidentsCount > 0 && (
          <div className="flex items-center gap-1.5 px-2.5 py-1 rounded-md bg-accent-rose/15 border border-accent-rose/30 text-accent-rose text-xs font-medium animate-pulse-subtle">
            <AlertTriangle className="w-3.5 h-3.5" />
            <span>{activeIncidentsCount} Active Incident (P1)</span>
          </div>
        )}

        {/* Notifications */}
        <button className="w-8 h-8 rounded-lg bg-surface-elevated border border-surface-border flex items-center justify-center text-slate-400 hover:text-white transition-all relative">
          <Bell className="w-4 h-4" />
          <span className="w-2 h-2 rounded-full bg-accent-cyan absolute top-1.5 right-1.5"></span>
        </button>
      </div>
    </header>
  );
}
