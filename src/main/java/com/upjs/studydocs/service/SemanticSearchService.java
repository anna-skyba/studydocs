package com.upjs.studydocs.service;

import com.upjs.studydocs.dto.SearchResultResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SemanticSearchService {
    private final VectorStore vectorStore;

    public SemanticSearchService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public List<SearchResultResponse> search(String question) {
        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(question)
                        .topK(5)
                        .build()
        );
        return results.stream()
                .map(this::toResponse)
                .toList();
    }

    private SearchResultResponse toResponse(Document document) {
        Map<String, Object> metadata = document.getMetadata();
        return new SearchResultResponse(
                document.getText(),
                metadata.get("filename").toString(),
                Long.valueOf(metadata.get("documentId").toString()),
                Long.valueOf(metadata.get("chunkId").toString()),
                Integer.valueOf(metadata.get("chunkIndex").toString())
        );
    }
}