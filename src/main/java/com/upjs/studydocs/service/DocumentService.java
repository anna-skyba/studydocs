package com.upjs.studydocs.service;

import com.upjs.studydocs.dao.DocumentDao;
import com.upjs.studydocs.model.DocumentChunk;
import com.upjs.studydocs.model.StudyDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentService {
    private final DocumentDao documentDao;
    private final TextChunkingService textChunkingService;
    private final VectorIndexingService vectorIndexingService;

    public DocumentService(DocumentDao documentDao, TextChunkingService textChunkingService, VectorIndexingService vectorIndexingService) {
        this.documentDao = documentDao;
        this.textChunkingService = textChunkingService;
        this.vectorIndexingService = vectorIndexingService;
    }

    public StudyDocument uploadTextDocument(MultipartFile file) {
        try {
            String filename = file.getOriginalFilename();
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            List<String> chunkTexts = textChunkingService.splitIntoChunks(content);
            StudyDocument savedDocument = documentDao.saveDocument(filename);
            List<DocumentChunk> savedChunks = new ArrayList<>();
            for (int i = 0; i < chunkTexts.size(); i++) {
                DocumentChunk savedChunk = documentDao.saveChunk(savedDocument.id(), i, chunkTexts.get(i));
                savedChunks.add(savedChunk);
            }
            StudyDocument documentWithChunks = savedDocument.withChunks(savedChunks);
            vectorIndexingService.indexDocument(documentWithChunks);
            return documentWithChunks;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read uploaded file", e);
        }
    }

    public List<StudyDocument> findAllDocuments() {
        return documentDao.findAllDocuments();
    }
}