package com.example.demo.api;

import com.example.demo.domain.Quiz;
import com.example.demo.domain.QuizAttempt;
import com.example.demo.domain.dto.quiz.CreateQuizRequest;
import com.example.demo.domain.dto.quiz.CreateQuizResponse;
import com.example.demo.domain.dto.quiz.LeaderboardEntryDto;
import com.example.demo.domain.dto.quiz.QuizDetailResponse;
import com.example.demo.domain.dto.quiz.QuizQuestionDto;
import com.example.demo.domain.dto.quiz.QuizSummaryResponse;
import com.example.demo.domain.dto.quiz.RealtimeParticipantDto;
import com.example.demo.domain.dto.quiz.RealtimeStatusResponse;
import com.example.demo.domain.dto.quiz.SubmitQuizAttemptRequest;
import com.example.demo.domain.dto.quiz.SubmitQuizAttemptResponse;
import com.example.demo.domain.dto.quiz.LatestAttemptResponse;
import com.example.demo.domain.dto.quiz.AttemptHistoryItemDto;
import com.example.demo.domain.entity.QuizQuestion;
import com.example.demo.repo.QuizAttemptRepository;
import com.example.demo.service.QuizService;
import com.example.demo.user.User;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.example.demo.domain.dto.quiz.QuizAttemptDto;
import java.time.Instant;
import java.util.ArrayList;
import com.example.demo.domain.dto.quiz.AttemptHistoryItem;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.List;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;
    private final QuizAttemptRepository quizAttemptRepository;

    private static final String FRONTEND_BASE_URL = "http://127.0.0.1:5500";

    public QuizController(
            QuizService quizService,
            QuizAttemptRepository quizAttemptRepository
    ) {
        this.quizService = quizService;
        this.quizAttemptRepository = quizAttemptRepository;
    }




    // 1) create quiz
    @PostMapping
    public ResponseEntity<CreateQuizResponse> createQuiz(
            @RequestBody CreateQuizRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal User currentUser
    ) {
        CreateQuizResponse response =
                quizService.createQuiz(request, currentUser, FRONTEND_BASE_URL);
        return ResponseEntity.ok(response);
    }

    // ---------- NEW: latest attempt + history (for leaderboard.html) ----------
// Latest attempt
@Transactional(readOnly = true)
@GetMapping("/attempts/latest")
public ResponseEntity<?> getLatestAttempt(
        @Parameter(hidden = true) @AuthenticationPrincipal User user
) {
    if (user == null) {
        return ResponseEntity.status(401).body("User not logged in");
    }

    QuizAttempt latest = quizAttemptRepository
            .findTopByUserAndCompletedTrueOrderBySubmittedAtDesc(user)
            .orElse(null);

    QuizAttemptDto dto = toDto(latest);
    return ResponseEntity.ok(dto);
}


// 🔥 USER KICK CHECK ENDPOINT
@Transactional(readOnly = true)
@GetMapping("/{quizId}/attempt/me")
public ResponseEntity<Void> hasMyRealtimeAttempt(
        @PathVariable Long quizId,
        @AuthenticationPrincipal User user
) {
    if (user == null) {
        return ResponseEntity.status(401).build();
    }

    Quiz quiz = quizService.getById(quizId);

    boolean exists = quizAttemptRepository
            .findByQuizAndUserAndRealtime(quiz, user, true)
            .filter(a -> !a.isBanned())   // 👈 banned ko ignore karo
            .isPresent();

    if (exists) {
        // still inside room
        return ResponseEntity.ok().build();
    } else {
        // ya to attempt hi nahi, ya banned → front-end ko "removed / banned" dikhana chahiye
        return ResponseEntity.notFound().build();
    }
}


    @GetMapping("/{quizId}/realtime/participants")
    public List<RealtimeParticipantDto> getRealtimeParticipants(
            @PathVariable Long quizId,
            @AuthenticationPrincipal User currentUser   // 👈 yahan pe tum jo bhi principal type use kar rahe ho, woh likhna
    ) {
        return quizService.getRealtimeParticipants(quizId, currentUser);
    }


    @DeleteMapping("/{quizId}/realtime/participants/{userId}")
    public ResponseEntity<Void> removeRealtimeParticipant(
            @PathVariable Long quizId,
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser
    ) {
        quizService.removeRealtimeParticipant(quizId, userId, currentUser);
        return ResponseEntity.noContent().build();
    }

@Transactional(readOnly = true)
@GetMapping("/attempts/history")
public ResponseEntity<List<AttemptHistoryItem>> getAttemptHistory(
        @Parameter(hidden = true) @AuthenticationPrincipal User user
) {
    if (user == null) {
        return ResponseEntity.status(401).build();
    }

    List<AttemptHistoryItem> list = quizService.getAttemptHistory(user);
    return ResponseEntity.ok(list);
}

    private String formatInstant(Instant instant) {
        return (instant == null) ? null : instant.toString();
    }

    // 2) list all public quizzes (Quizzes page)
    @GetMapping("/public")
    public ResponseEntity<List<QuizSummaryResponse>> getPublicQuizzes(
            @Parameter(hidden = true) @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(quizService.listPublicQuizzes(currentUser));
    }

    // 3) list my quizzes (host tab)
    @GetMapping("/mine")
    public ResponseEntity<List<QuizSummaryResponse>> getMyQuizzes(
            @Parameter(hidden = true) @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(quizService.listMyQuizzes(currentUser));
    }

    @PostMapping("/{quizId}/unban/{userId}")
    public ResponseEntity<Void> unbanUser(
            @PathVariable Long quizId,
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser
    ) {
        quizService.unbanUser(quizId, userId, currentUser);
        return ResponseEntity.ok().build();
    }
    

    @Transactional(readOnly = true)
    @GetMapping("/{quizId}/banned")
public List<RealtimeParticipantDto> bannedUsers(@PathVariable Long quizId) {
    return quizAttemptRepository.findByQuizIdAndBannedTrue(quizId)
            .stream()
            .map(a -> new RealtimeParticipantDto(
                    a.getUser().getId(),
                    a.getUser().getName(),
                    a.getUser().getEmail(),
                    false // removable not needed for banned list
            )).toList();
}
    

    // 4) get quiz by joinCode (public/private link open)
    @Transactional(readOnly = true)
    @GetMapping("/code/{joinCode}")
    public QuizDetailResponse getQuizDetailByJoinCode(@PathVariable String joinCode) {
        Quiz quiz = quizService.getByJoinCode(joinCode);
        return buildQuizDetailResponse(quiz);
    }

    // 5) get quiz by id (attempt page use karega)
    @Transactional(readOnly = true)
    @GetMapping("/{quizId}")
    public QuizDetailResponse getQuizDetailById(@PathVariable Long quizId) {
        Quiz quiz = quizService.getById(quizId);
        return buildQuizDetailResponse(quiz);
    }

    // ----- attempts + leaderboard -----

    @PostMapping("/{quizId}/attempt")
    public ResponseEntity<SubmitQuizAttemptResponse> submitAttempt(
            @PathVariable Long quizId,
            @RequestBody SubmitQuizAttemptRequest req,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(quizService.submitAttempt(quizId, user, req));
    }

    @GetMapping("/{quizId}/leaderboard")
    public ResponseEntity<List<LeaderboardEntryDto>> leaderboard(
            @PathVariable Long quizId
    ) {
        return ResponseEntity.ok(quizService.getLeaderboard(quizId));
    }

    // --------- REALTIME FLOW ----------

    @PostMapping("/{quizId}/join")
    public ResponseEntity<?> joinRealtimeQuiz(
            @PathVariable Long quizId,
            @AuthenticationPrincipal User currentUser
    ) {
        QuizService.JoinMode mode = quizService.joinRealtimeQuiz(quizId, currentUser);
        return ResponseEntity.ok(Map.of("mode", mode.name()));
    }

    @GetMapping("/{quizId}/realtime/pending")
    public List<RealtimeParticipantDto> getPending(
            @PathVariable Long quizId,
            @AuthenticationPrincipal User currentUser
    ) {
        return quizService.getPendingParticipants(quizId, currentUser);
    }

    @PostMapping("/{quizId}/realtime/approve/{userId}")
public ResponseEntity<Void> approveParticipant(
        @PathVariable Long quizId,
        @PathVariable Long userId,
        @AuthenticationPrincipal User currentUser
) {
    quizService.approveParticipant(quizId, userId, currentUser);
    return ResponseEntity.ok().build();
}

@GetMapping("/{quizId}/join-state")
public ResponseEntity<?> joinState(
        @PathVariable Long quizId,
        @AuthenticationPrincipal User currentUser
) {
    QuizService.JoinState state = quizService.getJoinState(quizId, currentUser);
    return ResponseEntity.ok(Map.of("state", state.name()));
}


    @PostMapping("/{quizId}/leave")
    public ResponseEntity<Void> leaveRealtimeQuiz(
            @PathVariable Long quizId,
            @Parameter(hidden = true) @AuthenticationPrincipal User currentUser
    ) {
        quizService.leaveRealtimeQuiz(quizId, currentUser);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{quizId}/start")
    public ResponseEntity<Void> startRealtimeQuiz(
            @PathVariable Long quizId,
            @Parameter(hidden = true) @AuthenticationPrincipal User currentUser
    ) {
        quizService.startRealtimeQuiz(quizId, currentUser);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/end")
    public ResponseEntity<?> endQuiz(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        quizService.endQuiz(id, currentUser);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{quizId}/expire")
    public ResponseEntity<Void> expireQuiz(
            @PathVariable Long quizId,
            @Parameter(hidden = true) @AuthenticationPrincipal User currentUser
    ) {
        quizService.endQuiz(quizId, currentUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{quizId}/status")
    public ResponseEntity<RealtimeStatusResponse> getRealtimeStatus(
            @PathVariable Long quizId
    ) {
        return ResponseEntity.ok(quizService.getRealtimeStatus(quizId));
    }

    @PostMapping("/{quizId}/submit")
    public ResponseEntity<Void> submitQuiz(
            @PathVariable Long quizId,
            @RequestParam(name = "isRealtime", defaultValue = "false") boolean isRealtime,
            @RequestParam(name = "score", required = false) Integer score,
            @Parameter(hidden = true) @AuthenticationPrincipal User currentUser
    ) {
        quizService.submitQuiz(quizId, currentUser, isRealtime, score);
        return ResponseEntity.ok().build();
    }

    // ------------- helper to build detail response -------------
    private QuizDetailResponse buildQuizDetailResponse(Quiz quiz) {
        List<QuizQuestionDto> questionDtos = new ArrayList<>();
        if (quiz.getQuestions() != null) {
            for (QuizQuestion q : quiz.getQuestions()) {
                QuizQuestionDto dto = new QuizQuestionDto();
                dto.setId(q.getId());
                dto.setType(q.getType());
                dto.setText(q.getText());
                dto.setOrdinalPosition(q.getOrdinalPosition());

                List<String> options = new ArrayList<>();
                if (q.getOption1() != null) options.add(q.getOption1());
                if (q.getOption2() != null) options.add(q.getOption2());
                if (q.getOption3() != null) options.add(q.getOption3());
                if (q.getOption4() != null) options.add(q.getOption4());
                dto.setOptions(options);

                dto.setCorrectIndex(q.getCorrectIndex());
                dto.setCorrectBool(q.getCorrectBool());
                dto.setCodingAnswer(q.getCodingAnswer());

                questionDtos.add(dto);
            }
        }

        QuizDetailResponse resp = new QuizDetailResponse();
        resp.setId(quiz.getId());
        resp.setTitle(quiz.getTitle());
        resp.setDescription(quiz.getDescription());
        resp.setDifficulty(quiz.getDifficulty());
        resp.setPublic(quiz.isPublic());
        resp.setRealtime(quiz.isRealtime());
        resp.setStatus(quiz.getStatus());
        resp.setDurationSeconds(quiz.getDurationSeconds());
        resp.setQuestions(questionDtos);

        return resp;
    }
    private QuizAttemptDto toDto(QuizAttempt attempt) {
        if (attempt == null) return null;
    
        QuizAttemptDto dto = new QuizAttemptDto();
        dto.setId(attempt.getId());
    
        if (attempt.getQuiz() != null) {
            dto.setQuizId(attempt.getQuiz().getId());
            dto.setQuizTitle(attempt.getQuiz().getTitle());
        }
    
        dto.setScore(attempt.getScore());
        dto.setTimeTakenMillis(attempt.getTimeTakenMillis());
        dto.setSubmittedAt(attempt.getSubmittedAt());
    
        // 🔹 NEW: analytics fields
        dto.setTotalQuestions(attempt.getTotalQuestions());
        dto.setCorrectCount(attempt.getCorrectCount());
        dto.setWrongCount(attempt.getWrongCount());
        dto.setSkippedCount(attempt.getSkippedCount());
    
        return dto;
    }
    private Map<String, Object> toAttemptPayload(QuizAttempt attempt) {
        if (attempt == null) {
            return null;
        }

        Map<String, Object> m = new HashMap<>();
        m.put("id", attempt.getId());

        if (attempt.getQuiz() != null) {
            m.put("quizId", attempt.getQuiz().getId());
            m.put("quizTitle", attempt.getQuiz().getTitle());
        }

        m.put("score", attempt.getScore());
        m.put("timeTakenMillis", attempt.getTimeTakenMillis());

        Instant submittedAt = attempt.getSubmittedAt();
        m.put("submittedAt", submittedAt != null ? submittedAt.toString() : null);

        return m;
    }
}