package com.example.demo.admin;
import com.example.demo.entity.ReportTargetType;
import com.example.demo.dto.ReportDto;
import com.example.demo.dto.ReportStatusUpdateRequest;
import com.example.demo.entity.Report;
import com.example.demo.entity.ReportStatus;
import com.example.demo.repo.ReportRepository;
import com.example.demo.service.ReportService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/reports")
public class AdminReportController {

  private final ReportRepository repo;
  private final ReportService service;

  private final AdminModerationService moderation;

  public AdminReportController(ReportRepository repo, ReportService service, AdminModerationService moderation) {
    this.repo = repo;
    this.service = service;
    this.moderation = moderation;
  }

  @GetMapping
  public List<ReportDto> list() {
    return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
  }

  @GetMapping("/{id}")
  public ReportDto get(@PathVariable Long id) {
    return toDto(service.get(id));
  }

  @GetMapping("/{id}/proof")
  public ResponseEntity<FileSystemResource> proof(@PathVariable Long id) {
    Report r = service.get(id);
    if (r.getProofPath() == null) return ResponseEntity.notFound().build();

    FileSystemResource res = new FileSystemResource(Path.of(r.getProofPath()));
    MediaType mt = (r.getProofMime() != null)
        ? MediaType.parseMediaType(r.getProofMime())
        : MediaType.APPLICATION_OCTET_STREAM;

    return ResponseEntity.ok()
        .contentType(mt)
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "inline; filename=\"" + (r.getProofOriginalName() == null ? "proof" : r.getProofOriginalName()) + "\"")
        .body(res);
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody ReportStatusUpdateRequest req) {
  
    Report r = service.get(id);
  
    ReportStatus st = ReportStatus.valueOf(req.status());
    service.updateStatus(id, st, req.adminNotes());
String tt = (r.getTargetType() == null) ? "" : r.getTargetType().name();
Long userId = r.getTargetId();

if (userId != null && tt.startsWith("USER")) {
  String reason = req.actionReason();

  switch (st) {
    case WARNED -> moderation.warnUser(userId, reason);
    case BLOCKED -> moderation.blockUser(userId, req.blockDays() == null ? 1 : req.blockDays(), reason);
    case BANNED -> moderation.banUser(userId, reason);
    default -> { }
  }
}
  
    return ResponseEntity.ok().build();
  }

  private ReportDto toDto(Report r) {
    return new ReportDto(
        r.getId(),
        r.getTargetType().name(),
        r.getTargetId(),
        r.getReason(),
        r.getDescription(),
        r.getReporterUserId(),
        r.getReporterEmail(),
        r.getStatus().name(),
        r.getProofOriginalName(),
        r.getProofMime(),
        r.getProofPath() != null,
        r.getAdminNotes(),
        r.getCreatedAt().toString()
    );
  }
}