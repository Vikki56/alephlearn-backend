package com.example.demo.moderation.dto;

import com.example.demo.moderation.UserReport;

public record CreateReportRequest(
        Long reportedUserId,
        String reason,
        String proofUrl,
        UserReport.Context context
) {}