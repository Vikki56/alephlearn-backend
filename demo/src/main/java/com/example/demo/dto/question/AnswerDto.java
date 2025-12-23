package com.example.demo.dto.question;

import com.example.demo.domain.Answer;
import java.time.Instant;

public record AnswerDto(
        Long id,
        Long questionId,
        String author,
        String body,
        String imageUrl,
        Instant createdAt
) {
    public static AnswerDto of(Answer a) {
        return new AnswerDto(
                a.getId(),
                a.getQuestionId(),
                a.getAuthor(),
                a.getBody(),
                a.getImageUrl(),
                a.getCreatedAt()
        );
    }
}
