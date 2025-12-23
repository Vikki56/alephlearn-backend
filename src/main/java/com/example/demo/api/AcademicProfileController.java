package com.example.demo.api;

import com.example.demo.domain.dto.profile.AcademicOptionsDto;
import com.example.demo.domain.dto.profile.AcademicProfileDto;
import com.example.demo.service.AcademicProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile/academic")
public class AcademicProfileController {

    private final AcademicProfileService academicProfileService;

    public AcademicProfileController(AcademicProfileService academicProfileService) {
        this.academicProfileService = academicProfileService;
    }

    // 🔐 Create academic profile (sirf ek hi baar allowed)
    @PostMapping
    public ResponseEntity<?> saveMyProfile(@RequestBody AcademicProfileDto dto) {
        try {
            AcademicProfileDto saved = academicProfileService.saveMyProfile(dto);
            return ResponseEntity.ok(saved);
        }
        // Agar profile pehle se hai
        catch (IllegalStateException ex) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)   // 409
                    .body(ex.getMessage());
        }
        // Agar invalid combination hai
        catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST) // 400
                    .body(ex.getMessage());
        }
    }

    // Profile details (agar nahi mila to 404)
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile() {
        try {
            AcademicProfileDto dto = academicProfileService.getMyProfile();
            return ResponseEntity.ok(dto);
        } catch (RuntimeException ex) {
            // "Profile not found" wali exception aa jayegi
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile not found");
        }
    }

    // Sirf check: profile hai ya nahi (guard ke liye)
    @GetMapping("/has")
    public boolean hasProfile() {
        return academicProfileService.hasProfile();
    }

    // Dropdown options
    @GetMapping("/options")
    public ResponseEntity<AcademicOptionsDto> getOptions() {
        AcademicOptionsDto dto = academicProfileService.getOptions();
        return ResponseEntity.ok(dto);
    }
}