package com.example.demo.api;

import com.example.demo.entity.Report;
import com.example.demo.entity.ReportStatus;
import com.example.demo.entity.ReportTargetType;
import com.example.demo.repo.ReportRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class PublicReportController {

  private final ReportRepository reportRepo;

  public PublicReportController(ReportRepository reportRepo) {
    this.reportRepo = reportRepo;
  }

  // Landing page se (no-login) bug report
  @PostMapping("/bug")
  public ResponseEntity<?> reportBug(@RequestBody BugReportRequest req) {

    Report r = new Report();

    // ✅ if you added APP in enum
    r.setTargetType(ReportTargetType.APP);
    r.setTargetId(0L);

    r.setReason("BUG_REPORT");
    r.setDescription(buildBugDescription(req));

    // public report: reporter info optional
    r.setReporterUserId(0L);
    r.setReporterEmail(req.email() == null ? "anonymous@public" : req.email().trim());

    r.setStatus(ReportStatus.OPEN);
    r.setAdminNotes(null);

    reportRepo.save(r);
    return ResponseEntity.ok().body(java.util.Map.of("ok", true, "id", r.getId()));
  }

  private String buildBugDescription(BugReportRequest req) {
    StringBuilder sb = new StringBuilder();
    sb.append("Title: ").append(nullToEmpty(req.title())).append("\n");
    sb.append("Page: ").append(nullToEmpty(req.page())).append("\n");
    sb.append("Steps: ").append(nullToEmpty(req.steps())).append("\n");
    sb.append("Expected: ").append(nullToEmpty(req.expected())).append("\n");
    sb.append("Actual: ").append(nullToEmpty(req.actual())).append("\n");
    sb.append("Device: ").append(nullToEmpty(req.device())).append("\n");
    return sb.toString();
  }

  private String nullToEmpty(String s) { return s == null ? "" : s; }

  public record BugReportRequest(
      String email,
      String title,
      String page,
      String steps,
      String expected,
      String actual,
      String device
  ) {}
}