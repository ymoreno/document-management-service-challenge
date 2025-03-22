--The script to initialize the schema was sourced from the Spring Batch Core dependency: org.springframework.batch.core.

CREATE SCHEMA document_schema;
SET SCHEMA 'document_schema';

-- Table Definition: documents
CREATE TABLE "public"."documents" (
    "document_id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "created_at" TIMESTAMP NOT NULL DEFAULT NOW(),
    "document_name" VARCHAR(255) NOT NULL CHECK (LENGTH(document_name) > 0),
    "file_size" BIGINT NOT NULL CHECK (file_size > 0),
    "file_type" VARCHAR(50) NOT NULL CHECK (LENGTH(file_type) > 0),
    "minio_path" VARCHAR(500) NOT NULL CHECK (LENGTH(minio_path) > 0),
    "user_name" VARCHAR(100) NOT NULL CHECK (LENGTH(user_name) > 0)
);

-- Índices para mejorar rendimiento en búsquedas comunes
CREATE INDEX idx_documents_user_name ON "public"."documents" ("user_name");
CREATE INDEX idx_documents_created_at ON "public"."documents" ("created_at");

-- Table Definition: document_tags
CREATE TABLE "public"."document_tags" (
    "document_id" UUID NOT NULL,
    "tag" VARCHAR(100) NOT NULL CHECK (LENGTH(tag) > 0),
    PRIMARY KEY ("document_id", "tag"),
    CONSTRAINT "fk_document_tags_documents" FOREIGN KEY ("document_id")
        REFERENCES "public"."documents"("document_id") ON DELETE CASCADE
);

-- Índice para búsqueda rápida por tag
CREATE INDEX idx_document_tags_tag ON "public"."document_tags" ("tag");
