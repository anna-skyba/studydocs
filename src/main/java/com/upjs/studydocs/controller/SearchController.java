package com.upjs.studydocs.controller;

import com.upjs.studydocs.dto.SearchRequest;
import com.upjs.studydocs.dto.SearchResultResponse;
import com.upjs.studydocs.service.SemanticSearchService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    private final SemanticSearchService semanticSearchService;

    public SearchController(SemanticSearchService semanticSearchService) {
        this.semanticSearchService = semanticSearchService;
    }

    @PostMapping
    public List<SearchResultResponse> search(@Valid @RequestBody SearchRequest request) {
        return semanticSearchService.search(request.question());
    }
}