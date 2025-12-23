package com.example.demo.entity;

import com.example.demo.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findTop20ByReceiverAndReadOrderByCreatedAtDesc(User receiver, boolean read);
}