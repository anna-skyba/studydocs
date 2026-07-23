package com.upjs.studydocs.dto;

import com.upjs.studydocs.model.StudyDocument;

import java.time.LocalDateTime;
import java.util.List;

public record DocumentDetailsResponse(
        Long id,
        String filename,
        LocalDateTime uploadedAt,
        List<DocumentChunkResponse> chunks
) {
    public static DocumentDetailsResponse fromModel(StudyDocument document) {
        return new DocumentDetailsResponse(
                document.id(),
                document.filename(),
                document.uploadedAt(),
                document.chunks()
                        .stream()
                        .map(DocumentChunkResponse::fromModel)
                        .toList()
        );
    }
}