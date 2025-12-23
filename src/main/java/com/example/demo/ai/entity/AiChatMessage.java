package com.example.demo.ai.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_chat_messages", indexes = {
        @Index(name = "ix_ai_msg_expl", columnList = "explanation_id"),
        @Index(name = "ix_ai_msg_created", columnList = "created_at")
})
public class AiChatMessage {

    public enum Sender { USER, AI }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "explanation_id")
    private AiExplanation explanation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Sender sender;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public AiExplanation getExplanation() { return explanation; }
    public void setExplanation(AiExplanation explanation) { this.explanation = explanation; }

    public Sender getSender() { return sender; }
    public void setSender(Sender sender) { this.sender = sender; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
