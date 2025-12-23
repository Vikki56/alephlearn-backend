package com.example.demo.moderation.dto;

import com.example.demo.moderation.UserReport;

import java.time.Instant;

public record ReportResponse(
        Long id,
        Long reportedUserId,
        String reportedUserEmail,
        Long reportedById,
        String reportedByEmail,
        String reason,
        String proofUrl,
        UserReport.Context context,
        UserReport.Status status,
        Instant createdAt
) {}