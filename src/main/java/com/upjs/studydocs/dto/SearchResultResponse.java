package com.upjs.studydocs.dto;

public record SearchResultResponse(
        String content,
        String filename,
        Long documentId,
        Long chunkId,
        Integer chunkIndex
) {}