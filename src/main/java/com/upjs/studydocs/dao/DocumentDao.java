package com.upjs.studydocs.dao;

import com.upjs.studydocs.model.DocumentChunk;
import com.upjs.studydocs.model.StudyDocument;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DocumentDao {

    private final JdbcTemplate jdbcTemplate;

    public DocumentDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public StudyDocument saveDocument(String filename) {
        String sql = """
                INSERT INTO study_documents (filename, uploaded_at)
                VALUES (?, now())
                RETURNING id, filename, uploaded_at
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> new StudyDocument(
                        rs.getLong("id"),
                        rs.getString("filename"),
                        rs.getTimestamp("uploaded_at").toLocalDateTime(),
                        List.of()
                ),
                filename
        );
    }

    public DocumentChunk saveChunk(Long documentId, int chunkIndex, String content) {
        String sql = """
                INSERT INTO document_chunks (document_id, chunk_index, content)
                VALUES (?, ?, ?)
                RETURNING id, document_id, chunk_index, content
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> new DocumentChunk(
                        rs.getLong("id"),
                        rs.getLong("document_id"),
                        rs.getInt("chunk_index"),
                        rs.getString("content")
                ),
                documentId,
                chunkIndex,
                content
        );
    }

    public List<StudyDocument> findAllDocuments() {
        String sql = """
                SELECT id, filename, uploaded_at
                FROM study_documents
                ORDER BY uploaded_at DESC
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    Long documentId = rs.getLong("id");

                    List<DocumentChunk> chunks = findChunksByDocumentId(documentId);

                    return new StudyDocument(
                            documentId,
                            rs.getString("filename"),
                            rs.getTimestamp("uploaded_at").toLocalDateTime(),
                            chunks
                    );
                }
        );
    }

    public List<DocumentChunk> findChunksByDocumentId(Long documentId) {
        String sql = """
                SELECT id, document_id, chunk_index, content
                FROM document_chunks
                WHERE document_id = ?
                ORDER BY chunk_index
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new DocumentChunk(
                        rs.getLong("id"),
                        rs.getLong("document_id"),
                        rs.getInt("chunk_index"),
                        rs.getString("content")
                ),
                documentId
        );
    }

    public boolean existsById(Long documentId) {
        String sql = """
            SELECT COUNT(*)
            FROM study_documents
            WHERE id = ?
            """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, documentId);
        return count != null && count > 0;
    }

    public void deleteChunksByDocumentId(Long documentId) {
        String sql = """
            DELETE FROM document_chunks
            WHERE document_id = ?
            """;
        jdbcTemplate.update(sql, documentId);
    }

    public void deleteDocumentById(Long documentId) {
        String sql = """
            DELETE FROM study_documents
            WHERE id = ?
            """;
        jdbcTemplate.update(sql, documentId);
    }

    public Optional<StudyDocument> findDocumentById(Long documentId) {
        String sql = """
            SELECT id, filename, uploaded_at
            FROM study_documents
            WHERE id = ?
            """;
        List<StudyDocument> documents = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    Long id = rs.getLong("id");
                    List<DocumentChunk> chunks = findChunksByDocumentId(id);
                    return new StudyDocument(id, rs.getString("filename"), rs.getTimestamp("uploaded_at").toLocalDateTime(), chunks);
                },
                documentId);
        return documents.stream().findFirst();
    }
}