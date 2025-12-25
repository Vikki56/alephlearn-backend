package com.example.demo.repo;

import com.example.demo.domain.Quiz;
import com.example.demo.domain.QuizStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.user.User;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findByIsPublicTrueOrderByCreatedAtDesc();

    Optional<Quiz> findByJoinCode(String joinCode);
    long countByCreatedBy(User createdBy);

    List<Quiz> findByCreatedByIdOrderByCreatedAtDesc(Long userId);

    List<Quiz> findByIsRealtimeTrueAndStatus(QuizStatus status);
    List<Quiz> findByCreatedByOrderByCreatedAtDesc(User createdBy);
    List<Quiz> findByIsPublicTrueAndSpecializationIgnoreCaseOrderByCreatedAtDesc(String specialization);
}