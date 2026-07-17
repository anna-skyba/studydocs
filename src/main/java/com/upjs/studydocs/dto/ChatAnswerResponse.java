package com.upjs.studydocs.dto;

import java.util.List;

public record ChatAnswerResponse(
        String answer,
        List<SearchResultResponse> sources
) {
}