package com.example.demo.ai.service;

import com.example.demo.domain.AcademicProfile;
import com.example.demo.repository.AcademicProfileRepository;
import com.example.demo.security.AuthUser;
import com.example.demo.user.User;
import org.springframework.stereotype.Service;

@Service
public class UserStreamService {

    private final AcademicProfileRepository academicProfileRepository;

    public UserStreamService(AcademicProfileRepository academicProfileRepository) {
        this.academicProfileRepository = academicProfileRepository;
    }

    public StreamKey currentStream() {
        User u = AuthUser.current();
        if (u == null) throw new RuntimeException("Unauthenticated");

        AcademicProfile p = academicProfileRepository.findByUser(u)
                .orElseThrow(() -> new RuntimeException("Academic profile missing"));

        return new StreamKey(
                safe(p.getEducationLevel()),
                safe(p.getMainStream()),
                safe(p.getSpecialization())
        );
    }

    private String safe(String s){ return s == null ? "" : s.trim(); }

    public record StreamKey(String educationLevel, String mainStream, String specialization) {}
}