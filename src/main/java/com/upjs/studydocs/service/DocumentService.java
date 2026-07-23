package com.upjs.studydocs.service;

import com.upjs.studydocs.dao.DocumentDao;
import com.upjs.studydocs.dao.DocumentFileDao;
import com.upjs.studydocs.dao.VectorStoreDao;
import com.upjs.studydocs.model.DocumentChunk;
import com.upjs.studydocs.model.DocumentFile;
import com.upjs.studydocs.model.StudyDocument;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

@Service
public class DocumentService {
    private final DocumentDao documentDao;
    private final TextChunkingService textChunkingService;
    private final VectorIndexingService vectorIndexingService;
    private final PdfTextExtractorService pdfTextExtractorService;
    private final VectorStoreDao vectorStoreDao;
    private final DocumentFileDao documentFileDao;

    public DocumentService(
            DocumentDao documentDao,
            TextChunkingService textChunkingService,
            VectorIndexingService vectorIndexingService,
            PdfTextExtractorService pdfTextExtractorService,
            VectorStoreDao vectorStoreDao,
            DocumentFileDao documentFileDao) {
        this.documentDao = documentDao;
        this.textChunkingService = textChunkingService;
        this.vectorIndexingService = vectorIndexingService;
        this.pdfTextExtractorService = pdfTextExtractorService;
        this.vectorStoreDao = vectorStoreDao;
        this.documentFileDao = documentFileDao;
    }

    public StudyDocument uploadDocument(MultipartFile file) {
        String filename = file.getOriginalFilename();
        String content = pdfTextExtractorService.extractText(file);
        List<String> chunkTexts = textChunkingService.splitIntoChunks(content);
        StudyDocument savedDocument = documentDao.saveDocument(filename);
        documentFileDao.saveDocumentFile(
                savedDocument.id(),
                getContentType(file),
                file.getSize(),
                readFileBytes(file)
        );
        List<DocumentChunk> savedChunks = new ArrayList<>();
        for (int i = 0; i < chunkTexts.size(); i++) {
            DocumentChunk savedChunk = documentDao.saveChunk(savedDocument.id(), i, chunkTexts.get(i));
            savedChunks.add(savedChunk);
        }
        StudyDocument documentWithChunks = savedDocument.withChunks(savedChunks);
        vectorIndexingService.indexDocument(documentWithChunks);
        return documentWithChunks;
    }

    public List<StudyDocument> findAllDocuments() {
        return documentDao.findAllDocuments();
    }

    public void deleteDocument(Long documentId) {
        if (!documentDao.existsById(documentId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found");
        }
        vectorStoreDao.deleteVectorsByDocumentId(documentId);
        documentFileDao.deleteByDocumentId(documentId);
        documentDao.deleteChunksByDocumentId(documentId);
        documentDao.deleteDocumentById(documentId);
    }

    public StudyDocument findDocumentById(Long documentId) {
        return documentDao.findDocumentById(documentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));
    }

    public DocumentFile findDocumentFileById(Long documentId) {
        if (!documentDao.existsById(documentId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found");
        }
        return documentFileDao.findByDocumentId(documentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document file not found"));
    }

    private byte[] readFileBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to read uploaded PDF file");
        }
    }

    private String getContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || contentType.isBlank()) {
            return "application/pdf";
        }
        return contentType;
    }
}