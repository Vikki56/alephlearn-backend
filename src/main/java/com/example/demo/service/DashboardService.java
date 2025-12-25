package com.example.demo.service;

import com.example.demo.domain.AcademicProfile;
import com.example.demo.domain.Quiz;
import com.example.demo.domain.repo.PreviousPaperRepository;
import com.example.demo.domain.entity.PreviousPaper;
import com.example.demo.dto.*;
import com.example.demo.entity.Doubt;
import com.example.demo.repo.DoubtAnswerRepository;
import com.example.demo.repo.DoubtRepository;
import com.example.demo.repo.QuizAttemptRepository;
import com.example.demo.repo.QuizRepository;
import com.example.demo.repository.AcademicProfileRepository;
import com.example.demo.user.User;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.domain.QuizAttempt;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final ProfileService profileService;
    private final DoubtRepository doubtRepository;
    private final DoubtAnswerRepository doubtAnswerRepository;
    private final QuizRepository quizRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final PreviousPaperRepository previousPaperRepository;
    private final AcademicProfileRepository academicProfileRepository;

    public DashboardService(ProfileService profileService,
                            DoubtRepository doubtRepository,
                            DoubtAnswerRepository doubtAnswerRepository,
                            QuizRepository quizRepository,
                            QuizAttemptRepository quizAttemptRepository,
                            PreviousPaperRepository previousPaperRepository,
                            AcademicProfileRepository academicProfileRepository) {
        this.profileService = profileService;
        this.doubtRepository = doubtRepository;
        this.doubtAnswerRepository = doubtAnswerRepository;
        this.quizRepository = quizRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.previousPaperRepository = previousPaperRepository;
        this.academicProfileRepository = academicProfileRepository;
    }

    public DashboardSummaryDto getSummary(User currentUser) {
        if (currentUser == null) {
            throw new RuntimeException("No authenticated user");
        }

        Long userId = currentUser.getId();

        // ---------- 1) Profile base ----------
        ProfileSummaryDto profile = profileService.getMyProfile(currentUser);

        // ---------- 2) Quick stats ----------
        long doubtsAsked       = doubtRepository.countByAskerId(userId);
        long answersGiven      = doubtAnswerRepository.countBySolverId(userId);
        long doubtsSolved      = doubtAnswerRepository.countBySolverIdAndAcceptedTrue(userId);
        long problemsAttempted = profile.getProblemsAttempted();

        long quizzesCreated    = quizRepository.countByCreatedBy(currentUser);
        long quizzesAttempted  = quizAttemptRepository.countByUser(currentUser);

        DashboardQuickStatsDto quick = new DashboardQuickStatsDto();
        quick.setDoubtsAsked(doubtsAsked);
        quick.setAnswersGiven(answersGiven);
        quick.setDoubtsSolved(doubtsSolved);
        quick.setProblemsAttempted(problemsAttempted);
        quick.setQuizzesCreated(quizzesCreated);
        quick.setQuizzesAttempted(quizzesAttempted);
        quick.setTotalPoints(profile.getTotalPoints());
        quick.setRankGlobal(profile.getRankGlobal());
        quick.setTotalUsersGlobal(profile.getTotalUsersGlobal());
                // ---------- 2.1) Latest quiz score % ----------
                quizAttemptRepository
                .findTopByUserAndCompletedTrueOrderBySubmittedAtDesc(currentUser)
                .ifPresent(attempt -> {
                    Integer totalQ = attempt.getTotalQuestions();
                    Integer correctQ = attempt.getCorrectCount();

                    if (totalQ != null && totalQ > 0 && correctQ != null) {
                        double percent = correctQ * 100.0 / totalQ;
                        quick.setLatestQuizScorePercent(percent);
                    }
                });

        // ---------- 3) Activity (week / month / streak) ----------
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        LocalDate startOfWeekDate = today.with(DayOfWeek.MONDAY);
        LocalDateTime startOfWeek = startOfWeekDate.atStartOfDay();

        LocalDate startOfMonthDate = today.withDayOfMonth(1);
        LocalDateTime startOfMonth = startOfMonthDate.atStartOfDay();

        long doubtsThisWeek = doubtRepository
                .countByAskerIdAndCreatedAtBetween(userId, startOfWeek, now);

        long answersThisMonth = doubtAnswerRepository
                .countBySolverIdAndCreatedAtBetween(userId, startOfMonth, now);

        long solutionsAcceptedAllTime = doubtsSolved;

        DashboardActivityDto activity = new DashboardActivityDto();
        activity.setDoubtsThisWeek(doubtsThisWeek);
        activity.setAnswersThisMonth(answersThisMonth);
        activity.setSolutionsAcceptedAllTime(solutionsAcceptedAllTime);
        activity.setDaysActiveThisYear(profile.getDaysActiveThisYear());
        activity.setLastLoginDate(profile.getLastLoginDate());
        activity.setLoginDatesThisYear(profile.getLoginDatesThisYear());

        // ---------- 4) Active chats (placeholder abhi 0) ----------
        long activeChats = 0L; 

        // ---------- 5) Trending discussions (doubts + quizzes) ----------
        List<DashboardTrendingItemDto> trendingItems = buildTrending(currentUser);

        // ---------- 6) Recent items (quizzes + papers) ----------
        List<DashboardRecentItemDto> recentItems = buildRecent(currentUser);

        // ---------- 7) Assemble ----------
        DashboardSummaryDto dto = new DashboardSummaryDto();
        dto.setProfile(profile);
        dto.setQuickStats(quick);
        dto.setActivity(activity);
        dto.setActiveChats(activeChats);
        dto.setTrending(trendingItems);
        dto.setRecent(recentItems);

        return dto;
    }

    // ======================= helpers =======================

    private List<DashboardTrendingItemDto> buildTrending(User currentUser) {
        List<DashboardTrendingItemDto> list = new ArrayList<>();

        // ---- try to respect stream (education/main/specialization) ----
        Optional<AcademicProfile> profileOpt =
                academicProfileRepository.findByUser(currentUser);

        // ---------- Trending Doubts (same stream) ----------
        List<Doubt> trendingDoubts;
        Sort doubtSort = Sort.by(
                Sort.Order.desc("answerCount"),
                Sort.Order.desc("viewCount"),
                Sort.Order.desc("createdAt")
        );
        Pageable top4 = PageRequest.of(0, 4, doubtSort);

        if (profileOpt.isPresent()) {
            AcademicProfile p = profileOpt.get();
            trendingDoubts = doubtRepository
                    .findByEducationLevelIgnoreCaseAndMainStreamIgnoreCaseAndSpecializationIgnoreCase(
                            safe(p.getEducationLevel()),
                            safe(p.getMainStream()),
                            safe(p.getSpecialization()),
                            top4
                    )
                    .getContent();
        } else {
            trendingDoubts = doubtRepository.findAll(top4).getContent();
        }

        for (Doubt d : trendingDoubts) {
            String subtitle = d.getSubject();
            String meta = d.getAnswerCount() + " answers · " +
                          d.getViewCount() + " views";
            list.add(new DashboardTrendingItemDto(
                    "DOUBT",
                    d.getId(),
                    d.getTitle(),
                    subtitle,
                    meta
            ));
        }

        // ---------- Trending Quizzes (public, same specialization) ----------
        List<Quiz> quizPool;
        if (profileOpt.isPresent()) {
            AcademicProfile p = profileOpt.get();
            String spec = safe(p.getSpecialization());
            if (!spec.isBlank()) {
                quizPool = quizRepository
                        .findByIsPublicTrueAndSpecializationIgnoreCaseOrderByCreatedAtDesc(spec);
            } else {
                quizPool = quizRepository.findByIsPublicTrueOrderByCreatedAtDesc();
            }
        } else {
            quizPool = quizRepository.findByIsPublicTrueOrderByCreatedAtDesc();
        }

        quizPool.sort((a, b) -> {
            int cmp = Long.compare(b.getTotalAttempts(), a.getTotalAttempts());
            if (cmp != 0) return cmp;
            return b.getCreatedAt().compareTo(a.getCreatedAt());
        });

        int maxQuiz = Math.min(3, quizPool.size());
        for (int i = 0; i < maxQuiz; i++) {
            Quiz q = quizPool.get(i);
            String subtitle = q.getDescription() != null ? q.getDescription() : "";
            String meta = q.getTotalAttempts() + " attempts · " +
                    q.getDifficulty().name().toLowerCase();
            list.add(new DashboardTrendingItemDto(
                    "QUIZ",
                    q.getId(),
                    q.getTitle(),
                    subtitle,
                    meta
            ));
        }

        return list;
    }

    private List<DashboardRecentItemDto> buildRecent(User currentUser) {
        List<DashboardRecentItemDto> list = new ArrayList<>();

        // ---------- Recent quizzes ----------
        List<Quiz> recentQuizzes = quizRepository.findByIsPublicTrueOrderByCreatedAtDesc();
        int qLimit = Math.min(4, recentQuizzes.size());

        for (int i = 0; i < qLimit; i++) {
            Quiz q = recentQuizzes.get(i);
            String subtitle = q.getDescription() != null ? q.getDescription() : "";
            String meta = q.getDifficulty().name().toLowerCase() +
                    " · " + q.getTotalAttempts() + " attempts";

            list.add(new DashboardRecentItemDto(
                    "QUIZ",
                    q.getId(),
                    q.getTitle(),
                    subtitle,
                    meta
            ));
        }

        // ---------- Recent papers ----------
        List<PreviousPaper> papers = previousPaperRepository.findAllSorted("latest");
        int pLimit = Math.min(4, papers.size());

        for (int i = 0; i < pLimit; i++) {
            PreviousPaper p = papers.get(i);
            String title = p.getSubjectName();
            String subtitle = p.getCollegeName() + " · " + p.getExamType();
            String meta = p.getExamYear() + " · " +
                    p.getDownloads() + " downloads";

            list.add(new DashboardRecentItemDto(
                    "PAPER",
                    p.getId(),
                    title,
                    subtitle,
                    meta
            ));
        }

        return list;
    }

    private String safe(String v) {
        return v == null ? "" : v;
    }
}