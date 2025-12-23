package com.example.demo.api;

import com.example.demo.dto.ProfileSummaryDto;
import com.example.demo.dto.ProfileLikeDto;
import com.example.demo.service.ProfileService;
import com.example.demo.user.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.MiniProfileDto;
import java.util.List;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/card")
    public MiniProfileDto getProfileCard(
            @RequestParam("email") String email,
            @AuthenticationPrincipal User currentUser
    ) {
        return profileService.getMiniProfileFor(email, currentUser);
    }

    @PostMapping("/card/like")
    public ProfileLikeDto toggleLike(
            @RequestParam("email") String email,
            @AuthenticationPrincipal User currentUser
    ) {
        return profileService.toggleLikeFor(email, currentUser);
    }
    // ==================== INTERESTS ====================
    @GetMapping("/interests")
    public List<String> getMyInterests(@AuthenticationPrincipal User currentUser) {
        return profileService.getMyInterests(currentUser);
    }

    @PutMapping("/interests")
    public List<String> saveMyInterests(
            @AuthenticationPrincipal User currentUser,
            @RequestBody List<String> labels
    ) {
        return profileService.saveMyInterests(currentUser, labels);
    }


    // ==================== LIKES ====================
    @GetMapping("/likes/me")
    public ProfileLikeDto getMyLikes(@AuthenticationPrincipal User currentUser) {
        return profileService.getMyLikes(currentUser);
    }

    @PostMapping("/likes/me/toggle")
    public ProfileLikeDto toggleMyLike(@AuthenticationPrincipal User currentUser) {
        return profileService.toggleSelfLike(currentUser);
    }


    // ==================== PROFILE SUMMARY ====================
    @GetMapping("/me")
    public ProfileSummaryDto me(@AuthenticationPrincipal User currentUser) {
        return profileService.getMyProfile(currentUser);
    }
}