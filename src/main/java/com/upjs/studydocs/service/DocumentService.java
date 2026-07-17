package com.upjs.studydocs.service;

import com.upjs.studydocs.entity.DocumentChunk;
import com.upjs.studydocs.entity.StudyDocument;
import com.upjs.studydocs.repository.StudyDocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class DocumentService {

    private final StudyDocumentRepository documentRepository;
    private final TextChunkingService textChunkingService;

    public DocumentService(
            StudyDocumentRepository documentRepository,
            TextChunkingService textChunkingService
    ) {
        this.documentRepository = documentRepository;
        this.textChunkingService = textChunkingService;
    }

    public StudyDocument uploadTextDocument(MultipartFile file) {
        try {
            String filename = file.getOriginalFilename();
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);

            List<String> chunks = textChunkingService.splitIntoChunks(content);

            StudyDocument document = new StudyDocument(filename);

            for (int i = 0; i < chunks.size(); i++) {
                DocumentChunk chunk = new DocumentChunk(i, chunks.get(i));
                document.addChunk(chunk);
            }

            return documentRepository.save(document);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read uploaded file", e);
        }
    }

    public List<StudyDocument> findAllDocuments() {
        return documentRepository.findAll();
    }
}