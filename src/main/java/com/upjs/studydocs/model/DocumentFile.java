package com.upjs.studydocs.model;

public record DocumentFile(
        Long documentId,
        String contentType,
        Long fileSize,
        byte[] data
) {}