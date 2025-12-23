package com.example.demo.service;

import com.example.demo.domain.AcademicProfile;
import com.example.demo.domain.dto.profile.AcademicProfileDto;
import com.example.demo.repository.AcademicProfileRepository;
import com.example.demo.security.AuthUser;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;


import org.springframework.stereotype.Service;
import com.example.demo.domain.academic.AcademicCatalog;
// import com.example.demo.domain.AcademicCatalog;
import com.example.demo.domain.dto.profile.AcademicOptionsDto;

@Service
public class AcademicProfileService {

    private final AcademicProfileRepository profileRepository;
    private final UserRepository userRepository; // (optional, future use)

    public AcademicProfileService(AcademicProfileRepository profileRepository,
                                  UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    // ✅ Use AuthUser.current() helper
    private User getCurrentUser() {
        User user = AuthUser.current();
        if (user == null) {
            throw new RuntimeException("No authenticated user in context");
        }
        return user;
    }

    public AcademicProfileDto getMyProfile() {
        User user = getCurrentUser();
        AcademicProfile profile = profileRepository
                .findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        return toDto(profile);
    }

    public boolean hasProfile() {
        User user = getCurrentUser();
        return profileRepository.existsByUser(user);
    }

    public AcademicOptionsDto getOptions() {
        AcademicOptionsDto dto = new AcademicOptionsDto();
    
        // Set -> List convert
        dto.setEducationLevels(
                new ArrayList<>(AcademicCatalog.EDUCATION_LEVELS)
        );
        dto.setMainStreams(
                new ArrayList<>(AcademicCatalog.MAIN_STREAMS)
        );
        dto.setSpecializations(
                new ArrayList<>(AcademicCatalog.SPECIALIZATIONS)
        );
    
        // 🔥 yeh naya part hai
        dto.setValidCombos(
                new ArrayList<>(AcademicCatalog.getValidCombinationKeys())
        );
    
        return dto;
    }

    public AcademicProfileDto saveMyProfile(AcademicProfileDto dto) {
        User user = getCurrentUser();
    
        // 🔒 STEP 1 — Lock: agar profile already hai → change allowed nahi
        if (profileRepository.existsByUser(user)) {
            throw new IllegalStateException("Academic profile already set and cannot be changed.");
        }
    
        // 🔍 STEP 2 — Valid combination check
        boolean ok = AcademicCatalog.isValidCombination(
                dto.getEducationLevel(),
                dto.getMainStream(),
                dto.getSpecialization()
        );
    
        if (!ok) {
            throw new IllegalArgumentException(
                    "Invalid academic combination: " +
                            dto.getEducationLevel() + " / " +
                            dto.getMainStream() + " / " +
                            dto.getSpecialization()
            );
        }
    
        // 🆕 STEP 3 — Always create new profile (no update allowed)
        AcademicProfile profile = new AcademicProfile();
        profile.setUser(user);
        profile.setEducationLevel(dto.getEducationLevel());
        profile.setMainStream(dto.getMainStream());
        profile.setSpecialization(dto.getSpecialization());
    
        profile = profileRepository.save(profile);
        return toDto(profile);
    }

    private AcademicProfileDto toDto(AcademicProfile profile) {
        AcademicProfileDto dto = new AcademicProfileDto();
        dto.setId(profile.getId());
        dto.setEducationLevel(profile.getEducationLevel());
        dto.setMainStream(profile.getMainStream());
        dto.setSpecialization(profile.getSpecialization());
        return dto;
    }
}