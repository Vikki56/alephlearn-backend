package com.example.demo.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "claims", indexes = {
        @Index(name = "ix_claim_q_user", columnList = "questionId,userId")
})
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long questionId;

    private String userId;

    private Instant createdAt = Instant.now();

    /** null means no expiry */
    private Instant expiresAt;

    // --- getters & setters ---
    public Long getId() { return id; }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

    // convenience
    @Transient
    public boolean isActive() {
        return expiresAt == null || expiresAt.isAfter(Instant.now());
    }
}
