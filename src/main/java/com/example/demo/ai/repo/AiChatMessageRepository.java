package com.example.demo.ai.repo;

import com.example.demo.ai.entity.AiChatMessage;
import com.example.demo.ai.entity.AiExplanation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiChatMessageRepository extends JpaRepository<AiChatMessage, Long> {
    List<AiChatMessage> findByExplanationOrderByCreatedAtAsc(AiExplanation explanation);
}
