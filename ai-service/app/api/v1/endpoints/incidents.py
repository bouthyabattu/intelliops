from fastapi import APIRouter
from pydantic import BaseModel, Field
from typing import Optional, Dict, Any
from app.agent.triage_agent import IncidentTriageAgent
from app.agent.tools import TOOL_REGISTRY, execute_tool

router = APIRouter()

class TriageRequest(BaseModel):
    title: str = Field(..., example="Surge in HTTP 504 Gateway Timeouts on /api/v1/tasks")
    severity: Optional[str] = "P1"
    serviceName: Optional[str] = "core-service"

class ToolExecutionRequest(BaseModel):
    toolName: str
    arguments: Dict[str, Any]

@router.post("/triage")
async def triage_incident(request: TriageRequest):
    """Executes multi-step ReAct agent triage over telemetry, logs, commits, and runbooks."""
    agent = IncidentTriageAgent(
        incident_title=request.title,
        severity=request.severity,
        service_name=request.serviceName
    )
    result = await agent.run()
    return result

@router.get("/tools")
async def list_tools():
    """Lists registered operational tools with execution classification and schema."""
    return {"tools": list(TOOL_REGISTRY.values())}

@router.post("/tools/execute")
async def execute_agent_tool(request: ToolExecutionRequest):
    """Direct execution of an operational tool."""
    res = await execute_tool(request.toolName, request.arguments)
    return res
