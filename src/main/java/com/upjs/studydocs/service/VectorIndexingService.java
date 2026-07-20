package com.upjs.studydocs.service;

import com.upjs.studydocs.model.DocumentChunk;
import com.upjs.studydocs.model.StudyDocument;
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
        List<Document> vectorDocuments = studyDocument.chunks()
                .stream()
                .map(chunk -> toVectorDocument(studyDocument, chunk))
                .toList();
        vectorStore.add(vectorDocuments);
    }

    private Document toVectorDocument(StudyDocument studyDocument, DocumentChunk chunk) {
        String stableIdSource = "document-" + studyDocument.id() + "-chunk-" + chunk.id();

        String uuid = UUID.nameUUIDFromBytes(
                stableIdSource.getBytes(StandardCharsets.UTF_8)
        ).toString();

        return Document.builder()
                .id(uuid)
                .text(chunk.content())
                .metadata(Map.of(
                        "documentId", studyDocument.id(),
                        "chunkId", chunk.id(),
                        "filename", studyDocument.filename(),
                        "chunkIndex", chunk.chunkIndex()
                ))
                .build();
    }
}