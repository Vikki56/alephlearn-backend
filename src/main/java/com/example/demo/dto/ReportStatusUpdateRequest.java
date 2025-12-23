package com.example.demo.dto;

public record ReportStatusUpdateRequest(
    String status,
    String adminNotes,
    String action,        // ✅ ADD THIS (NONE/WARN/BLOCK/BAN)
    Integer blockDays,
    String actionReason
) {}