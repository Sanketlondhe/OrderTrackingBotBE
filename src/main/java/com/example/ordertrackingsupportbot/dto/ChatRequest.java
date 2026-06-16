package com.example.ordertrackingsupportbot.dto;


import jakarta.validation.constraints.NotBlank;

public record ChatRequest(
        @NotBlank(message = "Message cannot be blank")
        String message,

        String sessionId  // optional: for conversation memory
) {}