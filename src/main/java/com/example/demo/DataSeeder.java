package com.example.demo;

import com.example.demo.domain.Room;          
import com.example.demo.RoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Instant;

@Slf4j
@Configuration
@Profile("!prod")
public class DataSeeder {

    @Bean
    CommandLineRunner seed(RoomRepository rooms) {
        return args -> {
            seedOne(rooms, "math",    "General",          "general");
            seedOne(rooms, "cs",      "Java Programming", "java-programming");
            seedOne(rooms, "physics", "Mechanics Doubts", "mechanics-doubts");
        };
    }

    private void seedOne(RoomRepository rooms, String subject, String title, String slug) {
        rooms.findBySubjectAndSlug(subject, slug).ifPresentOrElse(
            existing -> log.debug("ⓘ Room already exists: {}/{}", subject, slug),
            () -> {
                Room saved = rooms.save(Room.builder()
                        .subject(subject)
                        .title(title)
                        .slug(slug)
                        .visibility(Room.Visibility.PUBLIC)
                        .memberCount(0)
                        .lastActivity(Instant.now())
                        .build());
                log.info("✅ Seeded room: {} / {} (id={})", subject, slug, saved.getId());
            }
        );
    }
}
