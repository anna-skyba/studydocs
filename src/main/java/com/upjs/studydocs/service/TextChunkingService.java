package com.upjs.studydocs.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TextChunkingService {
    private static final int CHUNK_SIZE = 1000;
    private static final int OVERLAP = 150;

    public List<String> splitIntoChunks(String text) {
        String cleanedText = text.replaceAll("\\s+", " ").trim();
        List<String> chunks = new ArrayList<>();
        int start = 0;
        while (start < cleanedText.length()) {
            int end = Math.min(start + CHUNK_SIZE, cleanedText.length());
            String chunk = cleanedText.substring(start, end).trim();
            if (!chunk.isBlank()) {
                chunks.add(chunk);
            }
            if (end == cleanedText.length()) {
                break;
            }
            start = end - OVERLAP;
        }
        return chunks;
    }
}