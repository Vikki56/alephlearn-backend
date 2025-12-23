package com.example.demo.user;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;   

@Entity
@Table(
    name = "users",
    uniqueConstraints = @UniqueConstraint(columnNames = "email")
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 160, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash; // store the encoded password

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.USER;

    @Column(name = "reset_token")
private String resetToken;

@Column(name = "last_login_date")
private LocalDateTime lastLoginDate;   // ✅

    private Instant createdAt = Instant.now();


    @Column(nullable = false)
    private boolean blocked = false;
    
    @Column(name = "blocked_until")
    private Instant blockedUntil;
    
    @Column(length = 500)
    private String blockReason;



    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { 
        this.id = id; 
    }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email.toLowerCase(); }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Instant getCreatedAt() { return createdAt; }

public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }
    public LocalDateTime getLastLoginDate() { return lastLoginDate; }
    
    public void setLastLoginDate(LocalDateTime lastLoginDate) { this.lastLoginDate = lastLoginDate; }
    public boolean isBlocked() { return blocked; }
public void setBlocked(boolean blocked) { this.blocked = blocked; }

public Instant getBlockedUntil() { return blockedUntil; }
public void setBlockedUntil(Instant blockedUntil) { this.blockedUntil = blockedUntil; }

public String getBlockReason() { return blockReason; }
public void setBlockReason(String blockReason) { this.blockReason = blockReason; }
}