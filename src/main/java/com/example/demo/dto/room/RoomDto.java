package com.example.demo.dto.room;

import com.example.demo.domain.Room;
import java.time.Instant;

public record RoomDto(
        Long id,
        String subject,      
        String slug,           
        String title,       
        String tags,          
        String visibility,     
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
