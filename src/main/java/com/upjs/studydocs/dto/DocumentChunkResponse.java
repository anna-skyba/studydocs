package com.upjs.studydocs.dto;

import com.upjs.studydocs.model.DocumentChunk;

public record DocumentChunkResponse(
        Long id,
        int chunkIndex,
        String content
) {
    public static DocumentChunkResponse fromModel(DocumentChunk chunk) {
        return new DocumentChunkResponse(
                chunk.id(),
                chunk.chunkIndex(),
                chunk.content()
        );
    }
}