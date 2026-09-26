-- V6: Document Management & Vector Storage Schema
-- Folders, documents, versions, and chunk embeddings for RAG

CREATE TABLE IF NOT EXISTS folders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    parent_folder_id UUID REFERENCES folders(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_folders_org ON folders(organization_id);

CREATE TABLE IF NOT EXISTS documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    folder_id UUID REFERENCES folders(id) ON DELETE SET NULL,
    project_id UUID REFERENCES projects(id) ON DELETE SET NULL,
    title VARCHAR(300) NOT NULL,
    file_type VARCHAR(50) NOT NULL, -- PDF, DOCX, TXT, MD, CSV, JSON
    file_size_bytes BIGINT NOT NULL,
    storage_path TEXT NOT NULL,
    uploaded_by UUID REFERENCES users(id) ON DELETE SET NULL,
    is_indexed_for_rag BOOLEAN DEFAULT FALSE NOT NULL,
    indexing_status VARCHAR(50) DEFAULT 'PENDING' NOT NULL, -- PENDING, PROCESSING, COMPLETED, FAILED
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_documents_org ON documents(organization_id);
CREATE INDEX IF NOT EXISTS idx_documents_indexing ON documents(indexing_status);

CREATE TABLE IF NOT EXISTS document_versions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_id UUID NOT NULL REFERENCES documents(id) ON DELETE CASCADE,
    version_number INTEGER NOT NULL,
    storage_path TEXT NOT NULL,
    change_summary TEXT,
    created_by UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uq_doc_versions UNIQUE (document_id, version_number)
);

-- RAG Document Chunks with Vector Embeddings
CREATE TABLE IF NOT EXISTS document_chunks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    document_id UUID NOT NULL REFERENCES documents(id) ON DELETE CASCADE,
    chunk_index INTEGER NOT NULL,
    content TEXT NOT NULL,
    token_count INTEGER NOT NULL,
    page_number INTEGER,
    source_filename VARCHAR(255) NOT NULL,
    metadata JSONB DEFAULT '{}'::jsonb NOT NULL,
    embedding vector(1536), -- OpenAI text-embedding-3-small or ada-002 dimensions
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uq_doc_chunk_idx UNIQUE (document_id, chunk_index)
);

CREATE INDEX IF NOT EXISTS idx_doc_chunks_org ON document_chunks(organization_id);
CREATE INDEX IF NOT EXISTS idx_doc_chunks_doc ON document_chunks(document_id);

-- HNSW Vector Index for fast approximate nearest neighbor cosine similarity search
CREATE INDEX IF NOT EXISTS idx_doc_chunks_vector 
    ON document_chunks 
    USING hnsw (embedding vector_cosine_ops)
    WITH (m = 16, ef_construction = 64);

-- Full text search index for Hybrid Search (BM25 / tsvector + pgvector)
CREATE INDEX IF NOT EXISTS idx_doc_chunks_fts 
    ON document_chunks 
    USING gin (to_tsvector('english', content));
