package com.example.demo.ai.repo;

import com.example.demo.ai.entity.AiExplanation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AiExplanationRepository extends JpaRepository<AiExplanation, Long> {

    boolean existsByUserIdAndDoubtId(Long userId, Long doubtId);

    Optional<AiExplanation> findTopByUserIdAndDoubtIdOrderByCreatedAtDesc(Long userId, Long doubtId);

    long countByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
}
