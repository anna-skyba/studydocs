package com.upjs.studydocs.controller;

import com.upjs.studydocs.dto.ChatAnswerResponse;
import com.upjs.studydocs.dto.ChatRequest;
import com.upjs.studydocs.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ChatAnswerResponse chat(@Valid @RequestBody ChatRequest request) {
        return chatService.ask(request.question());
    }
}