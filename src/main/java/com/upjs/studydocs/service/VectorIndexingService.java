package com.upjs.studydocs.service;

import com.upjs.studydocs.entity.DocumentChunk;
import com.upjs.studydocs.entity.StudyDocument;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class VectorIndexingService {
    private final VectorStore vectorStore;

    public VectorIndexingService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void indexDocument(StudyDocument studyDocument) {
        List<Document> vectorDocuments = studyDocument.getChunks()
                .stream()
                .map(chunk -> toVectorDocument(studyDocument, chunk))
                .toList();
        vectorStore.add(vectorDocuments);
    }

    private Document toVectorDocument(StudyDocument studyDocument, DocumentChunk chunk) {
        String stableIdSource = "document-" + studyDocument.getId() + "-chunk-" + chunk.getId();
        String uuid = UUID.nameUUIDFromBytes(stableIdSource.getBytes(StandardCharsets.UTF_8)).toString();
        return Document.builder()
                .id(uuid)
                .text(chunk.getContent())
                .metadata(Map.of(
                        "documentId", studyDocument.getId(),
                        "chunkId", chunk.getId(),
                        "filename", studyDocument.getFilename(),
                        "chunkIndex", chunk.getChunkIndex()
                ))
                .build();
    }
}