package com.example.demo.ai.dto;

import java.time.LocalDateTime;

public record AiChatMessageDto(
        Long id,
        String sender,
        String message,
        LocalDateTime createdAt
) {}