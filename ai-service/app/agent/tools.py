from typing import Dict, Any, List
import datetime

class AgentTool:
    def __init__(self, name: str, description: str, classification: str, requires_approval: bool):
        self.name = name
        self.description = description
        self.classification = classification  # READ, WRITE, DESTRUCTIVE
        self.requires_approval = requires_approval

# Registry of SRE and DevOps operational tools
TOOL_REGISTRY: Dict[str, Dict[str, Any]] = {
    "query_logs": {
        "name": "query_logs",
        "description": "Searches Kubernetes pod logs across cluster namespaces (e.g. core-service, db, nginx-ingress)",
        "classification": "READ",
        "requires_approval": False,
        "parameters": {
            "service_name": {"type": "string", "description": "Target service: core-service, ai-service, postgres"},
            "time_window_minutes": {"type": "integer", "description": "Minutes back to query", "default": 15},
            "log_level": {"type": "string", "enum": ["ERROR", "WARN", "INFO"], "default": "ERROR"}
        }
    },
    "get_service_metrics": {
        "name": "get_service_metrics",
        "description": "Retrieves real-time Prometheus telemetry: P99 latency, request throughput, error rate, CPU/Memory",
        "classification": "READ",
        "requires_approval": False,
        "parameters": {
            "service_name": {"type": "string", "description": "Target service"}
        }
    },
    "check_ci_pipeline": {
        "name": "check_ci_pipeline",
        "description": "Inspects recent GitHub Actions commits, merges, and deployment status",
        "classification": "READ",
        "requires_approval": False,
        "parameters": {
            "repo_name": {"type": "string", "default": "intelliops-platform"}
        }
    },
    "fetch_runbook": {
        "name": "fetch_runbook",
        "description": "Fetches standard operating procedures (SOP) and incident runbooks from the knowledge base",
        "classification": "READ",
        "requires_approval": False,
        "parameters": {
            "incident_category": {"type": "string", "description": "e.g., db_pool_exhaustion, latency_spike"}
        }
    },
    "trigger_circuit_breaker": {
        "name": "trigger_circuit_breaker",
        "description": "Opens circuit breaker for downstream degraded services to protect upstream availability",
        "classification": "WRITE",
        "requires_approval": True,
        "parameters": {
            "target_service": {"type": "string", "description": "Service to isolate"}
        }
    },
    "execute_rollback": {
        "name": "execute_rollback",
        "description": "Initiates Kubernetes canary rollback to the previous stable container image tag",
        "classification": "DESTRUCTIVE",
        "requires_approval": True,
        "parameters": {
            "deployment_name": {"type": "string", "description": "Kubernetes deployment"},
            "target_image_tag": {"type": "string", "description": "Previous stable tag"}
        }
    }
}

async def execute_tool(tool_name: str, arguments: Dict[str, Any]) -> Dict[str, Any]:
    """Simulates real execution of the operational tooling with authentic diagnostic data."""
    now = datetime.datetime.now(datetime.timezone.utc).isoformat()
    
    if tool_name == "query_logs":
        svc = arguments.get("service_name", "core-service")
        return {
            "status": "success",
            "timestamp": now,
            "service": svc,
            "matched_entries": 42,
            "sample_logs": [
                f"[ERROR] HikariPool-1 - Connection is not available, request timed out after 30005ms (active=20, idle=0, waiting=14)",
                f"[WARN] TenantContextFilter - Slow tenant request detected (duration=4120ms, path=/api/v1/tasks)",
                f"[ERROR] TaskRepositoryImpl - PSQLException: FATAL: remaining connection slots are reserved for non-replication superuser connections"
            ]
        }
    
    elif tool_name == "get_service_metrics":
        return {
            "status": "success",
            "timestamp": now,
            "metrics": {
                "p99_latency_ms": 3840.5,
                "p95_latency_ms": 1920.0,
                "p50_latency_ms": 145.0,
                "error_rate_percent": 8.7,
                "cpu_utilization_pct": 74.2,
                "memory_utilization_pct": 82.1,
                "active_connections": 20,
                "max_connections": 20
            }
        }
    
    elif tool_name == "check_ci_pipeline":
        return {
            "status": "success",
            "latest_deployment": {
                "sha": "7f8b91a",
                "message": "feat(tasks): add unindexed query on task search with tenant join",
                "author": "dev-eng-2",
                "deployed_at": "18 minutes ago",
                "pipeline_status": "PASSED"
            }
        }
    
    elif tool_name == "fetch_runbook":
        return {
            "status": "success",
            "runbook_id": "RB-POSTGRES-CONN-01",
            "title": "Mitigating Connection Pool Saturation in Core Service",
            "recommended_actions": [
                "Verify database connection count via pg_stat_activity",
                "Check recent deployments for unindexed queries holding long transactions",
                "Scale pod replicas if pool max size per pod is saturated",
                "Execute fast rollback if commit sha was deployed in the last 30 minutes"
            ]
        }
        
    elif tool_name == "trigger_circuit_breaker":
        return {
            "status": "executed",
            "action": "Circuit breaker activated",
            "target": arguments.get("target_service"),
            "effective_until": "Manual reset"
        }
        
    elif tool_name == "execute_rollback":
        return {
            "status": "pending_approval",
            "message": "DESTRUCTIVE ACTION: Rollback requested. Requires human authorization gate.",
            "target_deployment": arguments.get("deployment_name"),
            "target_tag": arguments.get("target_image_tag")
        }
        
    return {"status": "error", "message": f"Unknown tool: {tool_name}"}
