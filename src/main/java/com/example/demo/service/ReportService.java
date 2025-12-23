package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repo.ReportRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.UUID;

@Service
public class ReportService {

  private final ReportRepository repo;

  @Value("${app.upload.reports-dir:uploads/reports}")
  private String reportsDir;

  public ReportService(ReportRepository repo) { this.repo = repo; }

  public Report create(
      ReportTargetType targetType,
      Long targetId,
      String reason,
      String description,
      Long reporterUserId,
      String reporterEmail,
      MultipartFile proof
  ) throws Exception {

    Report r = new Report();
    r.setTargetType(targetType);
    r.setTargetId(targetId);
    r.setReason(reason);
    r.setDescription(description);
    r.setReporterUserId(reporterUserId);
    r.setReporterEmail(reporterEmail);

    if (proof != null && !proof.isEmpty()) {
      Path dir = Paths.get(reportsDir);
      Files.createDirectories(dir);

      String original = (proof.getOriginalFilename() == null) ? "proof" : proof.getOriginalFilename();
      String safeName = UUID.randomUUID() + "_" + original.replaceAll("[^a-zA-Z0-9._-]", "_");
      Path filePath = dir.resolve(safeName);

      Files.copy(proof.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

      r.setProofPath(filePath.toString());
      r.setProofOriginalName(original);
      r.setProofMime(proof.getContentType());
    }

    return repo.save(r);
  }

  public Report get(Long id) {
    return repo.findById(id).orElseThrow(() -> new RuntimeException("Report not found"));
  }

  public Report updateStatus(Long id, ReportStatus status, String adminNotes) {
    Report r = get(id);
    r.setStatus(status);
    r.setAdminNotes(adminNotes);
    return repo.save(r);
  }
}