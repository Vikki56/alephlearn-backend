package com.example.demo.domain.dto.quiz;

import java.time.Instant;

public class RealtimeAttemptDto {
    private Long id;
    private String status;       // optional
    private Instant createdAt;   // optional

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}