package com.example.demo.repo;

import com.example.demo.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    // ✅ Get all questions (latest first)
    List<Question> findAllByOrderByCreatedAtDesc();

    List<Question> findByGroupNameOrderByCreatedAtDesc(String groupName);
    
        // Questions asked by a specific user (for private room list)
        List<Question> findByAskedByOrderByCreatedAtDesc(String askedBy);

}