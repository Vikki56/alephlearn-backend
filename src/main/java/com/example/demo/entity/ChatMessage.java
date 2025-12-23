package com.example.demo.entity;

// import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
// import jakarta.persistence.Column;


@Entity
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // @Column(length = 128)
    private String subject;
    // @Column(length = 128)
    private String slug;
    // @Column(length = 5000)
    private String text;

    private long ts;
    // @Column(length = 128)
    private String clientId;
    // @Column(length = 128)
    private String userName;
    // @Column(length = 191)
    private String userEmail;

    private boolean deleted;

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public long getTs() { return ts; }
    public void setTs(long ts) { this.ts = ts; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
