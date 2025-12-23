package com.example.demo.service;

import com.example.demo.domain.AcademicProfile;
import com.example.demo.domain.ProfileLike;
import com.example.demo.domain.Quiz;
import com.example.demo.domain.QuizAttempt;
import com.example.demo.domain.QuizStatus;
import com.example.demo.domain.UserInterest;
import com.example.demo.dto.ProfileLikeDto;
import com.example.demo.dto.ProfileSummaryDto;
import com.example.demo.repo.ClaimRepository;
import com.example.demo.repo.DoubtAnswerRepository;
import com.example.demo.repo.ProfileLikeRepository;
import com.example.demo.repo.QuizAttemptRepository;
import com.example.demo.repo.QuizRepository;
import com.example.demo.repo.UserInterestRepository;
import com.example.demo.repository.AcademicProfileRepository;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.dto.MiniProfileDto;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProfileService {

    private final DoubtAnswerRepository doubtAnswerRepository;
    private final QuizRepository quizRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final AcademicProfileRepository academicProfileRepository;
    private final ClaimRepository claimRepository;
    private final UserInterestRepository userInterestRepository;
    private final ProfileLikeRepository profileLikeRepository;
    private final UserRepository userRepository;
    private final LoginActivityService loginActivityService;

    public ProfileService(
            DoubtAnswerRepository doubtAnswerRepository,
            QuizRepository quizRepository,
            QuizAttemptRepository quizAttemptRepository,
            AcademicProfileRepository academicProfileRepository,
            ClaimRepository claimRepository,
            UserInterestRepository userInterestRepository,
            ProfileLikeRepository profileLikeRepository,
            UserRepository userRepository,
            LoginActivityService loginActivityService
    ) {
        this.doubtAnswerRepository = doubtAnswerRepository;
        this.quizRepository = quizRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.academicProfileRepository = academicProfileRepository;
        this.claimRepository = claimRepository;
        this.userInterestRepository = userInterestRepository;
        this.profileLikeRepository = profileLikeRepository;
        this.userRepository = userRepository;
        this.loginActivityService = loginActivityService;
    }

    // ==================== INTERESTS ====================

    public List<String> getMyInterests(User user) {
        if (user == null) {
            throw new RuntimeException("No authenticated user");
        }
        List<UserInterest> list = userInterestRepository.findByUserOrderByCreatedAtAsc(user);
        return list.stream()
                .map(UserInterest::getLabel)
                .toList();
    }

    @Transactional
    public List<String> saveMyInterests(User user, List<String> labels) {
        if (user == null) {
            throw new RuntimeException("No authenticated user");
        }
        if (labels == null) {
            labels = List.of();
        }

        List<String> cleaned = labels.stream()
                .map(s -> s == null ? "" : s.trim())
                .filter(s -> !s.isBlank())
                .distinct()
                .limit(5L)
                .toList();

        // purane interests delete
        userInterestRepository.deleteByUser(user);

        // naye interests save
        for (String lbl : cleaned) {
            UserInterest ui = new UserInterest();
            ui.setUser(user);
            ui.setLabel(lbl);
            userInterestRepository.save(ui);
        }

        // AcademicProfile ke saath sync
        updateMyInterests(user, cleaned);
        return getMyInterests(user);
    }

    @Transactional(readOnly = true)
    public MiniProfileDto getMiniProfileFor(String email, User currentUser) {
        if (currentUser == null) {
            throw new RuntimeException("No authenticated user");
        }

        User target = userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        Long targetId = target.getId();

        // Stats (same logic jo getMyProfile me use kar rahe ho)
        long doubtAnswers = doubtAnswerRepository.countBySolverId(targetId);
        long faqClaims = claimRepository.countByUserId(targetId.toString());
        long problemsAttempted = doubtAnswers + faqClaims;
        long doubtsSolved = doubtAnswerRepository.countBySolverIdAndAcceptedTrue(targetId);

        long quizReachPoints = computeQuizReachPoints(target);
        long rankBonus = computeQuizRankBonus(target);

        long totalPoints =
                problemsAttempted * 2L +
                doubtsSolved * 10L +
                quizReachPoints +
                rankBonus;

        // Global ranking (reuse same logic)
        Map<Long, Long> userPoints = new HashMap<>();
        List<User> allUsers = userRepository.findAll();
        for (User u : allUsers) {
            long pts = computeTotalPointsForUser(u);
            userPoints.put(u.getId(), pts);
        }

        List<Long> sortedUserIds = userPoints.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey)
                .toList();

        int idx = sortedUserIds.indexOf(targetId);
        long rankGlobal = (idx >= 0) ? idx + 1L : 0L;
        long totalUsersGlobal = sortedUserIds.size();

        String branchLabel = resolveBranchLabel(target);
        String initials = buildInitials(target.getName());

        // interests
        List<UserInterest> ui = userInterestRepository.findByUserOrderByCreatedAtAsc(target);
        List<String> interests = ui.stream()
                .map(UserInterest::getLabel)
                .toList();

        // likes for target user
        long likes = profileLikeRepository.countByTargetUser(target);
        boolean likedByMe = profileLikeRepository.existsByTargetUserAndLikedBy(target, currentUser);

        return new MiniProfileDto(
                target.getName(),
                target.getEmail(),
                initials,
                branchLabel,
                doubtsSolved,
                problemsAttempted,
                rankGlobal,
                totalUsersGlobal,
                interests,
                likes,
                likedByMe
        );
    }

    @Transactional
    public ProfileLikeDto toggleLikeFor(String email, User currentUser) {
        if (currentUser == null) {
            throw new RuntimeException("No authenticated user");
        }

        User target = userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        // chahe khud ko like karne do ya na – yahan check laga sakte ho
        // if (target.getId().equals(currentUser.getId())) {
        //     throw new RuntimeException("Cannot like yourself");
        // }

        Optional<ProfileLike> existingOpt =
                profileLikeRepository.findByTargetUserAndLikedBy(target, currentUser);

        if (existingOpt.isPresent()) {
            profileLikeRepository.delete(existingOpt.get());
        } else {
            ProfileLike pl = new ProfileLike();
            pl.setTargetUser(target);
            pl.setLikedBy(currentUser);
            profileLikeRepository.save(pl);
        }

        long newCount = profileLikeRepository.countByTargetUser(target);
        boolean likedNow = profileLikeRepository.existsByTargetUserAndLikedBy(target, currentUser);

        return new ProfileLikeDto(newCount, likedNow);
    }
    // ==================== LIKES ====================

    public ProfileLikeDto getMyLikes(User user) {
        if (user == null) {
            throw new RuntimeException("No authenticated user");
        }
        long likes = profileLikeRepository.countByTargetUser(user);
        boolean likedByMe = profileLikeRepository.existsByTargetUserAndLikedBy(user, user);
        return new ProfileLikeDto(likes, likedByMe);
    }

    public ProfileLikeDto toggleSelfLike(User user) {
        if (user == null) {
            throw new RuntimeException("No authenticated user");
        }

        var existingOpt = profileLikeRepository.findByTargetUserAndLikedBy(user, user);
        if (existingOpt.isPresent()) {
            profileLikeRepository.delete(existingOpt.get());
        } else {
            ProfileLike pl = new ProfileLike();
            pl.setTargetUser(user);
            pl.setLikedBy(user);
            profileLikeRepository.save(pl);
        }

        long newCount = profileLikeRepository.countByTargetUser(user);
        boolean likedNow = profileLikeRepository.existsByTargetUserAndLikedBy(user, user);

        return new ProfileLikeDto(newCount, likedNow);
    }

    // ==================== QUIZ REACH ( +8 per learner, max 3/day ) ====================

    private long computeQuizReachPoints(User u) {
        List<Quiz> myQuizzes = quizRepository.findByCreatedByOrderByCreatedAtDesc(u);
        if (myQuizzes.isEmpty()) return 0L;

        Map<LocalDate, java.util.Set<Long>> learnersPerDay = new HashMap<>();

        for (Quiz q : myQuizzes) {
            List<QuizAttempt> attempts = quizAttemptRepository.findByQuiz(q);
            if (attempts == null || attempts.isEmpty()) continue;

            for (QuizAttempt a : attempts) {
                if (a.getUser() == null) continue;
                if (a.getUser().getId().equals(u.getId())) continue;   // apna attempt ignore
                if (a.getSubmittedAt() == null) continue;

                LocalDate day = a.getSubmittedAt()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();

                learnersPerDay
                        .computeIfAbsent(day, d -> new java.util.HashSet<>())
                        .add(a.getUser().getId());
            }
        }

        long total = 0L;
        for (var entry : learnersPerDay.entrySet()) {
            int uniqueLearners = entry.getValue().size();
            int countedLearners = Math.min(uniqueLearners, 3);  // max 3 per day
            total += countedLearners * 8L;                      // +8 per learner
        }
        return total;
    }

    // ==================== QUIZ RANK BONUS ====================

    /**
     * Har quiz jisme user ne attempt diya aur quiz ENDED hai:
     *  - rank 1 => +9
     *  - rank 2 => +6
     *  - rank 3 => +3
     *  (per quiz best rank only)
     */
    private long computeQuizRankBonus(User u) {
        Long userId = u.getId();

        List<QuizAttempt> attempts = quizAttemptRepository.findByUser(u);
        if (attempts.isEmpty()) return 0L;

        Map<Long, Integer> bestRankPerQuiz = new HashMap<>();

        for (QuizAttempt a : attempts) {
            Quiz quiz = a.getQuiz();
            if (quiz == null) continue;
            if (quiz.getStatus() != QuizStatus.ENDED) continue;

            Long quizId = quiz.getId();
            if (bestRankPerQuiz.getOrDefault(quizId, Integer.MAX_VALUE) == 1) {
                continue;
            }

            List<QuizAttempt> leaderboardAttempts =
                    quizAttemptRepository.findTop20ByQuizOrderByScoreDescTimeTakenMillisAscSubmittedAtAsc(quiz);

            int rank = 1;
            Integer foundRank = null;

            for (QuizAttempt lb : leaderboardAttempts) {
                if (lb.getScore() == null) continue;

                if (lb.getUser() != null && lb.getUser().getId().equals(userId)) {
                    foundRank = rank;
                    break;
                }
                rank++;
                if (rank > 3) break;
            }

            if (foundRank != null) {
                Integer existing = bestRankPerQuiz.get(quizId);
                if (existing == null || foundRank < existing) {
                    bestRankPerQuiz.put(quizId, foundRank);
                }
            }
        }

        long bonus = 0L;
        for (Integer r : bestRankPerQuiz.values()) {
            if (r == 1)      bonus += 9L;
            else if (r == 2) bonus += 6L;
            else if (r == 3) bonus += 3L;
        }
        return bonus;
    }

    // ==================== HELPER: TOTAL POINTS FOR ANY USER ====================

    private long computeTotalPointsForUser(User u) {
        Long uid = u.getId();

        long doubtAnswers = doubtAnswerRepository.countBySolverId(uid);
        long faqClaims = claimRepository.countByUserId(uid.toString());
        long problemsAttempted = doubtAnswers + faqClaims;

        long doubtsSolved = doubtAnswerRepository.countBySolverIdAndAcceptedTrue(uid);

        long quizReachPoints = computeQuizReachPoints(u);
        long rankBonus = computeQuizRankBonus(u);

        return problemsAttempted * 2L
                + doubtsSolved * 10L
                + quizReachPoints
                + rankBonus;
    }

    // ==================== MAIN PROFILE SUMMARY ====================

    @Transactional(readOnly = true)
    public ProfileSummaryDto getMyProfile(User user) {
        if (user == null) {
            throw new RuntimeException("No authenticated user");
        }

        Long userId = user.getId();

        long doubtAnswers = doubtAnswerRepository.countBySolverId(userId);
        long faqClaims = claimRepository.countByUserId(userId.toString());
        long problemsAttempted = doubtAnswers + faqClaims;

        long doubtsSolved = doubtAnswerRepository.countBySolverIdAndAcceptedTrue(userId);

        List<Quiz> hosted = quizRepository.findByCreatedByOrderByCreatedAtDesc(user);
        long quizzesCreated = hosted.stream()
                .filter(q -> quizAttemptRepository.countByQuiz(q) > 0)
                .count();

        List<QuizAttempt> myAttempts = quizAttemptRepository.findByUser(user);
        long totalQuizAttempts = myAttempts.size();

        String branchLabel = resolveBranchLabel(user);
        String initials = buildInitials(user.getName());

        long quizReachPointsForMe = computeQuizReachPoints(user);
        long rankBonusForMe = computeQuizRankBonus(user);

        long totalPoints =
                problemsAttempted * 2L +
                doubtsSolved * 10L +
                quizReachPointsForMe +
                rankBonusForMe;

        long daysActiveThisYear = loginActivityService.countActiveDaysThisYear(user);
        // 🔥 saal ke saare login dates (LocalDate list)
        List<LocalDate> loginDates = loginActivityService.getLoginDatesThisYear(user);
        List<String> loginDateStrings = loginDates.stream()
                .map(LocalDate::toString)   // "2025-11-30" format
                .toList();
        // ---- GLOBAL RANK CALCULATION ----
        Map<Long, Long> userPoints = new HashMap<>();
        List<User> allUsers = userRepository.findAll();
        for (User u : allUsers) {
            long pts = computeTotalPointsForUser(u);
            userPoints.put(u.getId(), pts);
        }

        List<Long> sortedUserIds = userPoints.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey)
                .toList();

        int idx = sortedUserIds.indexOf(userId);
        long rankGlobal = (idx >= 0) ? idx + 1L : 0L;
        long totalUsersGlobal = sortedUserIds.size();

        LocalDate lastLogin =
        (user.getLastLoginDate() != null)
                ? user.getLastLoginDate().toLocalDate()
                : null;

ProfileSummaryDto dto = new ProfileSummaryDto(
        user.getName(),
        user.getEmail(),
        initials,
        branchLabel,
        problemsAttempted,
        doubtsSolved,
        quizzesCreated,
        totalQuizAttempts,
        totalPoints,
        rankGlobal,
        totalUsersGlobal,
        daysActiveThisYear,
        lastLogin // 👈 streak ke liye important
);
        dto.setId(user.getId());
        dto.setTotalUsers(totalUsersGlobal);
        dto.setLoginDatesThisYear(loginDateStrings);

        return dto;
    }

    // ==================== AcademicProfile interests sync ====================

    public void updateMyInterests(User user, List<String> rawInterests) {
        if (user == null) {
            throw new RuntimeException("No authenticated user");
        }

        AcademicProfile profile = academicProfileRepository
                .findByUser(user)
                .orElseThrow(() -> new RuntimeException("Academic profile not found for user"));

        if (rawInterests == null || rawInterests.isEmpty()) {
            profile.setInterests(null);
        } else {
            String csv = rawInterests.stream()
                    .map(s -> s == null ? "" : s.trim())
                    .filter(s -> !s.isEmpty())
                    .limit(5)
                    .collect(Collectors.joining(","));
            profile.setInterests(csv);
        }

        academicProfileRepository.save(profile);
    }

    // ==================== Helpers ====================

    private String resolveBranchLabel(User user) {
        Optional<AcademicProfile> opt = academicProfileRepository.findByUser(user);
        if (opt.isEmpty()) return "No stream selected";

        AcademicProfile p = opt.get();

        String edu = p.getEducationLevel();
        String main = p.getMainStream();
        String spec = p.getSpecialization();

        if (spec != null && !spec.isBlank()) {
            if (edu != null && !edu.isBlank()) {
                return edu + " · " + spec;
            }
            return spec;
        }
        if (main != null && !main.isBlank()) {
            if (edu != null && !edu.isBlank()) {
                return edu + " · " + main;
            }
            return main;
        }
        return (edu != null && !edu.isBlank()) ? edu : "No stream selected";
    }

    private String buildInitials(String name) {
        if (name == null || name.isBlank()) return "AL";

        String[] parts = name.trim().split("\\s+");
        char first = parts[0].charAt(0);

        char second;
        if (parts.length > 1) {
            second = parts[parts.length - 1].charAt(0);
        } else {
            second = parts[0].length() > 1 ? parts[0].charAt(1) : first;
        }

        return ("" + first + second).toUpperCase(Locale.ROOT);
    }
}