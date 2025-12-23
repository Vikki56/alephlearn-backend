package com.example.demo.dto.question;

import com.example.demo.domain.Claim;
import java.time.Instant;

public record ClaimDto(
        Long id,
        Long questionId,
        String userId,
        Instant createdAt,
        Instant expiresAt
) {
    public static ClaimDto of(Claim c) {
        return new ClaimDto(
                c.getId(),
                c.getQuestionId(),
                c.getUserId(),
                c.getCreatedAt(),
                c.getExpiresAt()
        );
    }
}
