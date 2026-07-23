package com.upjs.studydocs.dao;

import com.upjs.studydocs.model.DocumentFile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DocumentFileDao {
    private final JdbcTemplate jdbcTemplate;

    public DocumentFileDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveDocumentFile(Long documentId, String contentType, Long fileSize, byte[] data) {
        String sql = """
                INSERT INTO document_files (document_id, content_type, file_size, data)
                VALUES (?, ?, ?, ?)
                """;
        jdbcTemplate.update(sql, documentId, contentType, fileSize, data);
    }

    public Optional<DocumentFile> findByDocumentId(Long documentId) {
        String sql = """
                SELECT document_id, content_type, file_size, data
                FROM document_files
                WHERE document_id = ?
                """;
        List<DocumentFile> files = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new DocumentFile(
                        rs.getLong("document_id"),
                        rs.getString("content_type"),
                        rs.getLong("file_size"),
                        rs.getBytes("data")
                ),
                documentId);
        return files.stream().findFirst();
    }

    public void deleteByDocumentId(Long documentId) {
        String sql = """
                DELETE FROM document_files
                WHERE document_id = ?
                """;
        jdbcTemplate.update(sql, documentId);
    }
}