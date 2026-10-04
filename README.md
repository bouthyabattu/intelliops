# IntelliOps — AI-Powered Enterprise Engineering & Operations Platform

[![License: Apache-2.0](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net/)
[![Spring Boot 3](https://img.shields.io/badge/Spring_Boot-3.2+-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Python 3.11+](https://img.shields.io/badge/Python-3.11+-blue.svg)](https://www.python.org/)
[![FastAPI](https://img.shields.io/badge/FastAPI-0.110+-teal.svg)](https://fastapi.tiangolo.com/)
[![Next.js 14](https://img.shields.io/badge/Next.js-14-black.svg)](https://nextjs.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16_+_pgvector-blue.svg)](https://www.postgresql.org/)

IntelliOps is a centralized operational intelligence and orchestration platform for modern software organizations. It connects Jira, GitHub, CI/CD, documentation, Slack, monitoring, and databases into a unified AI-driven reasoning and automation platform.

---

## 🏛️ System Architecture

```text
                                   ┌─────────────────────┐
                                   │       USERS         │
                                   │ Developers / Admins │
                                   └──────────┬──────────┘
                                              │
                                              ▼
                           ┌─────────────────────────────────────┐
                           │      NEXT.JS 14 WEB APP             │
                           │  TypeScript + Tailwind + Radix UI   │
                           └──────────────┬──────────────────────┘
                                          │
                                   HTTPS / WebSocket
                                          │
                                          ▼
                           ┌─────────────────────────────────────┐
                           │            API GATEWAY              │
                           │  Keycloak OAuth2 / OIDC + Reverse   │
                           └──────────────┬──────────────────────┘
                                          │
                ┌─────────────────────────┼────────────────────────┐
                ▼                         ▼                        ▼
       ┌─────────────────┐      ┌──────────────────┐      ┌────────────────┐
       │  SPRING BOOT    │      │     FASTAPI      │      │   REAL-TIME    │
       │  CORE SERVICES  │      │    AI SERVICE    │      │    SERVICE     │
       │                 │      │                  │      │                │
       │ • Organizations │      │ • RAG Pipeline   │      │ • WebSockets   │
       │ • Projects      │      │ • Incident Agent │      │ • Live Updates │
       │ • Tasks & Board │      │ • Tool Calling   │      │ • Presence     │
       │ • Auth & RBAC   │      │ • Vector Search  │      │ • AI Stream    │
       │ • Documents     │      │ • AI Evaluation  │      │ • Notify Bus   │
       └────────┬────────┘      └────────┬─────────┘      └───────┬────────┘
                │                        │                        │
                └────────────────────────┼────────────────────────┘
                                         │
                  ┌──────────────────────┼─────────────────────┐
                  ▼                      ▼                     ▼
           ┌─────────────┐        ┌────────────┐       ┌──────────────┐
           │ PostgreSQL  │        │   Redis    │       │    Kafka     │
           │ 16+pgvector │        │ Cache/Sess │       │ Event Stream │
           └─────────────┘        └────────────┘       └──────────────┘
```

---

## 🚀 Quick Start

### 1. Prerequisites
- **Node.js**: v18+ (tested on v25)
- **Java**: JDK 21+
- **Maven**: 3.9+
- **Python**: 3.11+
- *(Optional)* **Docker & Docker Compose** for production container cluster

### 2. Environment Setup
```bash
cp .env.example .env
# Set your OPENAI_API_KEY if testing real LLM reasoning (mock fallback provided)
```

### 3. Running Services

#### Running the Full Stack with Docker:
```bash
docker compose up -d
```

#### Running Locally (No Docker Required):
1. **Core Service (Spring Boot 3.2 / Java 21):**
   ```bash
   cd apps/core-service
   mvn spring-boot:run
   ```
   *Runs at `http://localhost:8080` (Swagger UI at `/swagger-ui/index.html`)*

2. **AI Service (FastAPI / Python):**
   ```bash
   cd apps/ai-service
   pip install -r requirements.txt
   uvicorn app.main:app --port 8000 --reload
   ```
   *Runs at `http://localhost:8000` (Docs at `/docs`)*

3. **Realtime WebSocket Service (Node.js):**
   ```bash
   cd apps/realtime-service
   npm install
   npm run dev
   ```
   *Runs at `http://localhost:4000`*

4. **Web Frontend (Next.js 14):**
   ```bash
   cd apps/web
   npm install
   npm run dev
   ```
   *Runs at `http://localhost:3000`*

---

## 📦 Project Structure

```text
intelliops/
├── apps/
│   ├── web/                  # Next.js 14 App Router, TypeScript, Tailwind, Lucide, Radix
│   ├── core-service/         # Spring Boot 3.2, Java 21, Spring Modulith, JPA, Security
│   ├── ai-service/           # FastAPI, Python 3.11+, LangGraph RAG, OpenAI, pgvector
│   ├── realtime-service/     # Node.js WebSocket & Redis PubSub server
│   └── worker-service/       # Celery / Background async job processor
├── packages/
│   ├── types/                # Shared TypeScript contracts and API schemas
│   └── config/               # Shared ESLint, TS, Prettier configs
├── infrastructure/
│   ├── docker/               # Multi-stage Dockerfiles and DB scripts
│   ├── kubernetes/           # K8s manifests (Deployments, Services, Ingress, HPA)
│   ├── terraform/            # Cloud infrastructure modules
│   └── monitoring/           # Prometheus, Grafana, OpenTelemetry configs
└── docs/                     # Architecture Decision Records (ADRs) and specs
```

---

## 🛡️ Security & Enterprise Multi-Tenancy
- **Triple Auth Layer**: Keycloak OIDC + Spring Security JWT + NextAuth.js
- **Tenant Isolation**: Every entity is scoped with `organization_id` with enforced SQL filtering
- **Role-Based Access Control**: `ADMIN`, `MANAGER`, `ENGINEER`, `VIEWER`
- **Agent Guardrails**: Read / Write / Destructive tool classifications with human confirmation gates.
