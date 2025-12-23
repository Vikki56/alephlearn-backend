package com.example.demo.api;

import com.example.demo.entity.Report;
import com.example.demo.entity.ReportTargetType;
import com.example.demo.service.ReportService;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {

  private final ReportService service;
  private final UserRepository users;

  public ReportsController(ReportService service, UserRepository users) {
    this.service = service;
    this.users = users;
  }

  @PostMapping(consumes = "multipart/form-data")
  public ResponseEntity<?> createUserReport(
      @AuthenticationPrincipal User me,
      @RequestParam String reportedUserId,          // ✅ String now (id OR email)
      @RequestParam(required = false) String description,
      @RequestPart(required = false) MultipartFile screenshot
  ) throws Exception {

    Long targetId;
    // ✅ if numeric -> use as id, else treat as email
    try {
      targetId = Long.parseLong(reportedUserId);
    } catch (NumberFormatException ex) {
      targetId = users.findByEmail(reportedUserId)
          .orElseThrow(() -> new IllegalArgumentException("Reported user not found by email"))
          .getId();
    }

    Report r = service.create(
        ReportTargetType.USER,
        targetId,
        "USER_REPORT",
        description,
        me.getId(),
        me.getEmail(),
        screenshot
    );

    return ResponseEntity.ok(java.util.Map.of("id", r.getId(), "status", r.getStatus().name()));
  }
}