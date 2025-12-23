package com.example.demo.domain;

import com.example.demo.user.User;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "user_login_days",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "login_date"})
)
public class UserLoginDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "login_date", nullable = false)
    private LocalDate loginDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(LocalDate loginDate) {
        this.loginDate = loginDate;
    }
}