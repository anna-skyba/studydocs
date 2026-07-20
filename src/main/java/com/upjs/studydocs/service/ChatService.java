package com.upjs.studydocs.service;

import com.upjs.studydocs.dto.ChatAnswerResponse;
import com.upjs.studydocs.dto.SearchResultResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {
    private final SemanticSearchService semanticSearchService;
    private final ChatClient chatClient;

    public ChatService(SemanticSearchService semanticSearchService, ChatClient.Builder chatClientBuilder) {
        this.semanticSearchService = semanticSearchService;
        this.chatClient = chatClientBuilder.build();
    }

    public ChatAnswerResponse ask(String question) {
        List<SearchResultResponse> sources = semanticSearchService.search(question);
        String context = sources.stream()
                .map(source -> """
                        Source file: %s
                        Chunk index: %d
                        Content:
                        %s
                        """.formatted(
                        source.filename(),
                        source.chunkIndex(),
                        source.content()))
                .collect(Collectors.joining("\n---\n"));
        String answer = chatClient.prompt()
                .system("""
                        You are a study assistant.
                        Answer the user's question only using the provided context.
                        If the answer is not in the context, say that the document does not contain enough information.
                        Keep the answer clear and concise.
                        """)
                .user("""
                        Question:
                        %s

                        Context:
                        %s
                        """.formatted(question, context))
                .call()
                .content();
        return new ChatAnswerResponse(answer, sources);
    }
}