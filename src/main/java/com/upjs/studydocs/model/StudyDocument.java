package com.upjs.studydocs.model;

import java.time.LocalDateTime;
import java.util.List;

public record StudyDocument(
        Long id,
        String filename,
        LocalDateTime uploadedAt,
        List<DocumentChunk> chunks
) {
    public StudyDocument withChunks(List<DocumentChunk> chunks) {
        return new StudyDocument(id, filename, uploadedAt, chunks);
    }
}