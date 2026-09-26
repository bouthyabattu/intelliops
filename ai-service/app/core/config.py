from pydantic_settings import BaseSettings
from typing import Optional
import os

class Settings(BaseSettings):
    PROJECT_NAME: str = "IntelliOps AI Service"
    API_V1_STR: str = "/api/v1/ai"
    
    # Database
    POSTGRES_HOST: str = os.getenv("POSTGRES_HOST", "localhost")
    POSTGRES_PORT: int = int(os.getenv("POSTGRES_PORT", "5432"))
    POSTGRES_DB: str = os.getenv("POSTGRES_DB", "intelliops")
    POSTGRES_USER: str = os.getenv("POSTGRES_USER", "postgres")
    POSTGRES_PASSWORD: str = os.getenv("POSTGRES_PASSWORD", "postgres")
    
    @property
    def DATABASE_URL(self) -> str:
        return f"postgresql://{self.POSTGRES_USER}:{self.POSTGRES_PASSWORD}@{self.POSTGRES_HOST}:{self.POSTGRES_PORT}/{self.POSTGRES_DB}"
    
    # Redis
    REDIS_URL: str = os.getenv("REDIS_URL", "redis://localhost:6379/0")
    
    # LLM & Embeddings
    OPENAI_API_KEY: Optional[str] = os.getenv("OPENAI_API_KEY", "")
    DEFAULT_MODEL: str = os.getenv("OPENAI_MODEL", "gpt-4o")
    EMBEDDING_MODEL: str = os.getenv("EMBEDDING_MODEL", "text-embedding-3-small")
    EMBEDDING_DIMENSION: int = 1536
    
    # Mock LLM mode (allows offline and dev operation without real API keys)
    MOCK_LLM_MODE: bool = os.getenv("MOCK_LLM_MODE", "true").lower() in ("true", "1", "yes")

    class Config:
        env_file = ".env"
        case_sensitive = True

settings = Settings()
