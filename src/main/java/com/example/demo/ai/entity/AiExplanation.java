package com.example.demo.ai.entity;

import com.example.demo.entity.Doubt;
import com.example.demo.entity.DoubtAnswer;
import com.example.demo.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_explanations", indexes = {
        @Index(name = "ix_ai_expl_user", columnList = "user_id"),
        @Index(name = "ix_ai_expl_doubt", columnList = "doubt_id"),
        @Index(name = "ix_ai_expl_created", columnList = "created_at")
})
public class AiExplanation {

    public enum Status { PENDING, DONE, FAILED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user; // asker only

    @ManyToOne(optional = false)
    @JoinColumn(name = "doubt_id")
    private Doubt doubt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "accepted_answer_id")
    private DoubtAnswer acceptedAnswer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.PENDING;

    @Column(name = "final_explanation", columnDefinition = "TEXT")
    private String finalExplanation;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        if (status == null) status = Status.PENDING;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // getters/setters
    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Doubt getDoubt() { return doubt; }
    public void setDoubt(Doubt doubt) { this.doubt = doubt; }

    public DoubtAnswer getAcceptedAnswer() { return acceptedAnswer; }
    public void setAcceptedAnswer(DoubtAnswer acceptedAnswer) { this.acceptedAnswer = acceptedAnswer; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getFinalExplanation() { return finalExplanation; }
    public void setFinalExplanation(String finalExplanation) { this.finalExplanation = finalExplanation; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
