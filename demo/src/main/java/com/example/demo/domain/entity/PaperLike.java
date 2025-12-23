package com.example.demo.domain.entity;

import com.example.demo.user.User;
import jakarta.persistence.*;

@Entity
@Table(
    name = "paper_likes",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"paper_id", "user_id"})
    }
)
public class PaperLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paper_id")
    private PreviousPaper paper;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    public Long getId() {
        return id;
    }

    public PreviousPaper getPaper() {
        return paper;
    }

    public void setPaper(PreviousPaper paper) {
        this.paper = paper;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}