package com.example.demo.repo;

import com.example.demo.domain.QuizAttempt;
import com.example.demo.domain.Quiz;
import com.example.demo.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    Optional<QuizAttempt> findTopByUserAndCompletedTrueOrderBySubmittedAtDesc(User user);

    List<QuizAttempt> findByUserAndCompletedTrueOrderBySubmittedAtDesc(User user);

    Optional<QuizAttempt> findByQuizAndUserAndRealtime(Quiz quiz, User user, boolean realtime);

    Long countByQuiz(Quiz quiz);

    List<QuizAttempt> findByUser(User user);
    long countByUser(User user);

    List<QuizAttempt> findTop20ByQuizOrderByScoreDescTimeTakenMillisAscSubmittedAtAsc(Quiz quiz);

    Optional<QuizAttempt> findTopByUserOrderBySubmittedAtDesc(User user);

    List<QuizAttempt> findByUserOrderBySubmittedAtDesc(User user);

    List<QuizAttempt> findByQuizIdAndRealtimeTrue(Long quizId);

    Optional<QuizAttempt> findByQuizIdAndUserIdAndRealtimeTrue(Long quizId, Long userId);

    Optional<QuizAttempt> findByQuizIdAndUserId(Long quizId, Long userId);

    Optional<QuizAttempt> findByQuizAndUser(Quiz quiz, User user);

    List<QuizAttempt> findByQuizIdAndBannedTrue(Long quizId);

    List<QuizAttempt> findByQuizIdAndRealtimeTrueAndApprovedTrueAndBannedFalse(Long quizId);

List<QuizAttempt> findByQuizIdAndRealtimeTrueAndApprovedFalseAndBannedFalse(Long quizId);
List<QuizAttempt> findByQuiz(Quiz quiz);
}