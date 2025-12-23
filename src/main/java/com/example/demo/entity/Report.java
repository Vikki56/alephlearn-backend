package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "reports")
public class Report {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ReportTargetType targetType;

  @Column(nullable = false)
  private Long targetId;

  @Column(nullable = false, length = 80)
  private String reason;

  @Column(length = 2000)
  private String description;

  @Column(nullable = false)
  private Long reporterUserId;

  @Column(nullable = false)
  private String reporterEmail;

  private String proofPath;
  private String proofOriginalName;
  private String proofMime;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ReportStatus status = ReportStatus.OPEN;

  @Column(length = 2000)
  private String adminNotes;

  private Instant createdAt = Instant.now();
  private Instant updatedAt = Instant.now();

  @PreUpdate
  public void onUpdate() { updatedAt = Instant.now(); }

  // --- getters/setters ---
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public ReportTargetType getTargetType() { return targetType; }
  public void setTargetType(ReportTargetType targetType) { this.targetType = targetType; }

  public Long getTargetId() { return targetId; }
  public void setTargetId(Long targetId) { this.targetId = targetId; }

  public String getReason() { return reason; }
  public void setReason(String reason) { this.reason = reason; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public Long getReporterUserId() { return reporterUserId; }
  public void setReporterUserId(Long reporterUserId) { this.reporterUserId = reporterUserId; }

  public String getReporterEmail() { return reporterEmail; }
  public void setReporterEmail(String reporterEmail) { this.reporterEmail = reporterEmail; }

  public String getProofPath() { return proofPath; }
  public void setProofPath(String proofPath) { this.proofPath = proofPath; }

  public String getProofOriginalName() { return proofOriginalName; }
  public void setProofOriginalName(String proofOriginalName) { this.proofOriginalName = proofOriginalName; }

  public String getProofMime() { return proofMime; }
  public void setProofMime(String proofMime) { this.proofMime = proofMime; }

  public ReportStatus getStatus() { return status; }
  public void setStatus(ReportStatus status) { this.status = status; }

  public String getAdminNotes() { return adminNotes; }
  public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }

  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

  public Instant getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}