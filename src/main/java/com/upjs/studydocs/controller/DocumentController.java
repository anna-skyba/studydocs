package com.upjs.studydocs.controller;

import com.upjs.studydocs.dto.DocumentResponse;
import com.upjs.studydocs.model.StudyDocument;
import com.upjs.studydocs.service.DocumentService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DocumentResponse uploadDocument(@RequestParam("file") MultipartFile file) {
        StudyDocument document = documentService.uploadDocument(file);
        return DocumentResponse.fromModel(document);
    }

    @GetMapping
    public List<DocumentResponse> getDocuments() {
        return documentService.findAllDocuments()
                .stream()
                .map(DocumentResponse::fromModel)
                .toList();
    }
}