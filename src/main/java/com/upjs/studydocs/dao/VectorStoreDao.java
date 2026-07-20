package com.upjs.studydocs.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class VectorStoreDao {

    private final JdbcTemplate jdbcTemplate;

    public VectorStoreDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void deleteVectorsByDocumentId(Long documentId) {
        String sql = """
                DELETE FROM vector_store
                WHERE metadata->>'documentId' = ?
                """;
        jdbcTemplate.update(sql, documentId.toString());
    }
}