package com.example.demo.moderation;

import com.example.demo.user.User;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "user_reports")
public class UserReport {

    public enum Status { PENDING, WARNED, BLOCKED, BANNED, IGNORED }
    public enum Context { CHAT, DOUBT, QUIZ, PROFILE, OTHER }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="reported_user_id", nullable=false)
    private User reportedUser;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="reported_by_user_id", nullable=false)
    private User reportedBy;

    @Column(nullable=false, length=2000)
    private String reason;

    @Column(length=2000)
    private String proofUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private Context context = Context.OTHER;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private Status status = Status.PENDING;

    private Instant createdAt = Instant.now();

    // getters/setters (generate in IDE)
    public Long getId() { return id; }
    public User getReportedUser() { return reportedUser; }
    public void setReportedUser(User u) { this.reportedUser = u; }
    public User getReportedBy() { return reportedBy; }
    public void setReportedBy(User u) { this.reportedBy = u; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getProofUrl() { return proofUrl; }
    public void setProofUrl(String proofUrl) { this.proofUrl = proofUrl; }
    public Context getContext() { return context; }
    public void setContext(Context context) { this.context = context; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
}