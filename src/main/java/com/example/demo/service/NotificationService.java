package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.user.User;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(NotificationRepository notificationRepository,
                               SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public Notification notify(User receiver,
                               NotificationType type,
                               String message,
                               Long doubtId,
                               Long answerId) {

        Notification n = new Notification();
        n.setReceiver(receiver);
        n.setType(type);
        n.setMessage(message);
        n.setDoubtId(doubtId);
        n.setAnswerId(answerId);

        Notification saved = notificationRepository.save(n);

        // send to /user/{receiverId}/queue/notifications
// For user-specific topic
String destination = "/topic/notifications.user-" + receiver.getId();

java.util.Map<String, Object> payload = new java.util.LinkedHashMap<>();
payload.put("id", saved.getId());
payload.put("type", saved.getType() != null ? saved.getType().name() : null);
payload.put("message", saved.getMessage());
payload.put("doubtId", saved.getDoubtId());
payload.put("answerId", saved.getAnswerId());
payload.put("createdAt", saved.getCreatedAt() != null ? saved.getCreatedAt().toEpochMilli() : null);

messagingTemplate.convertAndSend(destination, payload);

        return saved;
    }
}