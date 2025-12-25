package com.example.demo.repo;
import org.springframework.data.domain.Page;
import com.example.demo.entity.Doubt;
import com.example.demo.entity.DoubtStatus;
import com.example.demo.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface DoubtRepository extends JpaRepository<Doubt, Long> {

    // ====== Existing methods (keep them) ======
    Page<Doubt> findByAsker(User asker, Pageable pageable);
    Page<Doubt> findByStatus(DoubtStatus status, Pageable pageable);

    Page<Doubt> findBySubjectIgnoreCase(String subject, Pageable pageable);

    Page<Doubt> findBySubjectIgnoreCaseAndStatus(
            String subject,
            DoubtStatus status,
            Pageable pageable
    );

    Page<Doubt> findByAskerId(Long askerId, Pageable pageable);

    long countByAskerId(Long userId);

    long countByAskerIdAndCreatedAtBetween(
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    );

    // ============================================================
    //              🔥 STREAM-AWARE FILTERING METHODS 🔥
    // ============================================================

    Page<Doubt> findByEducationLevelIgnoreCaseAndMainStreamIgnoreCaseAndSpecializationIgnoreCase(
            String educationLevel,
            String mainStream,
            String specialization,
            Pageable pageable
    );

    Page<Doubt> findByEducationLevelIgnoreCaseAndMainStreamIgnoreCaseAndSpecializationIgnoreCaseAndStatus(
            String educationLevel,
            String mainStream,
            String specialization,
            DoubtStatus status,
            Pageable pageable
    );

    Page<Doubt> findByEducationLevelIgnoreCaseAndMainStreamIgnoreCaseAndSpecializationIgnoreCaseAndSubjectIgnoreCase(
            String educationLevel,
            String mainStream,
            String specialization,
            String subject,
            Pageable pageable
    );

    Page<Doubt> findByEducationLevelIgnoreCaseAndMainStreamIgnoreCaseAndSpecializationIgnoreCaseAndSubjectIgnoreCaseAndStatus(
            String educationLevel,
            String mainStream,
            String specialization,
            String subject,
            DoubtStatus status,
            Pageable pageable
    );

Page<Doubt> findByAskerIdAndStatusAndAcceptedAnswerIsNotNull(
        Long askerId,
        DoubtStatus status,
        Pageable pageable
);

Page<Doubt> findByStatusAndAcceptedAnswerIsNotNull(DoubtStatus status, Pageable pageable);


Page<Doubt> findByEducationLevelIgnoreCaseAndMainStreamIgnoreCaseAndSpecializationIgnoreCaseAndStatusAndAcceptedAnswerIsNotNull(
        String educationLevel,
        String mainStream,
        String specialization,
        DoubtStatus status,
        Pageable pageable
);



}