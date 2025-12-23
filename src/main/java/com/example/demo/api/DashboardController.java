package com.example.demo.api;

import com.example.demo.dto.DashboardSummaryDto;
import com.example.demo.service.DashboardService;
import com.example.demo.user.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public DashboardSummaryDto getSummary(@AuthenticationPrincipal User currentUser) {
        return dashboardService.getSummary(currentUser);
    }
}