package com.example.demo.repo;
import com.example.demo.domain.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.time.Instant;
import java.util.Optional;
public interface ClaimRepository extends JpaRepository<Claim, Long> {



  Optional<Claim> findFirstByQuestionIdAndUserId(Long questionId, String userId);
    // ✅ Count how many active (non-expired) claims exist for a question
    @Query("""
           SELECT COUNT(c) FROM Claim c
           WHERE c.questionId = :qid
             AND (c.expiresAt IS NULL OR c.expiresAt > :now)
           """)
    long countActiveByQuestionId(@Param("qid") Long questionId, @Param("now") Instant now);

    // ✅ Check if a user has an active claim for this question
    @Query("""
           SELECT CASE WHEN COUNT(c)>0 THEN TRUE ELSE FALSE END
           FROM Claim c
           WHERE c.questionId = :qid
             AND c.userId = :uid
             AND (c.expiresAt IS NULL OR c.expiresAt > :now)
           """)
    boolean isActiveClaimer(@Param("qid") Long qid, @Param("uid") String userId, @Param("now") Instant now);

    @Query("""
      SELECT DISTINCT c.questionId FROM Claim c
      WHERE lower(c.userId) = lower(:uid)
        AND (c.expiresAt IS NULL OR c.expiresAt > :now)
      """)
List<Long> findActiveQuestionIdsByUser(@Param("uid") String userId,
                                      @Param("now") Instant now);

    // ✅ Used in QuestionController for claim checks
    boolean existsByQuestionIdAndUserId(Long questionId, String userId);

    long countByQuestionId(Long questionId);

    // ✅ New: delete all claims when a question is deleted
    void deleteByQuestionId(Long questionId);
    long countByUserId(String userId);
}