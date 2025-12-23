package com.example.demo.ai.dto;

import java.time.LocalDateTime;

public record ResolvedDoubtCardDto(
        Long doubtId,
        String subject,
        String title,
        LocalDateTime resolvedAt
) {}