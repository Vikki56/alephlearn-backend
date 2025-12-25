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

    @PostMapping
    public ResponseEntity<?> saveMyProfile(@RequestBody AcademicProfileDto dto) {
        try {
            AcademicProfileDto saved = academicProfileService.saveMyProfile(dto);
            return ResponseEntity.ok(saved);
        }
        catch (IllegalStateException ex) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)   
                    .body(ex.getMessage());
        }
        catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST) 
                    .body(ex.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile() {
        try {
            AcademicProfileDto dto = academicProfileService.getMyProfile();
            return ResponseEntity.ok(dto);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile not found");
        }
    }

    @GetMapping("/has")
    public boolean hasProfile() {
        return academicProfileService.hasProfile();
    }

    @GetMapping("/options")
    public ResponseEntity<AcademicOptionsDto> getOptions() {
        AcademicOptionsDto dto = academicProfileService.getOptions();
        return ResponseEntity.ok(dto);
    }
}