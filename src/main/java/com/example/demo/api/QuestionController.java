package com.example.demo.api;

import com.example.demo.domain.Answer;
import com.example.demo.domain.Claim;
import com.example.demo.domain.Question;
import com.example.demo.repo.AnswerRepository;
import com.example.demo.repo.ClaimRepository;
import com.example.demo.repo.QuestionRepository;
import com.example.demo.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.security.core.Authentication;
import java.time.Instant;

import com.example.demo.domain.dto.quiz.QuizDetailResponse;
import com.example.demo.dto.question.AnswerDto;
import com.example.demo.domain.Quiz;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;


@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionRepository questionRepo;
    private final ClaimRepository claimRepo;
    private final AnswerRepository answerRepo;

    /* ========================
     * DTOs / request payloads
     * ======================== */
    public static record QuestionDto(
        Long id,
        String title,
        String body,
        String askedBy,
        String status,
        Integer maxClaimers,
        Long acceptedAnswerId,
        Instant createdAt,
        long claimedCount,
        String imageUrl,
        String groupName,
        String privateRoom  
) {
    public static QuestionDto of(Question q, long claimed) {
        String roomName = "question/" + q.getId(); 

        return new QuestionDto(
                q.getId(),
                q.getTitle(),
                q.getBody(),
                q.getAskedBy(),
                q.getStatus(),
                q.getMaxClaimers(),
                q.getAcceptedAnswerId(),
                q.getCreatedAt(),
                claimed,
                q.getImageUrl(),
                q.getGroupName(),   
                roomName            
        );
    }
}

    public static record CreateQuestionReq(String title, String body, Integer maxClaimers, String room) {}
    public static record ClaimReq(String user) {}       
    public static record AnswerReq(String text, String user) {} 


    /* ========================
     * Create question (JSON)
     * ======================== */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<QuestionDto> create(@AuthenticationPrincipal User me,
                                              @RequestBody CreateQuestionReq req) {
        var q = new Question();
        q.setTitle(req != null && req.title() != null ? req.title().trim() : "");
        q.setBody(req != null && req.body() != null ? req.body().trim() : "");
        q.setAskedBy((me != null && me.getEmail() != null)
        ? me.getEmail().trim().toLowerCase()
        : "anon");
        q.setStatus("OPEN");
        if (req != null && req.maxClaimers() != null) q.setMaxClaimers(req.maxClaimers());
        q.setGroupName(req != null ? req.room() : null);
    
        Instant now = Instant.now();
        q.setCreatedAt(now);
        questionRepo.save(q);
    
        long claimed = claimRepo.countActiveByQuestionId(q.getId(), now);
        return ResponseEntity.ok(QuestionDto.of(q, claimed));
    }

    /* ========================
     * Create question (multipart + image)
     * ======================== */
@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
@Transactional
public ResponseEntity<QuestionDto> createWithImage(
        @AuthenticationPrincipal User me,
        @RequestParam String title,
        @RequestParam(required = false) String body,
        @RequestParam(defaultValue = "3") Integer maxClaimers,
        @RequestParam(required = false) String room,
        @RequestPart(name = "image", required = false) MultipartFile image
) throws IOException {

    String imageUrl = null;  

    if (image != null && !image.isEmpty()) {

        Path uploadDir = Paths.get("uploads");
        Files.createDirectories(uploadDir);

        String ext = Optional.ofNullable(image.getOriginalFilename())
                .filter(f -> f.contains("."))
                .map(f -> f.substring(f.lastIndexOf('.')))
                .orElse("");

        String filename = UUID.randomUUID() + ext;
        Path target = uploadDir.resolve(filename);

        try (InputStream in = image.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        imageUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/uploads/")
                .path(filename)
                .toUriString();
    }


    var q = new Question();
    q.setTitle(title != null ? title.trim() : "");
    q.setBody(body != null ? body.trim() : "");
    q.setAskedBy((me != null && me.getEmail() != null)
        ? me.getEmail().trim().toLowerCase()
        : "anon");
    q.setStatus("OPEN");
    q.setMaxClaimers(maxClaimers != null ? maxClaimers : 3);
    q.setCreatedAt(Instant.now());
    q.setImageUrl(imageUrl);   // 👈 safe
    q.setGroupName(room);

    questionRepo.save(q);

    long claimed = claimRepo.countActiveByQuestionId(q.getId(), Instant.now());
    return ResponseEntity.ok(QuestionDto.of(q, claimed));
}


    /* ========================
     * List questions (newest first)
     * ======================== */
    @GetMapping
    public List<QuestionDto> list(@RequestParam(required = false, name = "room") String room) {
        var qs = (room == null || room.isBlank())
                ? questionRepo.findAllByOrderByCreatedAtDesc()
                : questionRepo.findByGroupNameOrderByCreatedAtDesc(room);
    
        Instant now = Instant.now();
        return qs.stream()
                .map(q -> QuestionDto.of(q, claimRepo.countActiveByQuestionId(q.getId(), now)))
                .toList();
    }

    /* ========================
     * Get question details
     * ======================== */
    @GetMapping("/{id}")
    public ResponseEntity<QuestionDto> get(@PathVariable Long id) {
        Instant now = Instant.now();
        return questionRepo.findById(id)
                .map(q -> ResponseEntity.ok(
                        QuestionDto.of(q, claimRepo.countActiveByQuestionId(id, now))
                ))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/answers/{answerId}")
public ResponseEntity<Answer> getAnswer(@PathVariable Long answerId) {
    return answerRepo.findById(answerId)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
}
    /* ========================
     * Claim a slot (asker cannot claim)
     * ======================== */
    @PostMapping("/{id}/claim")
    @Transactional
    public ResponseEntity<?> claim(@PathVariable Long id,
                                   @AuthenticationPrincipal User me,
                                   @RequestBody(required = false) ClaimReq req) {
    
        var q = questionRepo.findById(id).orElse(null);
        if (q == null) return ResponseEntity.notFound().build();
    
        if ("LOCKED".equalsIgnoreCase(q.getStatus())) {
            return ResponseEntity.status(409).body("Question is locked.");
        }
        if ("RESOLVED".equalsIgnoreCase(q.getStatus())) {
            return ResponseEntity.status(409).body("Question already resolved.");
        }
    
        String who = (me != null && me.getEmail() != null) ? me.getEmail().toLowerCase() : "anon";
    

        if (who.equalsIgnoreCase(q.getAskedBy())) {
            return ResponseEntity.status(409).body("You can’t claim your own question.");
        }
    
        Instant now = Instant.now();
    

        if (claimRepo.existsByQuestionIdAndUserId(id, who)) {
            long active = claimRepo.countActiveByQuestionId(id, now);
    
            if (!"LOCKED".equalsIgnoreCase(q.getStatus())) {
                q.setStatus(active > 0 ? "CLAIMED" : "OPEN");
                questionRepo.save(q);
            }
    
            return ResponseEntity.ok(QuestionDto.of(q, active));
        }
    

        long activeNow = claimRepo.countActiveByQuestionId(id, now);
        if (q.getMaxClaimers() != null && activeNow >= q.getMaxClaimers()) {
            return ResponseEntity.status(409).body("Claim slots are full.");
        }

        Claim c = new Claim();
        c.setQuestionId(id);
        c.setUserId(who);
        c.setCreatedAt(now);
        c.setExpiresAt(now.plus(Duration.ofMinutes(10)));  
        claimRepo.save(c);
    
        long afterActive = claimRepo.countActiveByQuestionId(id, now);
    
        if (!"LOCKED".equalsIgnoreCase(q.getStatus())) {
            q.setStatus(afterActive > 0 ? "CLAIMED" : "OPEN");
            questionRepo.save(q);
        }
    
        return ResponseEntity.ok(QuestionDto.of(q, afterActive));
    }

    /* ========================
     * Post an answer (JSON)
     * ======================== */
    @PostMapping(path = "/{id}/answers", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<?> answerWithImage(@PathVariable Long id,
                                             @AuthenticationPrincipal User me,
                                             @RequestParam String text,
                                             @RequestPart(name = "image", required = false) MultipartFile image)
            throws IOException {
    
        var q = questionRepo.findById(id).orElse(null);
        if (q == null) return ResponseEntity.notFound().build();
        if ("RESOLVED".equalsIgnoreCase(q.getStatus()) || q.getAcceptedAnswerId() != null) {
            return ResponseEntity.status(409).body("Question already resolved.");
        }
    
        String author = (me != null && me.getEmail() != null) ? me.getEmail() : "anon";
    

        Instant now = Instant.now();
        if (!claimRepo.isActiveClaimer(id, author, now)) {
            return ResponseEntity.status(403).body("Only active claimers can answer.");
        }
    
        var body = (text != null ? text.trim() : "");
        if (body.isBlank() && (image == null || image.isEmpty()))
            return ResponseEntity.badRequest().body("Answer must include text or an image.");
    
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            Path uploadDir = Paths.get("uploads");
            Files.createDirectories(uploadDir);
    
            String ext = Optional.ofNullable(image.getOriginalFilename())
                    .filter(f -> f.contains("."))
    
                    .map(f -> f.substring(f.lastIndexOf('.')))
                    .orElse("");
    
            String filename = UUID.randomUUID() + ext;
            Path target = uploadDir.resolve(filename);
            try (InputStream in = image.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
    
            imageUrl = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/uploads/")
                    .path(filename)
                    .toUriString();
        }
    
        var a = new Answer();
        a.setQuestionId(id);
        a.setAuthor(author);
        a.setBody(body);
        a.setCreatedAt(Instant.now());
        a.setImageUrl(imageUrl);
        answerRepo.save(a);
    
        return ResponseEntity.ok(a);
    }



    /* ========================
     * List answers
     * ======================== */
    @GetMapping("/{id}/answers")
    public List<Answer> answers(@PathVariable Long id) {
        return answerRepo.findByQuestionIdOrderByCreatedAtAsc(id);
    }

/* ========================
 * Who am I? (claimer check)
 * ======================== */
@GetMapping("/{id}/am-i-claimer")
public Map<String, Boolean> amIClaimer(@PathVariable Long id,
                                       @AuthenticationPrincipal User me,
                                       @RequestParam(required = false) String user) {
    String who = (me != null && me.getEmail() != null) ? me.getEmail()
               : (user != null ? user : "anon");
    boolean claimed = claimRepo.existsByQuestionIdAndUserId(id, who);
    return Map.of("claimer", claimed);
}

    /* ========================
     * Accept an answer (only asker)
     * ======================== */
    @PatchMapping("/answers/{answerId}/accept")
    @Transactional
    public ResponseEntity<?> accept(@PathVariable Long answerId,
                                    @AuthenticationPrincipal User me) {
        var a = answerRepo.findById(answerId).orElse(null);
        if (a == null) return ResponseEntity.notFound().build();

        var q = questionRepo.findById(a.getQuestionId()).orElse(null);
        if (q == null) return ResponseEntity.notFound().build();

        String who = (me != null && me.getEmail() != null) ? me.getEmail() : "anon";
        if (!who.equalsIgnoreCase(q.getAskedBy()))
            return ResponseEntity.status(403).body("Only the asker can accept an answer.");

        q.setAcceptedAnswerId(answerId);
        q.setStatus("RESOLVED");   
        questionRepo.save(q);

        return ResponseEntity.ok(Map.of("ok", true, "message", "Answer accepted! Question locked."));
    }

    /* ========================
     * Delete a question (asker only)
     * ======================== */
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteQuestion(@PathVariable Long id,
                                            @AuthenticationPrincipal User me) {
        var q = questionRepo.findById(id).orElse(null);
        if (q == null) return ResponseEntity.notFound().build();

        String who = (me != null && me.getEmail() != null) ? me.getEmail() : "anon";
        if (!who.equalsIgnoreCase(q.getAskedBy())) {
            return ResponseEntity.status(403).body("Only the asker can delete this question.");
        }

        answerRepo.deleteByQuestionId(id);
        claimRepo.deleteByQuestionId(id);
        questionRepo.deleteById(id);

        return ResponseEntity.ok(Map.of("ok", true));
    }

    /* ========================
     * Delete an answer (author only)
     * ======================== */
    @DeleteMapping("/answers/{answerId}")
    @Transactional
    public ResponseEntity<?> deleteAnswer(@PathVariable Long answerId,
                                          @AuthenticationPrincipal User me) {
        var a = answerRepo.findById(answerId).orElse(null);
        if (a == null) return ResponseEntity.notFound().build();

        String who = (me != null && me.getEmail() != null) ? me.getEmail() : "anon";
        if (!who.equalsIgnoreCase(a.getAuthor())) {
            return ResponseEntity.status(403).body("Only the author can delete this answer.");
        }

        answerRepo.deleteById(answerId);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @GetMapping("/{id}/accepted-answer")
public ResponseEntity<AnswerDto> getAcceptedAnswer(@PathVariable Long id) {
    var q = questionRepo.findById(id).orElse(null);
    if (q == null || q.getAcceptedAnswerId() == null)
        return ResponseEntity.notFound().build();

    var a = answerRepo.findById(q.getAcceptedAnswerId()).orElse(null);
    if (a == null)
        return ResponseEntity.notFound().build();

    return ResponseEntity.ok(AnswerDto.of(a));
}
    /* ========================
 * My question rooms (for sidebar)
 * ======================== */
    @GetMapping("/my-rooms")
    public List<QuestionDto> myRooms(@AuthenticationPrincipal User me) {
        if (me == null || me.getEmail() == null) {
            return List.of();
        }
    
        String email = me.getEmail().toLowerCase();
        Instant now = Instant.now();
    
        List<Question> askedByMe =
                questionRepo.findByAskedByOrderByCreatedAtDesc(email);
    
        List<Long> myClaimedQIds =
                claimRepo.findActiveQuestionIdsByUser(email, now);
    
        List<Question> claimedByMe = myClaimedQIds.isEmpty()
                ? List.of()
                : questionRepo.findAllById(myClaimedQIds);
    
        Map<Long, Question> merged = new LinkedHashMap<>();
        askedByMe.forEach(q -> merged.put(q.getId(), q));
        claimedByMe.forEach(q -> merged.put(q.getId(), q));
    
        return merged.values().stream()
                .sorted(Comparator.comparing(Question::getCreatedAt).reversed())
                .map(q -> QuestionDto.of(
                        q,
                        claimRepo.countActiveByQuestionId(q.getId(), now)
                ))
                .toList();
    }
}