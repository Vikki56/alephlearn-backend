package com.example.demo.entity;

import com.example.demo.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class DoubtLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Doubt doubt;

    @ManyToOne(optional = false)
    private User user;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // ------- GETTERS & SETTERS -------

    public Long getId() {
        return id;
    }

    public Doubt getDoubt() {
        return doubt;
    }

    public void setDoubt(Doubt doubt) {
        this.doubt = doubt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}