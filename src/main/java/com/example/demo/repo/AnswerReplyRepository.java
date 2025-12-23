package com.example.demo.repo;

import com.example.demo.entity.AnswerReply;
import com.example.demo.entity.DoubtAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerReplyRepository extends JpaRepository<AnswerReply, Long> {
    List<AnswerReply> findByAnswerOrderByCreatedAtAsc(DoubtAnswer answer);
}