-- Initialize IntelliOps Database
-- Ensure pgvector and uuid extensions are active

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS "vector";

-- Verify vector extension is functional
SELECT '[1,2,3]'::vector;

-- Create schema namespace if needed
CREATE SCHEMA IF NOT EXISTS intelliops;
