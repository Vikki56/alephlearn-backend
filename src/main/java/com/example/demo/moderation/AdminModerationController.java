package com.example.demo.moderation;

import com.example.demo.moderation.dto.ReportResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/moderation")
public class AdminModerationController {

    private final ModerationService moderation;

    public AdminModerationController(ModerationService moderation) {
        this.moderation = moderation;
    }

    @GetMapping("/reports")
    public List<ReportResponse> all() {
        return moderation.listAll();
    }

    @PostMapping("/reports/{id}/warn")
    public ReportResponse warn(@PathVariable Long id) {
        return moderation.warn(id);
    }

    @PostMapping("/reports/{id}/block")
    public ReportResponse block(
            @PathVariable Long id,
            @RequestParam(defaultValue = "7") int days,
            @RequestParam(required = false) String reason
    ) {
        return moderation.block(id, days, reason);
    }

    @PostMapping("/reports/{id}/ban")
    public ReportResponse ban(
            @PathVariable Long id,
            @RequestParam(required = false) String reason
    ) {
        return moderation.ban(id, reason);
    }

    @PostMapping("/reports/{id}/ignore")
    public ReportResponse ignore(@PathVariable Long id) {
        return moderation.ignore(id);
    }
}