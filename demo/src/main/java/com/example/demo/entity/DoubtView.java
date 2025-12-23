// src/main/java/com/example/demo/entity/DoubtView.java
package com.example.demo.entity;

import com.example.demo.user.User;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "doubt_views",
    uniqueConstraints = @UniqueConstraint(columnNames = {"doubt_id", "viewer_id"})
)
public class DoubtView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "doubt_id")
    private Doubt doubt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "viewer_id")
    private User viewer;

    @Column(nullable = false)
    private Instant viewedAt = Instant.now();

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Doubt getDoubt() { return doubt; }
    public void setDoubt(Doubt doubt) { this.doubt = doubt; }

    public User getViewer() { return viewer; }
    public void setViewer(User viewer) { this.viewer = viewer; }

    public Instant getViewedAt() { return viewedAt; }
    public void setViewedAt(Instant viewedAt) { this.viewedAt = viewedAt; }
}