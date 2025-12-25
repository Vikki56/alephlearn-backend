package com.example.demo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {


    @Query("""
           SELECT m FROM Message m
           WHERE m.room = :room
             AND m.deleted = false
           ORDER BY m.ts DESC
           """)
    List<Message> findRecentByRoom(@Param("room") String room, Pageable pageable);

    @Query("""
           SELECT m FROM Message m
           WHERE m.room = :room
             AND m.pinned = true
             AND m.deleted = false
           ORDER BY m.ts DESC
           """)
    List<Message> findPinnedByRoom(@Param("room") String room, Pageable pageable);
}
