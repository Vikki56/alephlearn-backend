package com.example.demo;

import com.example.demo.entity.Notification;
import com.example.demo.entity.NotificationRepository;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationController(NotificationRepository notificationRepository,
                                  UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/unread")
    public List<Notification> unread(@RequestParam Long userId) {
      User u = userRepository.findById(userId)
              .orElseThrow(() -> new RuntimeException("User not found"));
      return notificationRepository
              .findTop20ByReceiverAndReadOrderByCreatedAtDesc(u, false);
    }

    @PostMapping("/{id}/read")
    public void markRead(@PathVariable Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }
}