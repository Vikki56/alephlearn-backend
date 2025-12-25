package com.example.demo;

import com.example.demo.domain.AcademicProfile;
import com.example.demo.entity.AnswerReply;
import com.example.demo.entity.Doubt;
import com.example.demo.entity.DoubtAnswer;
import com.example.demo.entity.DoubtStatus;
import com.example.demo.repo.DoubtRepository;
import com.example.demo.repository.AcademicProfileRepository;
import com.example.demo.dto.ActivityStatsDto;
import com.example.demo.dto.DoubtDto;
import com.example.demo.service.DoubtService;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doubts")
public class DoubtController {

    private final DoubtService doubtService;
    private final UserRepository userRepository;
    private final AcademicProfileRepository academicProfileRepository;
    private final DoubtRepository doubtRepository;

    public DoubtController(DoubtService doubtService,
                           UserRepository userRepository,
                           AcademicProfileRepository academicProfileRepository,
                           DoubtRepository doubtRepository) {
        this.doubtService = doubtService;
        this.userRepository = userRepository;
        this.academicProfileRepository = academicProfileRepository;
        this.doubtRepository = doubtRepository;
    }

    // -------------------- Create Doubt --------------------
    @PostMapping
    public ResponseEntity<?> createDoubt(@RequestBody DoubtDto dto) {
        try {
            if (dto.getUserId() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "userId must not be null"));
            }

            User asker = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + dto.getUserId()));

            Doubt doubt = new Doubt();
            doubt.setSubject(dto.getSubject());
            doubt.setTitle(dto.getTitle());
            doubt.setDescription(dto.getDescription());
            doubt.setCodeSnippet(dto.getCodeSnippet());
            doubt.setAttachmentUrl(dto.getAttachmentUrl());
            doubt.setTags(dto.getTags());
            doubt.setStatus(DoubtStatus.OPEN);
            doubt.setLikeCount(0);
            doubt.setAnswerCount(0);
            doubt.setViewCount(0);
            doubt.setAsker(asker);

            // 🔥 Yahan se: academic profile se stream info copy karo
            AcademicProfile p = academicProfileRepository.findByUser(asker).orElse(null);
            if (p != null) {
                doubt.setEducationLevel(p.getEducationLevel());
                doubt.setMainStream(p.getMainStream());
                doubt.setSpecialization(p.getSpecialization());
            }

            Doubt saved = doubtService.createDoubt(doubt);
            return ResponseEntity.ok(saved);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // -------------------- Activity Stats --------------------
    @GetMapping("/activity")
    public ActivityStatsDto getActivity(@RequestParam Long userId) {
        return doubtService.getUserActivity(userId);
    }

    // -------------------- Update Doubt --------------------
    @PutMapping("/{id}")
    public ResponseEntity<Doubt> updateDoubt(
            @PathVariable Long id,
            @RequestBody Doubt updated) {

        Doubt saved = doubtService.updateDoubt(id, updated);
        return ResponseEntity.ok(saved);
    }

    // -------------------- List Doubts (STREAM-BASED) --------------------
    @GetMapping
    public Page<Doubt> listDoubts(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(required = false) String subject,
                                  @RequestParam(required = false) DoubtStatus status,
                                  @RequestParam(defaultValue = "LATEST") String sort,
                                  @RequestParam(required = false) Long userId) {

        Sort sortSpec;
        switch (sort.toUpperCase()) {
            case "MOST_LIKED" -> sortSpec = Sort.by(Sort.Direction.DESC, "likeCount");
            case "MOST_VIEWED" -> sortSpec = Sort.by(Sort.Direction.DESC, "viewCount");
            default -> sortSpec = Sort.by(Sort.Direction.DESC, "createdAt");
        }
        Pageable pageable = PageRequest.of(page, size, sortSpec);

        if (userId != null) {
            User u = userRepository.findById(userId).orElse(null);
            if (u != null) {
                AcademicProfile p = academicProfileRepository.findByUser(u).orElse(null);
                if (p != null &&
                        p.getEducationLevel() != null &&
                        p.getMainStream() != null &&
                        p.getSpecialization() != null) {

                    String level = p.getEducationLevel();
                    String stream = p.getMainStream();
                    String spec = p.getSpecialization();

                    // 4 cases: subject / status ke combinations
                    if (subject != null && !subject.isBlank() && status != null) {
                        return doubtRepository
                                .findByEducationLevelIgnoreCaseAndMainStreamIgnoreCaseAndSpecializationIgnoreCaseAndSubjectIgnoreCaseAndStatus(
                                        level, stream, spec, subject, status, pageable);
                    } else if (subject != null && !subject.isBlank()) {
                        return doubtRepository
                                .findByEducationLevelIgnoreCaseAndMainStreamIgnoreCaseAndSpecializationIgnoreCaseAndSubjectIgnoreCase(
                                        level, stream, spec, subject, pageable);
                    } else if (status != null) {
                        return doubtRepository
                                .findByEducationLevelIgnoreCaseAndMainStreamIgnoreCaseAndSpecializationIgnoreCaseAndStatus(
                                        level, stream, spec, status, pageable);
                    } else {
                        return doubtRepository
                                .findByEducationLevelIgnoreCaseAndMainStreamIgnoreCaseAndSpecializationIgnoreCase(
                                        level, stream, spec, pageable);
                    }
                }
            }
        }

        return doubtService.listDoubts(subject, status, sort, page, size);
    }

    // -------------------- Get Single Doubt --------------------
    @GetMapping("/{id}")
    public Doubt getDoubt(@PathVariable Long id,
                          @RequestParam(required = false) Long userId) {
        return doubtService.getDoubt(id, userId);
    }

    // -------------------- Like / Unlike Doubt --------------------
    @PostMapping("/{id}/like")
    public Map<String, Long> likeDoubt(@PathVariable Long id,
                                       @RequestBody LikeRequest req) {
        return doubtService.likeDoubt(id, req.userId());
    }

    @DeleteMapping("/{id}/like")
    public Map<String, Long> unlikeDoubt(@PathVariable Long id,
                                         @RequestBody LikeRequest req) {
        return doubtService.unlikeDoubt(id, req.userId());
    }

    public record LikeRequest(Long userId) {}
    public record ReplyRequest(Long userId, String text) {}

    // -------------------- Answers --------------------
    @GetMapping("/{id}/answers")
    public List<DoubtAnswer> getAnswers(@PathVariable Long id) {
        return doubtService.getAnswersForDoubt(id);
    }

    @PostMapping("/{id}/answers")
    public DoubtAnswer addAnswer(@PathVariable Long id,
                                 @RequestBody AnswerRequest req) {
        return doubtService.addAnswer(id, req.userId(), req.body(), req.attachmentUrl());
    }

    @PutMapping("/answers/{answerId}")
    public DoubtAnswer editAnswer(@PathVariable Long answerId,
                                  @RequestBody AnswerRequest req) {
        return doubtService.editAnswer(answerId, req.userId(), req.body(), req.attachmentUrl());
    }

    @GetMapping("/ping")
    public String ping() {
        return "doubts-controller-alive";
    }

    @PostMapping("/answers/{answerId}/accept")
    public DoubtAnswer acceptAnswer(@PathVariable Long answerId,
                                    @RequestBody AcceptRequest req) {
        return doubtService.acceptAnswer(answerId, req.userId());
    }

    public record AnswerRequest(Long userId, String body, String attachmentUrl) {}
    public record AcceptRequest(Long userId) {}

    // -------------------- My Doubts --------------------
    @GetMapping("/mine")
    public Page<Doubt> myDoubts(@RequestParam Long userId,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size) {
        return doubtService.listMyDoubts(userId, page, size);
    }

    // -------------------- Replies --------------------
    @PostMapping("/answers/{answerId}/replies")
    public AnswerReply replyToAnswer(@PathVariable Long answerId,
                                     @RequestBody ReplyRequest req) {
        return doubtService.addReply(answerId, req.userId(), req.text());
    }

    @GetMapping("/answers/{answerId}/replies")
    public List<AnswerReply> getRepliesForAnswer(@PathVariable Long answerId) {
        return doubtService.getReplies(answerId);
    }

    @PostMapping("/answers/{answerId}/liked-event")
    public void answerLikedEvent(@PathVariable Long answerId,
                                 @RequestBody LikeRequest req) {
        doubtService.notifyAnswerLiked(answerId, req.userId());
    }
}