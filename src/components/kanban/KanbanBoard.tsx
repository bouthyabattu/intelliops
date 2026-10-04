'use client';

import React, { useState } from 'react';
import { 
  Plus, 
  MoreHorizontal, 
  Clock, 
  CheckCircle, 
  AlertCircle, 
  Bug, 
  Bookmark, 
  Flame, 
  Filter,
  ArrowRight
} from 'lucide-react';

interface TaskItem {
  id: string;
  taskNumber: number;
  title: string;
  status: 'BACKLOG' | 'TODO' | 'IN_PROGRESS' | 'IN_REVIEW' | 'DONE';
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT' | 'CRITICAL';
  type: 'TASK' | 'BUG' | 'STORY' | 'INCIDENT';
  storyPoints: number;
  assignee: string;
  estimatedHours: number;
}

const INITIAL_TASKS: TaskItem[] = [
  {
    id: 't-1',
    taskNumber: 101,
    title: 'Migrate core HikariCP pool configuration to Spring Boot 3.2 defaults',
    status: 'IN_PROGRESS',
    priority: 'HIGH',
    type: 'TASK',
    storyPoints: 5,
    assignee: 'Alice M.',
    estimatedHours: 4
  },
  {
    id: 't-2',
    taskNumber: 102,
    title: 'PostgreSQL connection starvation under high concurrency on task list query',
    status: 'IN_PROGRESS',
    priority: 'URGENT',
    type: 'INCIDENT',
    storyPoints: 8,
    assignee: 'Bob K.',
    estimatedHours: 6
  },
  {
    id: 't-3',
    taskNumber: 103,
    title: 'Implement LangGraph autonomous incident triage agent with log inspection',
    status: 'IN_REVIEW',
    priority: 'HIGH',
    type: 'STORY',
    storyPoints: 8,
    assignee: 'Carol L.',
    estimatedHours: 12
  },
  {
    id: 't-4',
    taskNumber: 104,
    title: 'Setup pgvector HNSW indexing on document chunks for sub-5ms retrieval',
    status: 'DONE',
    priority: 'MEDIUM',
    type: 'TASK',
    storyPoints: 3,
    assignee: 'Alice M.',
    estimatedHours: 3
  },
  {
    id: 't-5',
    taskNumber: 105,
    title: 'Fix token refresh race condition when multiple requests trigger simultaneously',
    status: 'TODO',
    priority: 'HIGH',
    type: 'BUG',
    storyPoints: 5,
    assignee: 'Dave R.',
    estimatedHours: 5
  },
  {
    id: 't-6',
    taskNumber: 106,
    title: 'Design visual workflow canvas for triggering Slack alerts on P99 latency spikes',
    status: 'BACKLOG',
    priority: 'MEDIUM',
    type: 'STORY',
    storyPoints: 5,
    assignee: 'Eve S.',
    estimatedHours: 8
  },
  {
    id: 't-7',
    taskNumber: 107,
    title: 'Add OpenTelemetry trace instrumentation on Spring Security filter chain',
    status: 'TODO',
    priority: 'LOW',
    type: 'TASK',
    storyPoints: 2,
    assignee: 'Bob K.',
    estimatedHours: 2
  }
];

export function KanbanBoard() {
  const [tasks, setTasks] = useState<TaskItem[]>(INITIAL_TASKS);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [newTitle, setNewTitle] = useState('');
  const [newPriority, setNewPriority] = useState<TaskItem['priority']>('MEDIUM');
  const [newType, setNewType] = useState<TaskItem['type']>('TASK');
  const [newPoints, setNewPoints] = useState(3);

  const columns: { key: TaskItem['status']; label: string; color: string }[] = [
    { key: 'BACKLOG', label: 'Backlog', color: 'border-slate-700' },
    { key: 'TODO', label: 'To Do', color: 'border-blue-500/40' },
    { key: 'IN_PROGRESS', label: 'In Progress', color: 'border-accent-indigo' },
    { key: 'IN_REVIEW', label: 'In Review', color: 'border-accent-purple' },
    { key: 'DONE', label: 'Done', color: 'border-accent-emerald' },
  ];

  const moveTask = (taskId: string, direction: 'next' | 'prev') => {
    const statusOrder: TaskItem['status'][] = ['BACKLOG', 'TODO', 'IN_PROGRESS', 'IN_REVIEW', 'DONE'];
    setTasks(prev => prev.map(t => {
      if (t.id !== taskId) return t;
      const curIdx = statusOrder.indexOf(t.status);
      const nextIdx = direction === 'next' ? Math.min(curIdx + 1, statusOrder.length - 1) : Math.max(curIdx - 1, 0);
      return { ...t, status: statusOrder[nextIdx] };
    }));
  };

  const handleCreateTask = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newTitle.trim()) return;

    const newTask: TaskItem = {
      id: `t-${Date.now()}`,
      taskNumber: tasks.length + 101,
      title: newTitle.trim(),
      status: 'TODO',
      priority: newPriority,
      type: newType,
      storyPoints: newPoints,
      assignee: 'John Doe',
      estimatedHours: newPoints * 1.5
    };

    setTasks(prev => [newTask, ...prev]);
    setNewTitle('');
    setIsModalOpen(false);
  };

  const getPriorityBadge = (p: TaskItem['priority']) => {
    switch (p) {
      case 'CRITICAL':
      case 'URGENT':
        return <span className="text-[10px] font-bold px-1.5 py-0.5 rounded bg-accent-rose/20 text-accent-rose border border-accent-rose/30">Urgent</span>;
      case 'HIGH':
        return <span className="text-[10px] font-bold px-1.5 py-0.5 rounded bg-accent-amber/20 text-accent-amber border border-accent-amber/30">High</span>;
      case 'MEDIUM':
        return <span className="text-[10px] font-semibold px-1.5 py-0.5 rounded bg-blue-500/20 text-blue-400 border border-blue-500/30">Medium</span>;
      default:
        return <span className="text-[10px] font-medium px-1.5 py-0.5 rounded bg-slate-800 text-slate-400">Low</span>;
    }
  };

  const getTypeIcon = (type: TaskItem['type']) => {
    switch (type) {
      case 'INCIDENT':
        return <Flame className="w-3.5 h-3.5 text-accent-rose" />;
      case 'BUG':
        return <Bug className="w-3.5 h-3.5 text-accent-amber" />;
      case 'STORY':
        return <Bookmark className="w-3.5 h-3.5 text-accent-purple" />;
      default:
        return <CheckCircle className="w-3.5 h-3.5 text-accent-cyan" />;
    }
  };

  return (
    <div className="space-y-6">
      {/* Board Controls */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-lg font-bold text-white tracking-tight">Active Sprint Board</h2>
          <p className="text-xs text-slate-400">Sprint 42: Production Reliability & AI Incident Copilot</p>
        </div>

        <div className="flex items-center gap-3">
          <button className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-surface-elevated border border-surface-border text-xs text-slate-300 hover:text-white transition-all">
            <Filter className="w-3.5 h-3.5" />
            <span>Filter Tasks</span>
          </button>

          <button
            onClick={() => setIsModalOpen(true)}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-accent-indigo hover:bg-accent-indigo/90 text-white text-xs font-semibold shadow-glow transition-all"
          >
            <Plus className="w-4 h-4" />
            <span>New Task</span>
          </button>
        </div>
      </div>

      {/* Kanban Columns Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-5 gap-4 items-start">
        {columns.map(col => {
          const colTasks = tasks.filter(t => t.status === col.key);
          const totalPoints = colTasks.reduce((acc, curr) => acc + curr.storyPoints, 0);

          return (
            <div
              key={col.key}
              className={`glass-panel rounded-xl border-t-2 ${col.color} border-surface-border p-3 flex flex-col min-h-[500px]`}
            >
              {/* Column Header */}
              <div className="flex items-center justify-between pb-3 mb-3 border-b border-surface-border">
                <div className="flex items-center gap-2">
                  <span className="text-xs font-bold text-white">{col.label}</span>
                  <span className="text-[11px] font-mono font-semibold px-1.5 py-0.2 rounded-full bg-slate-800 text-slate-300">
                    {colTasks.length}
                  </span>
                </div>
                <span className="text-[10px] text-slate-500 font-mono">{totalPoints} pts</span>
              </div>

              {/* Task Cards List */}
              <div className="space-y-3 flex-1">
                {colTasks.map(task => (
                  <div
                    key={task.id}
                    className="p-3.5 rounded-lg bg-surface-elevated/70 border border-surface-border hover:border-slate-600 transition-all shadow-sm group"
                  >
                    <div className="flex items-center justify-between mb-2">
                      <div className="flex items-center gap-1.5 text-xs text-slate-400">
                        {getTypeIcon(task.type)}
                        <span className="font-mono text-[11px]">#{task.taskNumber}</span>
                      </div>
                      {getPriorityBadge(task.priority)}
                    </div>

                    <h4 className="text-xs font-semibold text-slate-200 group-hover:text-white leading-snug mb-3">
                      {task.title}
                    </h4>

                    <div className="flex items-center justify-between pt-2 border-t border-surface-border/50 text-[11px] text-slate-400">
                      <div className="flex items-center gap-1.5">
                        <div className="w-5 h-5 rounded-full bg-slate-700 text-slate-200 text-[10px] flex items-center justify-center font-bold">
                          {task.assignee.substring(0, 2)}
                        </div>
                        <span className="truncate max-w-[80px]">{task.assignee}</span>
                      </div>

                      {/* Transition button */}
                      <div className="flex items-center gap-1">
                        <span className="font-mono font-semibold text-slate-300 mr-1">{task.storyPoints}pt</span>
                        {task.status !== 'DONE' && (
                          <button
                            onClick={() => moveTask(task.id, 'next')}
                            className="p-1 rounded bg-slate-800 hover:bg-accent-indigo hover:text-white text-slate-400 transition-all"
                            title="Move to next stage"
                          >
                            <ArrowRight className="w-3 h-3" />
                          </button>
                        )}
                      </div>
                    </div>
                  </div>
                ))}

                {colTasks.length === 0 && (
                  <div className="h-32 border border-dashed border-slate-800 rounded-lg flex items-center justify-center text-xs text-slate-600">
                    No tasks
                  </div>
                )}
              </div>
            </div>
          );
        })}
      </div>

      {/* Quick Create Task Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="glass-panel-elevated w-full max-w-lg p-6 rounded-2xl border border-surface-border shadow-glow">
            <h3 className="text-base font-bold text-white mb-4">Create New Operational Task</h3>
            <form onSubmit={handleCreateTask} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">Task Title</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Optimize Postgres query on TaskRepositoryImpl"
                  value={newTitle}
                  onChange={e => setNewTitle(e.target.value)}
                  className="w-full bg-surface border border-surface-border rounded-lg px-3 py-2 text-xs text-white focus:outline-none focus:border-accent-indigo"
                />
              </div>

              <div className="grid grid-cols-3 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">Type</label>
                  <select
                    value={newType}
                    onChange={e => setNewType(e.target.value as TaskItem['type'])}
                    className="w-full bg-surface border border-surface-border rounded-lg px-2.5 py-2 text-xs text-white"
                  >
                    <option value="TASK">Task</option>
                    <option value="BUG">Bug</option>
                    <option value="STORY">Story</option>
                    <option value="INCIDENT">Incident</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">Priority</label>
                  <select
                    value={newPriority}
                    onChange={e => setNewPriority(e.target.value as TaskItem['priority'])}
                    className="w-full bg-surface border border-surface-border rounded-lg px-2.5 py-2 text-xs text-white"
                  >
                    <option value="LOW">Low</option>
                    <option value="MEDIUM">Medium</option>
                    <option value="HIGH">High</option>
                    <option value="URGENT">Urgent</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">Story Points</label>
                  <input
                    type="number"
                    min="1"
                    max="21"
                    value={newPoints}
                    onChange={e => setNewPoints(parseInt(e.target.value) || 1)}
                    className="w-full bg-surface border border-surface-border rounded-lg px-3 py-2 text-xs text-white"
                  />
                </div>
              </div>

              <div className="flex items-center justify-end gap-3 pt-3 border-t border-surface-border">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 rounded-lg text-xs text-slate-400 hover:text-white"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-lg bg-accent-indigo hover:bg-accent-indigo/90 text-white text-xs font-semibold shadow-glow"
                >
                  Create Task
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
