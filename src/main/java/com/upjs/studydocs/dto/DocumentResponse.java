package com.upjs.studydocs.dto;

import com.upjs.studydocs.model.StudyDocument;

import java.time.LocalDateTime;

public record DocumentResponse(
        Long id,
        String filename,
        LocalDateTime uploadedAt,
        int chunksCount
) {
    public static DocumentResponse fromModel(StudyDocument document) {
        return new DocumentResponse(
                document.id(),
                document.filename(),
                document.uploadedAt(),
                document.chunks().size()
        );
    }
}