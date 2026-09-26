import math
import hashlib
from typing import List
from app.core.config import settings

def _generate_mock_embedding(text: str, dim: int = 1536) -> List[float]:
    """Generates a deterministic unit-normalized pseudo-embedding for testing/mock mode."""
    seed_hash = hashlib.sha256(text.encode("utf-8")).digest()
    vec = []
    for i in range(dim):
        byte_val = seed_hash[i % len(seed_hash)]
        val = ((byte_val / 255.0) * 2.0 - 1.0) * math.cos(i * 0.1)
        vec.append(val)
    
    # L2 normalize
    norm = math.sqrt(sum(x * x for x in vec)) or 1.0
    return [round(x / norm, 6) for x in vec]

async def get_embedding(text: str) -> List[float]:
    """Retrieves 1536-dimensional embedding vector for query or text chunk."""
    if settings.OPENAI_API_KEY and not settings.MOCK_LLM_MODE:
        try:
            from openai import AsyncOpenAI
            client = AsyncOpenAI(api_key=settings.OPENAI_API_KEY)
            res = await client.embeddings.create(
                model=settings.EMBEDDING_MODEL,
                input=text
            )
            return res.data[0].embedding
        except Exception:
            # Fallback to mock embedding on API failure
            return _generate_mock_embedding(text, settings.EMBEDDING_DIMENSION)
    
    return _generate_mock_embedding(text, settings.EMBEDDING_DIMENSION)

async def get_embeddings_batch(texts: List[str]) -> List[List[float]]:
    """Batch embedding calculation for chunks."""
    return [await get_embedding(t) for t in texts]
