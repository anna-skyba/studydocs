package com.upjs.studydocs.dto;

import com.upjs.studydocs.entity.StudyDocument;

import java.time.LocalDateTime;

public record DocumentResponse(
        Long id,
        String filename,
        LocalDateTime uploadedAt,
        int chunksCount
) {
    public static DocumentResponse fromEntity(StudyDocument document) {
        return new DocumentResponse(
                document.getId(),
                document.getFilename(),
                document.getUploadedAt(),
                document.getChunks().size()
        );
    }
}