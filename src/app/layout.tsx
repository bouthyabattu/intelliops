import type { Metadata } from 'next';
import './globals.css';

export const metadata: Metadata = {
  title: 'IntelliOps — AI-Powered Enterprise Operations Platform',
  description: 'Unified operational intelligence connecting Jira, GitHub, CI/CD, documentation, Slack, monitoring, and autonomous AI reasoning agents.',
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en" className="dark">
      <body className="bg-background text-slate-100 min-h-screen antialiased bg-grid-pattern selection:bg-accent-indigo selection:text-white">
        {children}
      </body>
    </html>
  );
}
