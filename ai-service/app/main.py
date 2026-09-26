from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.core.config import settings
from app.api.v1.endpoints import chat, rag, incidents, evaluation

app = FastAPI(
    title=settings.PROJECT_NAME,
    description="IntelliOps Autonomous Reasoning, Incident Triage Copilot, and Vector RAG Service",
    version="1.0.0",
    docs_url="/docs",
    redoc_url="/redoc"
)

# CORS Middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Include API v1 Routers
app.include_router(chat.router, prefix="/api/v1/ai", tags=["AI Reasoning & Chat"])
app.include_router(rag.router, prefix="/api/v1/ai/rag", tags=["Vector RAG Engine"])
app.include_router(incidents.router, prefix="/api/v1/ai/incidents", tags=["Incident Triage Copilot"])
app.include_router(evaluation.router, prefix="/api/v1/ai/eval", tags=["AI Evaluation & Benchmarks"])

@app.get("/health", tags=["Health"])
async def health_check():
    return {
        "status": "UP",
        "service": "intelliops-ai-service",
        "mockLlmMode": settings.MOCK_LLM_MODE,
        "defaultModel": settings.DEFAULT_MODEL,
        "embeddingModel": settings.EMBEDDING_MODEL
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("app.main:app", host="0.0.0.0", port=8000, reload=True)
