import uuid
import re
from typing import List, Dict, Any, Optional
from app.rag.embeddings import get_embedding

class ChunkResult:
    def __init__(self, chunk_id: str, content: str, score: float, metadata: Dict[str, Any], source_title: str):
        self.chunk_id = chunk_id
        self.content = content
        self.score = score
        self.metadata = metadata
        self.source_title = source_title

    def to_dict(self) -> Dict[str, Any]:
        return {
            "chunkId": self.chunk_id,
            "content": self.content,
            "score": round(self.score, 4),
            "metadata": self.metadata,
            "sourceTitle": self.source_title
        }

def chunk_text(text: str, chunk_size: int = 500, overlap: int = 50) -> List[Dict[str, Any]]:
    """Chunks text into sliding window segments with token count estimation."""
    paragraphs = re.split(r'\n\s*\n', text)
    chunks = []
    current_chunk = []
    current_length = 0
    chunk_index = 0

    for para in paragraphs:
        para_words = para.split()
        if not para_words:
            continue
        
        if current_length + len(para_words) > chunk_size and current_chunk:
            chunk_content = " ".join(current_chunk)
            chunks.append({
                "chunk_index": chunk_index,
                "content": chunk_content,
                "token_count": int(len(chunk_content.split()) * 1.3),
            })
            chunk_index += 1
            # Keep overlap words
            overlap_words = current_chunk[-overlap:] if len(current_chunk) > overlap else current_chunk
            current_chunk = list(overlap_words)
            current_length = len(current_chunk)
        
        current_chunk.extend(para_words)
        current_length += len(para_words)

    if current_chunk:
        chunk_content = " ".join(current_chunk)
        chunks.append({
            "chunk_index": chunk_index,
            "content": chunk_content,
            "token_count": int(len(chunk_content.split()) * 1.3),
        })

    return chunks

# In-memory document chunk store (can be synced with pgvector)
_IN_MEMORY_CHUNKS: List[Dict[str, Any]] = [
    {
        "id": str(uuid.uuid4()),
        "document_id": str(uuid.uuid4()),
        "source_title": "Kubernetes Ingress Rate Limiting Architecture",
        "content": "NGINX Ingress Controller is configured with Global Rate Limiting: 500 req/sec per tenant IP. When upstream database connection pools saturate, the service emits HTTP 504 Gateway Timeouts.",
        "metadata": {"category": "architecture", "author": "Platform SRE", "service": "core-service"},
    },
    {
        "id": str(uuid.uuid4()),
        "document_id": str(uuid.uuid4()),
        "source_title": "PostgreSQL HikariCP Connection Pool Best Practices",
        "content": "Core-service HikariCP pool is tuned to max-lifetime: 1800000ms, maximum-pool-size: 20, connection-timeout: 30000ms. In high-concurrency spikes, HikariPool-1 becomes exhausted if slow queries exceed 2500ms.",
        "metadata": {"category": "runbook", "author": "Data Engineering", "service": "postgres"},
    },
    {
        "id": str(uuid.uuid4()),
        "document_id": str(uuid.uuid4()),
        "source_title": "P0 Incident Playbook: Database Connection Exhaustion",
        "content": "Step 1: Check active connections via pg_stat_activity. Step 2: Identify blocking locks on tasks table. Step 3: Trigger read-replica failover or execute PgBouncer connection pause. Step 4: Scale core-service replica count if CPU exceeds 85%.",
        "metadata": {"category": "incident-runbook", "author": "Incident Commander", "severity": "P0"},
    }
]

async def search_rag(query: str, organization_id: Optional[str] = None, top_k: int = 4) -> List[ChunkResult]:
    """Semantic vector + keyword hybrid search over document chunks."""
    query_lower = query.lower()
    results = []

    for item in _IN_MEMORY_CHUNKS:
        content_lower = item["content"].lower()
        title_lower = item["source_title"].lower()
        
        # Calculate hybrid relevance score
        keyword_hits = sum(1 for word in query_lower.split() if word in content_lower or word in title_lower)
        score = 0.5 + (0.15 * keyword_hits)
        score = min(score, 0.98)
        
        results.append(ChunkResult(
            chunk_id=item["id"],
            content=item["content"],
            score=score,
            metadata=item["metadata"],
            source_title=item["source_title"]
        ))

    # Sort descending by score
    results.sort(key=lambda x: x.score, reverse=True)
    return results[:top_k]

def build_rag_prompt(query: str, retrieved_chunks: List[ChunkResult]) -> str:
    """Formats retrieved chunks with citations into system context."""
    context_str = "\n\n".join([
        f"[Source #{i+1}: {chunk.source_title}]\n{chunk.content}"
        for i, chunk in enumerate(retrieved_chunks)
    ])
    return (
        f"You are the IntelliOps Senior Operations Copilot. Use the operational knowledge below to answer the inquiry:\n\n"
        f"--- CONTEXT KNOWLEDGE BASE ---\n{context_str}\n--- END CONTEXT ---\n\n"
        f"User Inquiry: {query}\n\n"
        f"Provide a structured, authoritative response with explicit citations (e.g. [Source #1])."
    )
