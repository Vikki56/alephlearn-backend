package com.example.demo.repo;

import com.example.demo.entity.Doubt;
import com.example.demo.entity.DoubtAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface DoubtAnswerRepository extends JpaRepository<DoubtAnswer, Long> {

    List<DoubtAnswer> findByDoubtOrderByCreatedAtAsc(Doubt doubt);
    long countBySolverId(Long userId);

    long countBySolverIdAndAcceptedTrue(Long userId);
    

    long countBySolverIdAndCreatedAtBetween(
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    );
}