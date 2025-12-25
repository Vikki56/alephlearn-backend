package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
  name = "room_kicks",
  uniqueConstraints = @UniqueConstraint(columnNames = {"questionId", "kickedUserEmail"})
)
public class RoomKick {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long questionId;

    @Column(nullable = false, length = 300)
    private String room; 

    @Column(nullable = false, length = 200)
    private String kickedUserEmail;

    @Column(nullable = false, length = 200)
    private String kickedByEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RoomKickReason reason;

    @Column(nullable = false, length = 2000)
    private String note;

    @Column(length = 2000)
    private String proofUrl;

    private Instant createdAt = Instant.now();

    // getters/setters
    public Long getId() { return id; }
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public String getKickedUserEmail() { return kickedUserEmail; }
    public void setKickedUserEmail(String kickedUserEmail) { this.kickedUserEmail = kickedUserEmail; }

    public String getKickedByEmail() { return kickedByEmail; }
    public void setKickedByEmail(String kickedByEmail) { this.kickedByEmail = kickedByEmail; }

    public RoomKickReason getReason() { return reason; }
    public void setReason(RoomKickReason reason) { this.reason = reason; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getProofUrl() { return proofUrl; }
    public void setProofUrl(String proofUrl) { this.proofUrl = proofUrl; }

    public Instant getCreatedAt() { return createdAt; }
}