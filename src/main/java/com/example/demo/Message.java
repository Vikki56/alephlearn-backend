package com.example.demo;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "chat_messages",
    indexes = { @Index(name = "ix_room_ts", columnList = "room, ts") }
)
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String room;

    @Column(nullable = false, length = 128)
    private String userName;

    @Column(nullable = false, length = 5000)
    private String text;

    @Column(nullable = false)
    private Instant ts;

    @Column(length = 128)
    private String clientId;

    private Instant editedAt;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(name = "reply_to_id")
    private Long replyToId;

    @Column(nullable = false)
    private boolean pinned = false;

    @Column(length = 128)
    private String userEmail;

    // ---------- Getters / Setters ----------
    public Long getId() { return id; }
    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public Instant getTs() { return ts; }

    public void setTs(Instant ts) { this.ts = ts; }

    public void setTs(long epochMillis) {
        this.ts = Instant.ofEpochMilli(epochMillis);
    }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public Instant getEditedAt() { return editedAt; }
    public void setEditedAt(Instant editedAt) { this.editedAt = editedAt; }
    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
    public Long getReplyToId() { return replyToId; }
    public void setReplyToId(Long replyToId) { this.replyToId = replyToId; }
    public boolean isPinned() { return pinned; }
    public void setPinned(boolean pinned) { this.pinned = pinned; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
}