package com.example.demo.repo;

import com.example.demo.entity.RoomKick;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomKickRepository extends JpaRepository<RoomKick, Long> {
    boolean existsByQuestionIdAndKickedUserEmailIgnoreCase(Long questionId, String kickedUserEmail);
}