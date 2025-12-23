package com.example.demo.dto.question;

import com.example.demo.domain.Question;
import java.time.Instant;

public record QuestionDto(
        Long id,
        String title,
        String body,
        String askedBy,
        String status,           // e.g., "OPEN", "CLAIMED", "RESOLVED"
        Integer maxClaimers,
        Long acceptedAnswerId,
        Instant createdAt,
        long claimedCount,
        String imageUrl
) {
    public static QuestionDto of(Question q, long claimedCount) {
        return new QuestionDto(
                q.getId(),
                q.getTitle(),
                q.getBody(),
                q.getAskedBy(),
                q.getStatus(),
                q.getMaxClaimers(),
                q.getAcceptedAnswerId(),
                q.getCreatedAt(),
                claimedCount,
                q.getImageUrl()
        );
    }
}
