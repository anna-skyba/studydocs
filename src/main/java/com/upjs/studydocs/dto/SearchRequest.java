package com.upjs.studydocs.dto;

import jakarta.validation.constraints.NotBlank;

public record SearchRequest(
        @NotBlank
        String question
) {}