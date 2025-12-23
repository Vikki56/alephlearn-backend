package com.example.demo.dto.room;

import com.example.demo.domain.Room;
import java.time.Instant;

public record RoomDto(
        Long id,
        String subject,        // e.g., "math", "cs"
        String slug,           // e.g., "general", "java-programming"
        String title,          // human friendly
        String tags,           // comma-separated for now
        String visibility,     // PUBLIC / PRIVATE / TEMP (string to decouple frontend from enum)
        Integer memberCount,
        Instant createdAt,
        Instant lastActivity
) {
    public static RoomDto of(Room r) {
        return new RoomDto(
                r.getId(),
                r.getSubject(),
                r.getSlug(),
                r.getTitle(),
                r.getTags(),
                r.getVisibility().name(),
                r.getMemberCount(),
                r.getCreatedAt(),
                r.getLastActivity()
        );
    }
}
