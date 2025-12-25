package com.example.demo.repo;

import com.example.demo.domain.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findByQuestionIdOrderByCreatedAtAsc(Long questionId);

    void deleteByQuestionId(Long questionId);

    boolean existsByIdAndAuthor(Long id, String author);
}