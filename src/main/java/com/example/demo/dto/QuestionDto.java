package com.example.demo.dto;

import com.example.demo.domain.Question;
import java.time.Instant;

public record QuestionDto(
    Long id,
    String title,
    String body,
    String askedBy,
    String status,
    Integer maxClaimers,
    Long acceptedAnswerId,
    Instant createdAt,
    long claimedCount
) {
    public static QuestionDto of(Question q, long claimed) {
        return new QuestionDto(
            q.getId(),
            q.getTitle(),
            q.getBody(),
            q.getAskedBy(),
            q.getStatus(),
            q.getMaxClaimers(),
            q.getAcceptedAnswerId(),
            q.getCreatedAt(),
            claimed
        );
    }
}