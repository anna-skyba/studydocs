package com.upjs.studydocs.model;

public record DocumentChunk(
        Long id,
        Long documentId,
        int chunkIndex,
        String content
) {}