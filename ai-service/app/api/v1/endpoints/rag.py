from fastapi import APIRouter
from pydantic import BaseModel, Field
from typing import List, Optional, Dict, Any
from app.rag.pipeline import search_rag, chunk_text, _IN_MEMORY_CHUNKS
import uuid

router = APIRouter()

class RAGQueryRequest(BaseModel):
    query: str = Field(..., description="Semantic search query")
    organizationId: Optional[str] = None
    topK: Optional[int] = 5

class RAGIndexRequest(BaseModel):
    documentId: str
    organizationId: str
    title: str
    content: str
    category: Optional[str] = "documentation"

@router.post("/query")
async def query_rag(request: RAGQueryRequest):
    results = await search_rag(request.query, request.organizationId, request.topK)
    return {
        "query": request.query,
        "totalMatches": len(results),
        "results": [r.to_dict() for r in results]
    }

@router.post("/index")
async def index_document(request: RAGIndexRequest):
    chunks = chunk_text(request.content)
    added_ids = []
    
    for c in chunks:
        chunk_id = str(uuid.uuid4())
        _IN_MEMORY_CHUNKS.append({
            "id": chunk_id,
            "document_id": request.documentId,
            "source_title": request.title,
            "content": c["content"],
            "metadata": {
                "category": request.category,
                "organization_id": request.organizationId,
                "token_count": c["token_count"]
            }
        })
        added_ids.append(chunk_id)

    return {
        "status": "INDEXED",
        "documentId": request.documentId,
        "title": request.title,
        "chunksCreated": len(chunks),
        "chunkIds": added_ids
    }
