package com.example.demo.entity;

import com.example.demo.user.User;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class AnswerReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private DoubtAnswer answer;

    @ManyToOne(optional = false)
    private User replier;

    @Column(nullable = false, length = 2000)
    private String text;

    private Instant createdAt = Instant.now();

    // ====== GETTERS & SETTERS ======

    public Long getId() {
        return id;
    }

    public DoubtAnswer getAnswer() {
        return answer;
    }

    public void setAnswer(DoubtAnswer answer) {
        this.answer = answer;
    }

    public User getReplier() {
        return replier;
    }

    public void setReplier(User replier) {
        this.replier = replier;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}