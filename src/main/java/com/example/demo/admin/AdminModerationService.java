package com.example.demo.admin;

import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class AdminModerationService {

  private final UserRepository users;

  public AdminModerationService(UserRepository users) {
    this.users = users;
  }

  public void warnUser(Long userId, String reason) {
    User u = users.findById(userId).orElseThrow();
    // for now: just store reason (optional: add warnings counter later)
    u.setBlockReason(reason != null ? reason : "Warning issued");
    users.save(u);
  }

  public void blockUser(Long userId, int days, String reason) {
    User u = users.findById(userId).orElseThrow();
    u.setBlocked(true);
    u.setBlockedUntil(Instant.now().plusSeconds(days * 86400L));
    u.setBlockReason(reason != null ? reason : "Temporarily blocked");
    users.save(u);
  }

  public void banUser(Long userId, String reason) {
    User u = users.findById(userId).orElseThrow();
    u.setBlocked(true);
    u.setBlockedUntil(null); // permanent
    u.setBlockReason(reason != null ? reason : "Permanently banned");
    users.save(u);
  }

  public void unbanUser(Long userId) {
    User u = users.findById(userId).orElseThrow();
    u.setBlocked(false);
    u.setBlockedUntil(null);
    u.setBlockReason(null);
    users.save(u);
  }

  public void autoUnblockIfExpired(User u) {
    if (u.isBlocked() && u.getBlockedUntil() != null && u.getBlockedUntil().isBefore(Instant.now())) {
      u.setBlocked(false);
      u.setBlockedUntil(null);
      u.setBlockReason(null);
      users.save(u);
    }
  }
}