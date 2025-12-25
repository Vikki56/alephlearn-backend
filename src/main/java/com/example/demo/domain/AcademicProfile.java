package com.example.demo.domain;

import com.example.demo.user.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
    name = "academic_profiles",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id" }) 
    }
)
public class AcademicProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "education_level", nullable = false, length = 50)
    private String educationLevel;

    @Column(name = "main_stream", length = 100)
    private String mainStream;

    @Column(name = "specialization", length = 100)
    private String specialization;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @Column(name = "interests", length = 255)
    private String interests;

// ====== getters / setters ======

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }


    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getMainStream() {
        return mainStream;
    }

    public void setMainStream(String mainStream) {
        this.mainStream = mainStream;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}