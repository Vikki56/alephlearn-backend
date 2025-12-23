package com.example.demo.moderation;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserReportRepository extends JpaRepository<UserReport, Long> {
    List<UserReport> findByStatusOrderByCreatedAtDesc(UserReport.Status status);
    List<UserReport> findAllByOrderByCreatedAtDesc();
}