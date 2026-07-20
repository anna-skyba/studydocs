CREATE TABLE IF NOT EXISTS study_documents (
    id BIGSERIAL PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    uploaded_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS document_chunks (
    id BIGSERIAL PRIMARY KEY,
    document_id BIGINT NOT NULL,
    chunk_index INT NOT NULL,
    content TEXT NOT NULL,
    CONSTRAINT fk_document
    FOREIGN KEY (document_id)
    REFERENCES study_documents(id)
    ON DELETE CASCADE
);