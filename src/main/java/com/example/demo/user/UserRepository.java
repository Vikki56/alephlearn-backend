package com.example.demo.user;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    // ⭐ ADD THIS — Ye error fix karega
    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByResetToken(String resetToken);
    List<User> findAllByRole(Role role);

    // blocked users (temporary block): blocked=true AND blockedUntil > now
    List<User> findAllByBlockedIsTrueAndBlockedUntilIsNotNullAndBlockedUntilAfter(Instant now);
    
    // banned users (permanent ban): blocked=true AND blockedUntil IS NULL
    List<User> findAllByBlockedIsTrueAndBlockedUntilIsNull();
}