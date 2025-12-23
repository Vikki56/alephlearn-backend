package com.example.demo.admin;

import com.example.demo.admin.dto.CreateTeacherRequest;
import com.example.demo.user.Role;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminUserService {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    public AdminUserService(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @Transactional
    public User createTeacher(CreateTeacherRequest req) {
        if (req == null) throw new IllegalArgumentException("Body required");
        if (req.name() == null || req.name().isBlank()) throw new IllegalArgumentException("Name required");
        if (req.email() == null || req.email().isBlank()) throw new IllegalArgumentException("Email required");
        if (req.password() == null || req.password().length() < 6) throw new IllegalArgumentException("Password min 6 chars");

        String email = req.email().trim().toLowerCase();

        if (users.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        User u = new User();
        u.setName(req.name().trim());
        u.setEmail(email);
        u.setPasswordHash(encoder.encode(req.password()));
        u.setRole(Role.TEACHER);

        return users.save(u);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listTeachers() {
        return users.findAll().stream()
                .filter(u -> u.getRole() == Role.TEACHER)
                .map(this::toSafe)
                .collect(Collectors.toList());
    }

    // ✅ NEW: blocked users (temporary block)
    @Transactional(readOnly = true)
    public List<Map<String, Object>> blockedUsers() {
        Instant now = Instant.now();
        return users.findAll().stream()
                .filter(User::isBlocked)
                .filter(u -> u.getBlockedUntil() != null && u.getBlockedUntil().isAfter(now))
                .map(this::toSafe)
                .collect(Collectors.toList());
    }

    // ✅ NEW: banned users (permanent ban => blocked=true AND blockedUntil=null)
    @Transactional(readOnly = true)
    public List<Map<String, Object>> bannedUsers() {
        return users.findAll().stream()
                .filter(User::isBlocked)
                .filter(u -> u.getBlockedUntil() == null)
                .map(this::toSafe)
                .collect(Collectors.toList());
    }

    // ✅ NEW: unban / unblock
    @Transactional
    public void unban(Long id) {
        User u = users.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        u.setBlocked(false);
        u.setBlockedUntil(null);
        u.setBlockReason(null);
        users.save(u);
    }

    private Map<String, Object> toSafe(User u) {
        Map<String, Object> m = new java.util.LinkedHashMap<>();
        m.put("id", u.getId());
        m.put("name", u.getName());
        m.put("email", u.getEmail());
        m.put("role", (u.getRole() == null ? "USER" : u.getRole().name()));
        m.put("blocked", u.isBlocked());
        m.put("blockedUntil", u.getBlockedUntil()); // can be null ✅
        m.put("blockReason", u.getBlockReason());   // can be null ✅
        return m;
    }
    
}