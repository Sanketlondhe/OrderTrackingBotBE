package com.example.ordertrackingsupportbot.controller;

import com.example.ordertrackingsupportbot.dto.ChatRequest;
import com.example.ordertrackingsupportbot.dto.ChatResponse;
import com.example.ordertrackingsupportbot.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        // Generate session ID if not provided (new conversation)
        String sessionId = (request.sessionId() != null && !request.sessionId().isBlank())
                ? request.sessionId()
                : UUID.randomUUID().toString();

        String reply = chatService.chat(request.message(), sessionId);
        return ResponseEntity.ok(new ChatResponse(reply, sessionId));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Order Tracking Bot is running!");
    }
}
