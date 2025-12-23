package com.example.demo;

import com.example.demo.dto.ActivityStatsDto;
import com.example.demo.service.DoubtService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ActivityController {

    private final DoubtService doubtService;

    public ActivityController(DoubtService doubtService) {
        this.doubtService = doubtService;
    }

    @GetMapping("/activity")
    public ActivityStatsDto getActivity(@RequestParam Long userId) {
        return doubtService.getUserActivity(userId);
    }
}