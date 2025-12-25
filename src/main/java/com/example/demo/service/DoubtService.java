package com.example.demo.service;

import com.example.demo.entity.AnswerReply;
import com.example.demo.entity.Doubt;
import com.example.demo.entity.DoubtLike;
import com.example.demo.entity.DoubtStatus;
import com.example.demo.entity.DoubtAnswer;
import com.example.demo.entity.DoubtView;
import com.example.demo.entity.NotificationType;
import com.example.demo.repo.DoubtLikeRepository;
import com.example.demo.repo.DoubtRepository;
import com.example.demo.repo.AnswerReplyRepository;
import com.example.demo.repo.DoubtAnswerRepository;
import com.example.demo.repo.DoubtViewRepository;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.example.demo.dto.ActivityStatsDto;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.example.demo.domain.AcademicProfile;
import com.example.demo.repository.AcademicProfileRepository;
import com.example.demo.security.AuthUser;

@Service
public class DoubtService {

    private final DoubtRepository doubtRepository;
    private final DoubtLikeRepository doubtLikeRepository;
    private final DoubtAnswerRepository doubtAnswerRepository;
    private final UserRepository userRepository;
    private final DoubtViewRepository doubtViewRepository;
    private final NotificationService notificationService;
    private final AnswerReplyRepository answerReplyRepository;
    private final AcademicProfileRepository academicProfileRepository;

    public DoubtService(DoubtRepository doubtRepository,
    DoubtLikeRepository doubtLikeRepository,
    DoubtAnswerRepository doubtAnswerRepository,
    UserRepository userRepository,
    DoubtViewRepository doubtViewRepository,
    NotificationService notificationService,
    AnswerReplyRepository answerReplyRepository,
    AcademicProfileRepository academicProfileRepository) {
this.doubtRepository = doubtRepository;
this.doubtLikeRepository = doubtLikeRepository;
this.doubtAnswerRepository = doubtAnswerRepository;
this.userRepository = userRepository;
this.doubtViewRepository = doubtViewRepository;
this.notificationService = notificationService;
this.answerReplyRepository = answerReplyRepository;
this.academicProfileRepository = academicProfileRepository;
}


public Doubt updateDoubt(Long id, Doubt updated) {
    Doubt existing = doubtRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Doubt not found"));

    existing.setTitle(updated.getTitle());
    existing.setDescription(updated.getDescription());

    if (updated.getSubject() != null && !updated.getSubject().isBlank()) {
        existing.setSubject(updated.getSubject());
    }

    if (updated.getTags() != null && !updated.getTags().isEmpty()) {
        existing.setTags(updated.getTags());
    }
    if (existing.getSubject() == null || existing.getSubject().isBlank()) {
        existing.setSubject("General");
    }

    return doubtRepository.save(existing);
}

public Doubt getDoubtById(Long id) {
    return doubtRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Doubt not found with id: " + id));
}

public Doubt saveDoubt(Doubt doubt) {
    return doubtRepository.save(doubt);
}
    public AnswerReply addReply(Long answerId, Long userId, String text) {
    DoubtAnswer answer = doubtAnswerRepository.findById(answerId)
            .orElseThrow(() -> new RuntimeException("Answer not found"));

    User replier = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

    AnswerReply reply = new AnswerReply();
    reply.setAnswer(answer);
    reply.setReplier(replier);
    reply.setText(text);

    AnswerReply saved = answerReplyRepository.save(reply);

    User solver = answer.getSolver();
    if (!solver.getId().equals(userId)) {
        notificationService.notify(
                solver,
                NotificationType.ANSWER_REPLIED,
                replier.getName() + " replied to your answer.",
                answer.getDoubt().getId(),
                answer.getId()
        );
    }

    return saved;
}

// ---------- Get all replies for an answer ----------
public List<AnswerReply> getReplies(Long answerId) {
    DoubtAnswer ans = doubtAnswerRepository.findById(answerId)
            .orElseThrow(() -> new RuntimeException("Answer not found"));

    return answerReplyRepository.findByAnswerOrderByCreatedAtAsc(ans);
}

// ---------- Notify when an answer is liked ----------
public void notifyAnswerLiked(Long answerId, Long userId) {
    DoubtAnswer answer = doubtAnswerRepository.findById(answerId)
            .orElseThrow(() -> new RuntimeException("Answer not found"));

    User liker = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

    User solver = answer.getSolver();
    if (solver != null && !solver.getId().equals(userId)) {
        notificationService.notify(
                solver,
                NotificationType.ANSWER_LIKED,
                liker.getName() + " liked your answer.",
                answer.getDoubt().getId(),
                answer.getId()
        );
    }
}

    // ---------- Create ----------
    public Doubt createDoubt(Doubt doubt) {

        User asker = doubt.getAsker();
        if (asker == null) {
            throw new RuntimeException("Asker must not be null");
        }

        AcademicProfile profile = academicProfileRepository
                .findByUser(asker)
                .orElseThrow(() ->
                        new RuntimeException("Academic profile not found for user: " + asker.getId())
                );

        doubt.setEducationLevel(profile.getEducationLevel());
        doubt.setMainStream(profile.getMainStream());
        doubt.setSpecialization(profile.getSpecialization());

        return doubtRepository.save(doubt);
    }


    public Page<Doubt> listDoubts(String subject,
        DoubtStatus status,
        String sort,
        int page,
        int size) {

User current = AuthUser.current();
if (current == null) {
throw new RuntimeException("No authenticated user in context");
}

AcademicProfile profile = academicProfileRepository
.findByUser(current)
.orElseThrow(() ->
new RuntimeException("Academic profile not found for user: " + current.getId())
);

String edu  = profile.getEducationLevel();
String main = profile.getMainStream();
String spec = profile.getSpecialization();

Sort sortObj;
if ("MOST_LIKED".equalsIgnoreCase(sort)) {
sortObj = Sort.by(Sort.Direction.DESC, "likeCount");
} else if ("MOST_VIEWED".equalsIgnoreCase(sort)) {
sortObj = Sort.by(Sort.Direction.DESC, "viewCount");
} else if ("UNANSWERED".equalsIgnoreCase(sort)) {
sortObj = Sort.by(Sort.Direction.ASC, "answerCount")
.and(Sort.by(Sort.Direction.DESC, "createdAt"));
} else { // default: latest
sortObj = Sort.by(Sort.Direction.DESC, "createdAt");
}

Pageable pageable = PageRequest.of(page, size, sortObj);

boolean hasSubject = subject != null && !subject.isBlank();
boolean hasStatus  = status != null;

if (hasSubject && hasStatus) {
return doubtRepository
.findByEducationLevelIgnoreCaseAndMainStreamIgnoreCaseAndSpecializationIgnoreCaseAndSubjectIgnoreCaseAndStatus(
  edu, main, spec, subject, status, pageable);
} else if (hasSubject) {
return doubtRepository
.findByEducationLevelIgnoreCaseAndMainStreamIgnoreCaseAndSpecializationIgnoreCaseAndSubjectIgnoreCase(
  edu, main, spec, subject, pageable);
} else if (hasStatus) {
return doubtRepository
.findByEducationLevelIgnoreCaseAndMainStreamIgnoreCaseAndSpecializationIgnoreCaseAndStatus(
  edu, main, spec, status, pageable);
} else {
return doubtRepository
.findByEducationLevelIgnoreCaseAndMainStreamIgnoreCaseAndSpecializationIgnoreCase(
  edu, main, spec, pageable);
}
}

    // ---------- Get one (and increase views, only once per user) ----------
    public Doubt getDoubt(Long id, Long userId) {

        Doubt doubt = doubtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doubt not found: " + id));

        if (userId == null) {
            doubt.setViewCount(doubt.getViewCount() + 1);
            return doubtRepository.save(doubt);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // check if this user already viewed this doubt
        Optional<DoubtView> existing =
                doubtViewRepository.findByDoubtAndViewer(doubt, user);

        if (existing.isEmpty()) {
            // first time this user is viewing → record view + increment count
            DoubtView view = new DoubtView();
            view.setDoubt(doubt);
            view.setViewer(user);
            doubtViewRepository.save(view);

            doubt.setViewCount(doubt.getViewCount() + 1);
            doubtRepository.save(doubt);
        }

        return doubt;
    }

    // ---------- LIKE ----------
    public Map<String, Long> likeDoubt(Long doubtId, Long userId) {
        Doubt doubt = doubtRepository.findById(doubtId)
                .orElseThrow(() -> new RuntimeException("Doubt not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<DoubtLike> existing =
                doubtLikeRepository.findByDoubtAndUser(doubt, user);

        if (existing.isEmpty()) {
            DoubtLike like = new DoubtLike();
            like.setDoubt(doubt);
            like.setUser(user);
            doubtLikeRepository.save(like);

            doubt.setLikeCount(doubt.getLikeCount() + 1);
            doubtRepository.save(doubt);
        }

        return Map.of("likeCount", (long) doubt.getLikeCount());
    }

    // ---------- UNLIKE ----------
    public Map<String, Long> unlikeDoubt(Long doubtId, Long userId) {
        Doubt doubt = doubtRepository.findById(doubtId)
                .orElseThrow(() -> new RuntimeException("Doubt not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<DoubtLike> existing =
                doubtLikeRepository.findByDoubtAndUser(doubt, user);

        existing.ifPresent(like -> {
            doubtLikeRepository.delete(like);
            doubt.setLikeCount(Math.max(0, doubt.getLikeCount() - 1));
            doubtRepository.save(doubt);
        });

        return Map.of("likeCount", (long) doubt.getLikeCount());
    }

    // ---------- Get all answers for a doubt ----------
    public List<DoubtAnswer> getAnswersForDoubt(Long doubtId) {
        Doubt doubt = doubtRepository.findById(doubtId)
                .orElseThrow(() -> new RuntimeException("Doubt not found"));

        return doubtAnswerRepository.findByDoubtOrderByCreatedAtAsc(doubt);
    }

    // ---------- Add answer ----------
    public DoubtAnswer addAnswer(Long doubtId, Long userId, String body, String attachmentUrl) {
        Doubt doubt = doubtRepository.findById(doubtId)
                .orElseThrow(() -> new RuntimeException("Doubt not found"));

        User solver = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        DoubtAnswer answer = new DoubtAnswer();
        answer.setDoubt(doubt);
        answer.setSolver(solver);
        answer.setBody(body);
        answer.setAttachmentUrl(attachmentUrl);

        DoubtAnswer saved = doubtAnswerRepository.save(answer);

        doubt.setAnswerCount(doubt.getAnswerCount() + 1);
        doubtRepository.save(doubt);

        User asker = doubt.getAsker();
        if (asker != null && !asker.getId().equals(userId)) {
            notificationService.notify(
                    asker,
                    NotificationType.ANSWER_POSTED,
                    "Someone answered your doubt: " + doubt.getTitle(),
                    doubt.getId(),
                    saved.getId()
            );
        }

        return saved;
    }

    // ---------- Edit answer (only by solver) ----------
    public DoubtAnswer editAnswer(Long answerId, Long userId, String body, String attachmentUrl) {
        DoubtAnswer answer = doubtAnswerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));

        if (!answer.getSolver().getId().equals(userId)) {
            throw new RuntimeException("You can edit only your own answers");
        }

        answer.setBody(body);
        answer.setAttachmentUrl(attachmentUrl);
        answer.setEdited(true);

        return doubtAnswerRepository.save(answer);
    }

    // ---------- Accept answer (only by asker) ----------
    public DoubtAnswer acceptAnswer(Long answerId, Long userId) {
        DoubtAnswer answer = doubtAnswerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));

        Doubt doubt = answer.getDoubt();

        if (!doubt.getAsker().getId().equals(userId)) {
            throw new RuntimeException("Only the asker can accept an answer");
        }

        answer.setAccepted(true);
        doubt.setAcceptedAnswer(answer);
        doubt.setStatus(DoubtStatus.RESOLVED);

        DoubtAnswer saved = doubtAnswerRepository.save(answer);
        doubtRepository.save(doubt);

        User solver = saved.getSolver();
        if (solver != null && !solver.getId().equals(userId)) {
            notificationService.notify(
                    solver,
                    NotificationType.ANSWER_ACCEPTED,
                    "Your answer was accepted for: " + doubt.getTitle(),
                    doubt.getId(),
                    saved.getId()
            );
        }

        return saved;
    }

    // ---------- List doubts asked by a specific user ----------
    public Page<Doubt> listMyDoubts(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));
        return doubtRepository.findByAskerId(userId, pageable);
    }

    // ---------- User Activity (week/month/all-time) ----------
    public ActivityStatsDto getUserActivity(Long userId) {

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        LocalDate startOfWeekDate = today.with(DayOfWeek.MONDAY);
        LocalDateTime startOfWeek = startOfWeekDate.atStartOfDay();

        LocalDate startOfMonthDate = today.withDayOfMonth(1);
        LocalDateTime startOfMonth = startOfMonthDate.atStartOfDay();

        // 1) Doubts posted THIS WEEK
        long doubtsThisWeek = doubtRepository
                .countByAskerIdAndCreatedAtBetween(userId, startOfWeek, now);

        // 2) Answers given THIS MONTH
        long answersThisMonth = doubtAnswerRepository
                .countBySolverIdAndCreatedAtBetween(userId, startOfMonth, now);

        // 3) Solutions accepted ALL-TIME
        long solutionsAcceptedAllTime = doubtAnswerRepository
                .countBySolverIdAndAcceptedTrue(userId);

        return new ActivityStatsDto(
                doubtsThisWeek,
                answersThisMonth,
                solutionsAcceptedAllTime
        );
    }
}