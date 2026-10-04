'use client';

import React, { useState } from 'react';
import { 
  BookOpen, 
  Search, 
  Upload, 
  FileText, 
  CheckCircle2, 
  Clock, 
  Sparkles, 
  Layers, 
  Database,
  ExternalLink
} from 'lucide-react';

interface DocItem {
  id: string;
  title: string;
  type: string;
  size: string;
  indexingStatus: 'COMPLETED' | 'PROCESSING' | 'PENDING';
  chunkCount: number;
  uploadedAt: string;
}

const INITIAL_DOCS: DocItem[] = [
  {
    id: 'd-1',
    title: 'Kubernetes Ingress Rate Limiting Architecture',
    type: 'MARKDOWN',
    size: '14.2 KB',
    indexingStatus: 'COMPLETED',
    chunkCount: 8,
    uploadedAt: '2 hours ago'
  },
  {
    id: 'd-2',
    title: 'PostgreSQL HikariCP Connection Pool Best Practices',
    type: 'PDF',
    size: '184 KB',
    indexingStatus: 'COMPLETED',
    chunkCount: 24,
    uploadedAt: '1 day ago'
  },
  {
    id: 'd-3',
    title: 'P0 Incident Playbook: Database Connection Exhaustion',
    type: 'DOCX',
    size: '92 KB',
    indexingStatus: 'COMPLETED',
    chunkCount: 16,
    uploadedAt: '3 days ago'
  },
  {
    id: 'd-4',
    title: 'Multi-Tenant RBAC Security Architecture Specification',
    type: 'MARKDOWN',
    size: '28.5 KB',
    indexingStatus: 'PROCESSING',
    chunkCount: 12,
    uploadedAt: '10 mins ago'
  }
];

export function KnowledgeBaseView() {
  const [docs, setDocs] = useState<DocItem[]>(INITIAL_DOCS);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedDoc, setSelectedDoc] = useState<DocItem | null>(null);
  const [searchResult, setSearchResult] = useState<string | null>(null);

  const handleSimulateSearch = () => {
    if (!searchQuery.trim()) return;
    setSearchResult(
      `Retrieved 3 matching chunks for "${searchQuery}" with highest cosine similarity 0.942 against 'PostgreSQL HikariCP Connection Pool Best Practices'. Chunk #4 specifies maximum-pool-size: 20 and connection-timeout: 30000ms.`
    );
  };

  return (
    <div className="space-y-6">
      {/* Knowledge Base Header */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-lg font-bold text-white tracking-tight">RAG Knowledge Base & Vector Index</h2>
          <p className="text-xs text-slate-400">
            60 indexed chunks in PostgreSQL pgvector (1536 dimensions) for AI Copilot grounding
          </p>
        </div>

        <button className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-accent-indigo hover:bg-accent-indigo/90 text-white text-xs font-semibold shadow-glow transition-all">
          <Upload className="w-4 h-4" />
          <span>Upload Document</span>
        </button>
      </div>

      {/* Semantic Search Sandbox */}
      <div className="glass-panel p-4 rounded-xl border border-surface-border">
        <h3 className="text-xs font-bold text-white flex items-center gap-1.5 mb-2">
          <Sparkles className="w-3.5 h-3.5 text-accent-cyan" />
          <span>Vector Hybrid Retrieval Sandbox</span>
        </h3>
        <div className="flex gap-2">
          <input
            type="text"
            placeholder="Type query to test semantic chunk retrieval (e.g. 'HikariCP timeout', 'Ingress rate limiting')..."
            value={searchQuery}
            onChange={e => setSearchQuery(e.target.value)}
            onKeyDown={e => e.key === 'Enter' && handleSimulateSearch()}
            className="flex-1 bg-surface border border-surface-border rounded-lg px-3 py-2 text-xs text-white focus:outline-none focus:border-accent-indigo"
          />
          <button
            onClick={handleSimulateSearch}
            className="px-4 py-2 rounded-lg bg-surface-elevated hover:bg-slate-700 border border-surface-border text-xs text-white font-medium"
          >
            Run Query
          </button>
        </div>

        {searchResult && (
          <div className="mt-3 p-3 rounded-lg bg-surface border border-accent-cyan/30 text-xs text-slate-300 font-mono">
            {searchResult}
          </div>
        )}
      </div>

      {/* Document Library Table */}
      <div className="glass-panel rounded-xl border border-surface-border overflow-hidden">
        <div className="p-4 border-b border-surface-border flex items-center justify-between">
          <h3 className="text-xs font-bold text-white">Indexed Operational Documents</h3>
          <span className="text-[11px] text-slate-400 font-mono">4 Documents</span>
        </div>

        <table className="w-full text-left text-xs">
          <thead className="bg-surface-elevated/40 border-b border-surface-border text-slate-400 text-[11px] font-semibold">
            <tr>
              <th className="py-2.5 px-4">Document Title</th>
              <th className="py-2.5 px-4">Format</th>
              <th className="py-2.5 px-4">Size</th>
              <th className="py-2.5 px-4">RAG Status</th>
              <th className="py-2.5 px-4">Chunks</th>
              <th className="py-2.5 px-4">Uploaded</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-surface-border/40">
            {docs.map(doc => (
              <tr key={doc.id} className="hover:bg-surface-elevated/40 transition-colors">
                <td className="py-3 px-4 flex items-center gap-2 font-medium text-slate-200">
                  <FileText className="w-4 h-4 text-accent-indigo" />
                  <span>{doc.title}</span>
                </td>
                <td className="py-3 px-4 font-mono text-[11px] text-slate-400">{doc.type}</td>
                <td className="py-3 px-4 font-mono text-[11px] text-slate-400">{doc.size}</td>
                <td className="py-3 px-4">
                  {doc.indexingStatus === 'COMPLETED' ? (
                    <span className="inline-flex items-center gap-1 text-[10px] font-bold px-2 py-0.5 rounded-full bg-accent-emerald/20 text-accent-emerald border border-accent-emerald/30">
                      <CheckCircle2 className="w-3 h-3" /> Indexed
                    </span>
                  ) : (
                    <span className="inline-flex items-center gap-1 text-[10px] font-bold px-2 py-0.5 rounded-full bg-accent-cyan/20 text-accent-cyan animate-pulse">
                      <Clock className="w-3 h-3" /> Processing
                    </span>
                  )}
                </td>
                <td className="py-3 px-4 font-mono text-accent-cyan">{doc.chunkCount} vectors</td>
                <td className="py-3 px-4 text-slate-400">{doc.uploadedAt}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
