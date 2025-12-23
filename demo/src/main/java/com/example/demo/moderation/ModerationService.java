package com.example.demo.moderation;

import com.example.demo.moderation.dto.CreateReportRequest;
import com.example.demo.moderation.dto.ReportResponse;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class ModerationService {

    private final UserRepository users;
    private final UserReportRepository reports;

    public ModerationService(UserRepository users, UserReportRepository reports) {
        this.users = users;
        this.reports = reports;
    }

    public ReportResponse createReport(User reporter, CreateReportRequest req) {
        User reported = users.findById(req.reportedUserId())
                .orElseThrow(() -> new IllegalArgumentException("Reported user not found"));

        UserReport r = new UserReport();
        r.setReportedBy(reporter);
        r.setReportedUser(reported);
        r.setReason(req.reason());
        r.setProofUrl(req.proofUrl());
        r.setContext(req.context() != null ? req.context() : UserReport.Context.OTHER);

        UserReport saved = reports.save(r);
        return toDto(saved);
    }

    // ADMIN
    public List<ReportResponse> listAll() {
        return reports.findAllByOrderByCreatedAtDesc().stream().map(this::toDto).toList();
    }

    public ReportResponse warn(Long reportId) {
        UserReport r = reports.findById(reportId).orElseThrow();
        r.setStatus(UserReport.Status.WARNED);
        return toDto(r);
    }

    public ReportResponse block(Long reportId, int days, String reason) {
        UserReport r = reports.findById(reportId).orElseThrow();
        User u = r.getReportedUser();

        u.setBlocked(true);
        u.setBlockedUntil(Instant.now().plusSeconds(days * 86400L));
        u.setBlockReason(reason != null ? reason : "Temporarily blocked");

        r.setStatus(UserReport.Status.BLOCKED);
        return toDto(r);
    }

    public ReportResponse ban(Long reportId, String reason) {
        UserReport r = reports.findById(reportId).orElseThrow();
        User u = r.getReportedUser();

        u.setBlocked(true);
        u.setBlockedUntil(null); // permanent
        u.setBlockReason(reason != null ? reason : "Permanently banned");

        r.setStatus(UserReport.Status.BANNED);
        return toDto(r);
    }

    public ReportResponse ignore(Long reportId) {
        UserReport r = reports.findById(reportId).orElseThrow();
        r.setStatus(UserReport.Status.IGNORED);
        return toDto(r);
    }

    private ReportResponse toDto(UserReport r) {
        return new ReportResponse(
                r.getId(),
                r.getReportedUser().getId(),
                r.getReportedUser().getEmail(),
                r.getReportedBy().getId(),
                r.getReportedBy().getEmail(),
                r.getReason(),
                r.getProofUrl(),
                r.getContext(),
                r.getStatus(),
                r.getCreatedAt()
        );
    }
}