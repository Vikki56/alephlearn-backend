package com.example.demo.domain;

import com.example.demo.user.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
    name = "profile_likes",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"target_user_id", "liked_by_id"})
    }
)
public class ProfileLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_user_id", nullable = false)
    private User targetUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "liked_by_id", nullable = false)
    private User likedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }


    public Long getId() {
        return id;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }

    public User getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(User likedBy) {
        this.likedBy = likedBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}