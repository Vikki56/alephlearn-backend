package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(
        name = "rooms",
        uniqueConstraints = @UniqueConstraint(columnNames = {"subject", "slug"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String subject;          // e.g. "math", "cs"

    @Column(nullable = false, length = 140)
    private String slug;             // e.g. "java-programming"

    @Column(nullable = false, length = 120)
    private String title;            // e.g. "General Discussion"

    @Column(length = 200)
    private String tags;             // comma-separated tags, optional

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private Visibility visibility = Visibility.PUBLIC;   // default value

    @Builder.Default
    private Integer memberCount = 0;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Builder.Default
    private Instant lastActivity = Instant.now();

    // ---------------- Academic Stream Fields ----------------
    @Column(name = "education_level")
    private String educationLevel;

    @Column(name = "main_stream")
    private String mainStream;

    @Column(name = "specialization")
    private String specialization;

    // ✅ Explicit setters (agar Lombok index / plugin issue ho)
    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public void setMainStream(String mainStream) {
        this.mainStream = mainStream;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    // ✅ Nested enum
    public enum Visibility {
        PUBLIC, PRIVATE, TEMP
    }
}