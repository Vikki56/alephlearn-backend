package com.example.demo.auth.dto;

import com.example.demo.user.User;
import java.time.Instant;

public record UserResponse(
        Long id,
        String name,
        String email,
        String role,
        Instant createdAt
) {
    public static UserResponse from(User u) {
        return new UserResponse(
                u.getId(),
                u.getName(),
                u.getEmail(),
                u.getRole().name(),
                u.getCreatedAt()
        );
    }
}