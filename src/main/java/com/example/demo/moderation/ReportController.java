// package com.example.demo.moderation;

// import com.example.demo.dto.ReportCreateResponse;
// import com.example.demo.entity.Report;
// import com.example.demo.entity.ReportTargetType;
// import com.example.demo.service.ReportService;
// import com.example.demo.user.User;
// import org.springframework.http.MediaType;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.multipart.MultipartFile;

// @RestController
// @RequestMapping("/api/reports")
// public class ReportController {

//   private final ReportService reportService;

//   public ReportController(ReportService reportService) {
//     this.reportService = reportService;
//   }

//   @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//   public ReportCreateResponse create(
//       @AuthenticationPrincipal User me,
//       @RequestParam ReportTargetType targetType,
//       @RequestParam Long targetId,
//       @RequestParam String reason,
//       @RequestParam(required = false) String description,
//       @RequestPart(required = false) MultipartFile proof
//   ) throws Exception {

//     Long reporterUserId = me.getId();
//     String reporterEmail = me.getEmail();

//     Report r = reportService.create(
//         targetType, targetId, reason, description,
//         reporterUserId, reporterEmail, proof
//     );

//     return new ReportCreateResponse(r.getId(), r.getStatus().name());
//   }
// }