package com.example.demo.repo;

import com.example.demo.domain.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    // ✅ Get all answers for a question (oldest first)
    List<Answer> findByQuestionIdOrderByCreatedAtAsc(Long questionId);

    // ✅ Delete all answers belonging to a question (for cascading deletion)
    void deleteByQuestionId(Long questionId);

    // ✅ Check if an answer belongs to a specific author (for delete security)
    boolean existsByIdAndAuthor(Long id, String author);
}