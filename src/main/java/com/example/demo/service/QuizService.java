package com.example.demo.service;

import com.example.demo.domain.*;
import com.example.demo.domain.dto.quiz.CreateQuizRequest;
import com.example.demo.domain.dto.quiz.CreateQuizResponse;
import com.example.demo.domain.dto.quiz.LeaderboardEntryDto;
import com.example.demo.domain.dto.quiz.QuizSummaryResponse;
import com.example.demo.domain.dto.quiz.RealtimeParticipantDto;
import com.example.demo.domain.dto.quiz.RealtimeStatusResponse;
import com.example.demo.domain.dto.quiz.SubmitQuizAttemptRequest;
import com.example.demo.domain.dto.quiz.SubmitQuizAttemptResponse;
import com.example.demo.repo.QuizAttemptRepository;
import com.example.demo.repo.QuizRepository;
import com.example.demo.user.User;
import java.util.Objects;
import java.util.stream.Collectors;

import com.example.demo.repository.AcademicProfileRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.entity.QuizQuestion;
import com.example.demo.domain.dto.quiz.QuizQuestionDto;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.domain.dto.quiz.AttemptHistoryItem;


import java.util.HashMap;
import java.util.Map;



@Service
@Transactional
public class QuizService {

    public enum JoinMode {
        WAITING,
        PENDING,
        ENDED,
        BANNED
    }

    private final QuizRepository quizRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final AcademicProfileRepository academicProfileRepository;

    public QuizService(QuizRepository quizRepository,
                       QuizAttemptRepository quizAttemptRepository,
                       AcademicProfileRepository academicProfileRepository) {
        this.quizRepository = quizRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.academicProfileRepository = academicProfileRepository;   
    }

    public CreateQuizResponse createQuiz(CreateQuizRequest request, User currentUser, String baseUrl) {
        Quiz quiz = new Quiz();
        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setDifficulty(request.getDifficulty());
        quiz.setPublic(request.isPublic());
        quiz.setRealtime(request.isRealtime());
        quiz.setDurationSeconds(request.getDurationSeconds());
        quiz.setCreatedBy(currentUser);
    
        // ---- Academic Stream Attach ----
        AcademicProfile p = academicProfileRepository
        .findByUser(currentUser)
        .orElse(null);

if (p != null) {
    quiz.setEducationLevel(p.getEducationLevel());
    quiz.setMainStream(p.getMainStream());
    quiz.setSpecialization(p.getSpecialization());
}
    
        if (request.getQuestions() != null && !request.getQuestions().isEmpty()) {
            List<QuizQuestion> questionEntities = new java.util.ArrayList<>();
            int index = 1; 
    
            for (QuizQuestionDto dto : request.getQuestions()) {
                QuizQuestion q = new QuizQuestion();
                q.setQuiz(quiz);
                q.setType(dto.getType());
                q.setText(dto.getText());
                q.setOrdinalPosition(index++);
    
                // MCQ options
                if (dto.getOptions() != null) {
                    var opts = dto.getOptions();
                    if (opts.size() > 0) q.setOption1(opts.get(0));
                    if (opts.size() > 1) q.setOption2(opts.get(1));
                    if (opts.size() > 2) q.setOption3(opts.get(2));
                    if (opts.size() > 3) q.setOption4(opts.get(3));
                }
    
                q.setCorrectIndex(dto.getCorrectIndex());
                q.setCorrectBool(dto.getCorrectBool());
                q.setCodingAnswer(dto.getCodingAnswer());
    
                questionEntities.add(q);
            }
    
            quiz.setQuestions(questionEntities);
        }
    

        Quiz saved = quizRepository.save(quiz);
    
        String link = baseUrl + "/quizzes.html?code=" + saved.getJoinCode();
    
        return new CreateQuizResponse(
                saved.getId(),
                saved.getJoinCode(),
                link,
                saved.getDifficulty(),
                saved.isPublic(),
                saved.isRealtime(),
                saved.getStatus()
        );
    }
    @Transactional(readOnly = true)
    public List<QuizSummaryResponse> listPublicQuizzes(User currentUser) {
    
        if (currentUser != null) {

            AcademicProfile p = academicProfileRepository
                    .findByUser(currentUser)
                    .orElse(null);
        
            if (p != null && p.getSpecialization() != null && !p.getSpecialization().isBlank()) {
                String specialization = p.getSpecialization();
        
                return quizRepository
                        .findByIsPublicTrueAndSpecializationIgnoreCaseOrderByCreatedAtDesc(specialization)
                        .stream()
                        .map(q -> {
                            boolean isHost = q.getCreatedBy().getId().equals(currentUser.getId());
                            return new QuizSummaryResponse(
                                    q.getId(),
                                    q.getTitle(),
                                    q.getDescription(),
                                    q.getDifficulty(),
                                    q.isPublic(),
                                    q.isRealtime(),
                                    q.getStatus(),
                                    q.getTotalAttempts(),
                                    isHost
                            );
                        })
                        .toList();
            }
        }
    

        return quizRepository.findByIsPublicTrueOrderByCreatedAtDesc().stream()
                .map(q -> {
                    boolean isHost = currentUser != null &&
                            q.getCreatedBy().getId().equals(currentUser.getId());
                    return new QuizSummaryResponse(
                            q.getId(),
                            q.getTitle(),
                            q.getDescription(),
                            q.getDifficulty(),
                            q.isPublic(),
                            q.isRealtime(),
                            q.getStatus(),
                            q.getTotalAttempts(),
                            isHost
                    );
                })
                .toList();
    }
    @Transactional
    public void unbanUser(Long quizId, Long userId, User currentUser) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
    

        if (!quiz.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Only host can unban participants");
        }
    
        quizAttemptRepository
        .findByQuizIdAndUserIdAndRealtimeTrue(quizId, userId)
        .ifPresent(a -> {

            if (!a.isApproved()) {
                a.setApproved(true);
                a.setBanned(false);    
                if (a.getJoinedAt() == null) {
                    a.setJoinedAt(Instant.now());
                }
                quizAttemptRepository.save(a);

                quiz.setJoinedCount(quiz.getJoinedCount() + 1);
                quizRepository.save(quiz);
            }
        });
    }



    @Transactional(readOnly = true)
public List<AttemptHistoryItem> getAttemptHistory(User user) {

    List<QuizAttempt> attempts = quizAttemptRepository
            .findByUserOrderBySubmittedAtDesc(user);

    Map<Long, AttemptHistoryItem> map = new HashMap<>();

    for (QuizAttempt a : attempts) {
        Quiz q = a.getQuiz();
        Long quizId = q.getId();

        AttemptHistoryItem item = map.get(quizId);
        if (item == null) {
            item = new AttemptHistoryItem(
                    quizId,
                    q.getTitle(),
                    null,
                    null,
                    0,
                    null  
            );
        }

        item.setTotalAttempts(item.getTotalAttempts() + 1);


        if (a.getScore() != null) {
            if (item.getBestScore() == null || a.getScore() > item.getBestScore()) {
                item.setBestScore(a.getScore());
            }
        }

  
        if (a.getTimeTakenMillis() != null) {
            if (item.getFastestMs() == null || a.getTimeTakenMillis() < item.getFastestMs()) {
                item.setFastestMs(a.getTimeTakenMillis());
            }
        }

        map.put(quizId, item);
    }


    for (AttemptHistoryItem item : map.values()) {
        List<LeaderboardEntryDto> leaderboard =
                getLeaderboard(item.getQuizId()); 

        Integer rank = null;

        for (LeaderboardEntryDto e : leaderboard) {

            if (e.getUsername().equalsIgnoreCase(user.getName()) ||
                (user.getEmail() != null &&
                 e.getUsername().equalsIgnoreCase(user.getEmail().split("@")[0]))) {

                rank = e.getRank();
                break;
            }
        }

        item.setBestRank(rank);
    }

    return new ArrayList<>(map.values());
}

public List<RealtimeParticipantDto> getRealtimeParticipants(Long quizId, User currentUser) {
    Quiz quiz = quizRepository.findById(quizId)
            .orElseThrow(() -> new RuntimeException("Quiz not found"));


    if (currentUser == null) {
        return List.of();
    }


    Long hostId = quiz.getCreatedBy().getId();
    if (!hostId.equals(currentUser.getId())) {
        return List.of();   
    }


    List<QuizAttempt> attempts =
    quizAttemptRepository
        .findByQuizIdAndRealtimeTrueAndApprovedTrueAndBannedFalse(quizId);

    List<RealtimeParticipantDto> list = new ArrayList<>();

    for (QuizAttempt a : attempts) {
        if (a == null || a.getUser() == null) continue;

        User u = a.getUser();
        boolean removable = !u.getId().equals(hostId); 

        list.add(new RealtimeParticipantDto(
                u.getId(),
                u.getName(),
                u.getEmail(),
                removable
        ));
    }

    return list;
}


public List<RealtimeParticipantDto> getPendingParticipants(Long quizId, User currentUser) {
    Quiz quiz = quizRepository.findById(quizId)
            .orElseThrow(() -> new RuntimeException("Quiz not found"));

    if (!quiz.getCreatedBy().getId().equals(currentUser.getId()))
        return List.of();

    return quizAttemptRepository
            .findByQuizIdAndRealtimeTrueAndApprovedFalseAndBannedFalse(quizId)
            .stream()
            .map(a -> new RealtimeParticipantDto(
                    a.getUser().getId(),
                    a.getUser().getName(),
                    a.getUser().getEmail(),
                    true
            )).toList();
}

public void approveParticipant(Long quizId, Long userId, User currentUser) {
    Quiz quiz = quizRepository.findById(quizId)
            .orElseThrow(() -> new RuntimeException("Quiz not found"));

    if (!quiz.getCreatedBy().getId().equals(currentUser.getId()))
        throw new RuntimeException("Not host");

    quizAttemptRepository
            .findByQuizIdAndUserIdAndRealtimeTrue(quizId, userId)
            .ifPresent(a -> {

                if (!a.isApproved()) {
                    a.setApproved(true);
                    a.setBanned(false);    
                    if (a.getJoinedAt() == null) {
                        a.setJoinedAt(Instant.now());
                    }
                    quizAttemptRepository.save(a);

                    quiz.setJoinedCount(quiz.getJoinedCount() + 1);
                    quizRepository.save(quiz);
                }
            });
}

public enum JoinState {
    NONE, PENDING, APPROVED, BANNED
}

public JoinState getJoinState(Long quizId, User user) {
    Quiz quiz = quizRepository.findById(quizId)
            .orElseThrow(() -> new RuntimeException("Quiz not found"));

    QuizAttempt a = quizAttemptRepository
            .findByQuizAndUserAndRealtime(quiz, user, true)
            .orElse(null);

    if (a == null) return JoinState.NONE;
    if (a.isBanned()) return JoinState.BANNED;
    if (!a.isApproved()) return JoinState.PENDING;
    return JoinState.APPROVED;
}


public void removeRealtimeParticipant(Long quizId, Long participantUserId, User currentUser) {
    Quiz quiz = quizRepository.findById(quizId)
            .orElseThrow(() -> new RuntimeException("Quiz not found"));

    Long hostId = quiz.getCreatedBy().getId();
    if (!hostId.equals(currentUser.getId())) {

        return;
    }


    if (hostId.equals(participantUserId)) return;


    User dummy = new User();
    dummy.setId(participantUserId);

    quizAttemptRepository
            .findByQuizAndUserAndRealtime(quiz, dummy, true)
            .ifPresent(attempt -> {
                boolean wasApproved = attempt.isApproved();


                attempt.setBanned(true);
                attempt.setApproved(false);
                quizAttemptRepository.save(attempt);


                if (wasApproved) {
                    quiz.setJoinedCount(Math.max(0, quiz.getJoinedCount() - 1));
                    quizRepository.save(quiz);
                }
            });
}


    @Transactional
    public SubmitQuizAttemptResponse submitAttempt(Long quizId, User user, SubmitQuizAttemptRequest req) {
    
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
    
        List<QuizQuestion> questions = quiz.getQuestions();
        int totalQuestions = (questions != null) ? questions.size() : 0;
    
        int correctCount = 0;
        int wrongCount = 0;
        int skippedCount = 0;
    

        for (int i = 0; i < totalQuestions; i++) {
            QuizQuestion q = questions.get(i);
    
            Integer selected = (req.getSelectedOptions() != null
                    && req.getSelectedOptions().size() > i)
                    ? req.getSelectedOptions().get(i)
                    : null;
    
            if (selected == null) {
                skippedCount++;
            } else if (Objects.equals(selected, q.getCorrectIndex())) {
                correctCount++;
            } else {
                wrongCount++;
            }
        }
    
        int score = correctCount;
    
        QuizAttempt attempt = quizAttemptRepository
                .findByQuizAndUserAndRealtime(quiz, user, req.isRealtime())
                .orElseGet(() -> {
                    QuizAttempt a = new QuizAttempt();
                    a.setQuiz(quiz);
                    a.setUser(user);
                    a.setRealtime(req.isRealtime());
                    a.setJoinedAt(Instant.now());
                    return a;
                });
    
        attempt.setScore(score);
        attempt.setCompleted(true);
        attempt.setTimeTakenMillis(req.getTimeTakenMillis());
        attempt.setSubmittedAt(Instant.now());
    
        attempt.setTotalQuestions(totalQuestions);
        attempt.setCorrectCount(correctCount);
        attempt.setWrongCount(wrongCount);
        attempt.setSkippedCount(skippedCount);
    
        quizAttemptRepository.save(attempt);
    
        if (!quiz.isRealtime()) {
            quiz.setTotalAttempts(quiz.getTotalAttempts() + 1);
        }
        quizRepository.save(quiz);
    
        return new SubmitQuizAttemptResponse(
                score,
                totalQuestions,
                req.getTimeTakenMillis()
        );
    }

    @Transactional(readOnly = true)
    public List<LeaderboardEntryDto> getLeaderboard(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
    
        if (quiz.getStatus() != QuizStatus.ENDED) {
            return List.of();
        }
    
        List<QuizAttempt> attempts =
                quizAttemptRepository.findTop20ByQuizOrderByScoreDescTimeTakenMillisAscSubmittedAtAsc(quiz);
    
        List<LeaderboardEntryDto> result = new ArrayList<>();
        int rank = 1;
    
        for (QuizAttempt a : attempts) {
    
            if (a.getScore() == null) {
                continue;
            }
    
            int safeScore = a.getScore();
            long safeTime = (a.getTimeTakenMillis() != null)
                    ? a.getTimeTakenMillis()
                    : 0L;
    
            String userName = "Unknown";
            if (a.getUser() != null) {
                if (a.getUser().getName() != null && !a.getUser().getName().isEmpty()) {
                    userName = a.getUser().getName();
                } else if (a.getUser().getEmail() != null) {
                    userName = a.getUser().getEmail().split("@")[0];
                }
            }
    
            result.add(new LeaderboardEntryDto(
                    userName,
                    rank++,
                    safeScore,
                    safeTime
            ));
        }
        return result;
    }
    @Transactional(readOnly = true)
    public List<QuizSummaryResponse> listMyQuizzes(User currentUser) {
        if (currentUser == null) {
            throw new RuntimeException("Not logged in");
        }
    
        return quizRepository.findByCreatedByOrderByCreatedAtDesc(currentUser).stream()
                .filter(q -> q.getStatus() != QuizStatus.ENDED)
                .map(q -> {
                    boolean isHost = true;
                    return new QuizSummaryResponse(
                            q.getId(),
                            q.getTitle(),
                            q.getDescription(),
                            q.getDifficulty(),
                            q.isPublic(),
                            q.isRealtime(),
                            q.getStatus(),
                            q.getTotalAttempts(),
                            isHost
                    );
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public Quiz getByJoinCode(String joinCode) {
        return quizRepository.findByJoinCode(joinCode)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
    }
    @Transactional(readOnly = true)
public Quiz getById(Long id) {
    return quizRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Quiz not found"));
}

    // ------- realtime join ---------

    public JoinMode joinRealtimeQuiz(Long quizId, User user) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
    
        if (!quiz.isRealtime()) {
            throw new RuntimeException("Not a realtime quiz");
        }
    
        if (quiz.getStatus() == QuizStatus.ENDED || quiz.getStatus() == QuizStatus.COMPLETED) {
            return JoinMode.ENDED;
        }
    
        QuizAttempt existing = quizAttemptRepository
                .findByQuizAndUserAndRealtime(quiz, user, true)
                .orElse(null);
    
        if (existing != null && existing.isBanned()) {
            return JoinMode.BANNED;
        }
    
        if (existing == null) {
            existing = new QuizAttempt();
            existing.setQuiz(quiz);
            existing.setUser(user);
            existing.setRealtime(true);
            existing.setBanned(false);
            existing.setApproved(false);
            existing.setJoinedAt(null);
            quizAttemptRepository.save(existing);
        }
    
        boolean waiting = (quiz.getStatus() == QuizStatus.WAITING);
    
        // ---------- WAITING STATE ----------
        if (waiting) {
            if (!existing.isApproved()) {
                existing.setApproved(true);
                if (existing.getJoinedAt() == null) {
                    existing.setJoinedAt(Instant.now());
                }
    
                quiz.setJoinedCount(quiz.getJoinedCount() + 1);
                quizRepository.save(quiz);
                quizAttemptRepository.save(existing);
            }
    
            return JoinMode.WAITING;
        }
    
        // ---------- LIVE STATE ----------
        if (quiz.getStatus() == QuizStatus.LIVE) {
            if (existing.isApproved()) {
                return JoinMode.WAITING; 
            }
    

            return JoinMode.PENDING;
        }

        return JoinMode.PENDING;
    }

    public void leaveRealtimeQuiz(Long quizId, User user) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
    
        if (!quiz.isRealtime()) {
            throw new RuntimeException("Not a realtime quiz");
        }
    

        QuizAttempt existing = quizAttemptRepository
                .findByQuizAndUserAndRealtime(quiz, user, true)
                .orElse(null);
    
                if (existing != null) {
                    boolean wasApproved = existing.isApproved();
                
                    quizAttemptRepository.delete(existing);
                
                    if (wasApproved) {   
                        long current = quiz.getJoinedCount();
                        if (current > 0) {
                            quiz.setJoinedCount(current - 1);
                        }
                        quizRepository.save(quiz);
                    }
                }
  
    }



    public void startRealtimeQuiz(Long quizId, User currentUser) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        if (!quiz.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Only quiz creator can start");
        }

        if (!quiz.isRealtime()) {
            throw new RuntimeException("Not a realtime quiz");
        }

        quiz.setStatus(QuizStatus.LIVE);
        quiz.setStartTime(Instant.now());

        if (quiz.getDurationSeconds() != null) {
            quiz.setEndTime(quiz.getStartTime().plusSeconds(quiz.getDurationSeconds()));
        }

        quizRepository.save(quiz);
    }

    @Transactional(readOnly = true)
    public RealtimeStatusResponse getRealtimeStatus(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
    
        Instant now = Instant.now();
    
        if (quiz.isRealtime()
                && quiz.getStatus() == QuizStatus.WAITING
                && quiz.getCreatedAt() != null) {
    
            Instant expireAt = quiz.getCreatedAt().plus(Duration.ofMinutes(10));
            if (now.isAfter(expireAt)) {
                quiz.setStatus(QuizStatus.COMPLETED);
                quiz.setEndTime(now);
                quizRepository.save(quiz);
            }
        }
    
        if (quiz.getStatus() == QuizStatus.LIVE && quiz.getEndTime() != null) {
            if (now.isAfter(quiz.getEndTime())) {
                quiz.setStatus(QuizStatus.COMPLETED);
                quizRepository.save(quiz);
            }
        }
    
        Long remaining = null;
        if (quiz.getStatus() == QuizStatus.LIVE && quiz.getEndTime() != null) {
            if (now.isBefore(quiz.getEndTime())) {
                remaining = Duration.between(now, quiz.getEndTime()).getSeconds();
            } else {
                remaining = 0L;
            }
        }
    
        return new RealtimeStatusResponse(
                quiz.getStatus(),
                quiz.getJoinedCount(),
                remaining
        );
    }


    public void submitQuiz(Long quizId, User user, boolean isRealtime, Integer score) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
    
        QuizAttempt attempt = quizAttemptRepository
                .findByQuizAndUserAndRealtime(quiz, user, isRealtime)
                .orElseGet(() -> {
                    QuizAttempt a = new QuizAttempt();
                    a.setQuiz(quiz);
                    a.setUser(user);
                    a.setRealtime(isRealtime);
                    a.setJoinedAt(Instant.now());  
                    return a;
                });
    
        Instant now = Instant.now();
    
        attempt.setScore(score);
        attempt.setSubmittedAt(now);
        attempt.setCompleted(true);
    
        if (attempt.getJoinedAt() != null) {
            long ms = java.time.Duration.between(attempt.getJoinedAt(), now).toMillis();
            attempt.setTimeTakenMillis(ms);
        }
    
        if (quiz.getQuestions() != null) {
            attempt.setTotalQuestions(quiz.getQuestions().size());
        }
    
        quizAttemptRepository.save(attempt);
    
        if (!quiz.isRealtime()) {
            quiz.setTotalAttempts(quiz.getTotalAttempts() + 1);
        }
    
        quizRepository.save(quiz);
    }

    @Transactional
    public void endQuiz(Long quizId, User currentUser) {
    
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
    
        if (quiz.getCreatedBy() != null &&
            !Objects.equals(quiz.getCreatedBy().getId(), currentUser.getId())) {
            throw new RuntimeException("Only quiz creator can end the quiz");
        }
    
        quiz.setStatus(QuizStatus.ENDED);
        quiz.setEndTime(Instant.now());
    
        quiz.setPublic(false);
    
        if (quiz.getDurationSeconds() == null) {
            quiz.setDurationSeconds(0);
        }
    
        quizRepository.save(quiz);
    }

}