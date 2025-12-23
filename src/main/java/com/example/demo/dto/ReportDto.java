package com.example.demo.dto;

public record ReportDto(
  Long id,
  String targetType,
  Long targetId,
  String reason,
  String description,
  Long reporterUserId,
  String reporterEmail,
  String status,
  String proofOriginalName,
  String proofMime,
  boolean hasProof,
  String adminNotes,
  String createdAt
) {}