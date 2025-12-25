package com.example.demo;

import com.example.demo.domain.AcademicProfile;
import com.example.demo.domain.Claim;
import com.example.demo.domain.Question;
import com.example.demo.domain.Room;
import com.example.demo.domain.academic.StreamKeyUtil;
import com.example.demo.entity.NotificationType;
import com.example.demo.repo.ClaimRepository;
import com.example.demo.repo.QuestionRepository;
import com.example.demo.repository.AcademicProfileRepository;
import com.example.demo.security.JwtService;
import com.example.demo.service.NotificationService;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.repo.RoomKickRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomsController {

    private final RoomRepository rooms;
    private final AcademicProfileRepository academicProfiles;
    private final UserRepository users;
    private final JwtService jwt;
    private final QuestionRepository questions;
    private final ClaimRepository claims;
    private final NotificationService notificationService;
    private final RoomKickRepository kicks;
    private static final List<String> DEFAULT_ROOM_TITLES = List.of(
            "General Chat",
            "Quizzes"
    );

    // Automatically create default 2 rooms for each stream
    private void ensureDefaultRooms(String streamKey, AcademicProfile p) {
        for (String title : DEFAULT_ROOM_TITLES) {

            String slug = Slugger.slugify(title);

            if (!rooms.existsBySubjectAndSlug(streamKey, slug)) {

                Room r = Room.builder()
                        .subject(streamKey)
                        .slug(slug)
                        .title(title)
                        .visibility(Room.Visibility.PUBLIC)
                        .memberCount(0)
                        .createdAt(Instant.now())
                        .lastActivity(Instant.now())
                        .build();

                if (p != null) {
                    r.setEducationLevel(p.getEducationLevel());
                    r.setMainStream(p.getMainStream());
                    r.setSpecialization(p.getSpecialization());
                }

                rooms.save(r);
            }
        }
    }

    // ============================
    // CLAIM QUESTION (15 min expiry)
    // ============================
    @PostMapping("/claim/{questionId}")
    public ResponseEntity<?> claimQuestion(@PathVariable Long questionId,
                                          @RequestHeader("Authorization") String auth) {

        User user = jwt.extractUser(auth, users);

        String email = (user != null && user.getEmail() != null)
                ? user.getEmail().trim().toLowerCase()
                : "";

        if (email.isBlank()) {
            return ResponseEntity.badRequest().body("Invalid user email");
        }

        Question q = questions.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
// 🚫 kicked user cannot claim this room
if (kicks.existsByQuestionIdAndKickedUserEmailIgnoreCase(questionId, email)) {
    return ResponseEntity.status(403).body("You were removed from this room");
}
        // ❌ same user cannot claim again EVER
        if (claims.existsByQuestionIdAndUserId(questionId, email)) {
            return ResponseEntity.badRequest().body("You already claimed this question");
        }


        Integer max = (q.getMaxClaimers() == null) ? 3 : q.getMaxClaimers();
        long activeClaims = claims.countActiveByQuestionId(questionId, Instant.now());
        if (activeClaims >= max) {
            return ResponseEntity.badRequest().body("Claim slots full");
        }

        Claim c = new Claim();
        c.setQuestionId(questionId);
        c.setUserId(email);
        c.setExpiresAt(Instant.now().plusSeconds(15 * 60)); 
        claims.save(c);


        String askerEmail = (q.getAskedBy() == null) ? "" : q.getAskedBy().trim();
        String display = (user.getName() == null || user.getName().isBlank()) ? email : user.getName();

        if (!askerEmail.isBlank()) {
            users.findByEmailIgnoreCase(askerEmail).ifPresent(asker -> {
                notificationService.notify(
                        asker,
                        NotificationType.CLAIMED,
                        display + " claimed your question",
                        questionId,
                        null
                );
            });
        }

        return ResponseEntity.ok(java.util.Map.of(
            "status", "CLAIMED",
            "questionId", questionId
    ));
    }

    // ============================
    // MARK JOINED (lock claim expiry)
    // ============================
    @PostMapping("/claim/{questionId}/joined")
    public ResponseEntity<?> joinedRoom(@PathVariable Long questionId,
                                        @RequestHeader("Authorization") String auth) {
    
        User user = jwt.extractUser(auth, users);
    
        String email = (user != null && user.getEmail() != null)
                ? user.getEmail().trim().toLowerCase()
                : "";
    
        if (email.isBlank()) {
            return ResponseEntity.badRequest().body("Invalid user email");
        }
    

        claims.findFirstByQuestionIdAndUserId(questionId, email)
                .ifPresent(c -> {
                    c.setExpiresAt(null);
                    claims.save(c);
                });
    

        Question q = questions.findById(questionId).orElse(null);
        if (q != null) {
            String askerEmail = (q.getAskedBy() == null) ? "" : q.getAskedBy().trim();
            String display = (user.getName() == null || user.getName().isBlank()) ? email : user.getName();
    
            if (!askerEmail.isBlank()) {
                users.findByEmailIgnoreCase(askerEmail).ifPresent(asker -> {
                    notificationService.notify(
                            asker,
                            NotificationType.CLAIMED,
                            display + " joined the solving room",
                            questionId,
                            null
                    );
                });
            }
        }
    
        return ResponseEntity.ok(java.util.Map.of(
            "status", "JOINED",
            "questionId", questionId
    ));
    }

    // ============================
    // LIST ROOMS (public + my private doubt rooms)
    // ============================
    @GetMapping
    public List<Room> list(@RequestParam String subject,
                           @RequestParam(required = false) String q,
                           @RequestHeader("Authorization") String authHeader) {

        User current = jwt.extractUser(authHeader, users);
        AcademicProfile p = academicProfiles.findByUser(current).orElse(null);

        String query = (q == null) ? "" : q.trim();
        String streamKey = StreamKeyUtil.forProfile(p, subject);

        String myEmail = "";
        if (current != null && current.getEmail() != null) {
            myEmail = current.getEmail().trim().toLowerCase();
        }


        ensureDefaultRooms(streamKey, p);


        List<Room> base = rooms.search(streamKey, Room.Visibility.PUBLIC, query);

 
        List<Room> doubtRooms = createOrLoadDoubtRoomsForStream(streamKey, query, myEmail);

        base.addAll(doubtRooms);
        return base;
    }

    // ============================
    // CREATE ROOM (optional)
    // ============================
    public record CreateReq(String subject, String title, String visibility) {}

    @PostMapping
    public Room create(@RequestBody CreateReq req,
                       @RequestHeader("Authorization") String authHeader) {

        User current = jwt.extractUser(authHeader, users);
        AcademicProfile p = academicProfiles.findByUser(current).orElse(null);

        String subject = StreamKeyUtil.forProfile(p, req.subject());
        String title = (req.title() == null || req.title().isBlank()) ? "New Room" : req.title().trim();
        String slug = Slugger.slugify(title);

        Room.Visibility vis = Room.Visibility.PUBLIC;
        if (req.visibility() != null) {
            try { vis = Room.Visibility.valueOf(req.visibility().toUpperCase()); } catch (Exception ignored) {}
        }

        Room exists = rooms.findBySubjectAndSlug(subject, slug).orElse(null);
        if (exists != null) return exists;

        Room room = Room.builder()
                .subject(subject)
                .title(title)
                .slug(slug)
                .visibility(vis)
                .memberCount(0)
                .lastActivity(Instant.now())
                .build();

        if (p != null) {
            room.setEducationLevel(p.getEducationLevel());
            room.setMainStream(p.getMainStream());
            room.setSpecialization(p.getSpecialization());
        }

        return rooms.save(room);
    }

    // ============================
    // PRIVATE DOUBT ROOMS LOADER
    // ============================
    private List<Room> createOrLoadDoubtRoomsForStream(String streamKey,
                                                      String query,
                                                      String myEmailRaw) {

        String q = (query == null) ? "" : query.trim().toLowerCase();
        String me = (myEmailRaw == null) ? "" : myEmailRaw.trim().toLowerCase();

        List<Room> result = new ArrayList<>();
        if (me.isEmpty()) return result;

        // 1) Questions asked by me
        List<Question> askedByMe = questions.findByAskedByOrderByCreatedAtDesc(me);

        // 2) Questions jinke liye maine ACTIVE claim kiya hai
        List<Long> claimedIds = claims.findActiveQuestionIdsByUser(me, Instant.now());
        List<Question> claimedByMe = claimedIds.isEmpty()
                ? List.of()
                : questions.findAllById(claimedIds);

        // 3) Merge distinct by id
        Map<Long, Question> byId = new LinkedHashMap<>();
        for (Question qn : askedByMe) byId.put(qn.getId(), qn);
        for (Question qn : claimedByMe) byId.put(qn.getId(), qn);

        for (Question question : byId.values()) {

            String slug = "doubt-q-" + question.getId(); // must match frontend getDoubtRoomId

            Room room = rooms.findBySubjectAndSlug(streamKey, slug).orElse(null);
            if (room == null) {
                room = Room.builder()
                        .subject(streamKey)
                        .slug(slug)
                        .title(question.getTitle() != null ? question.getTitle() : "Doubt")
                        .visibility(Room.Visibility.PRIVATE)
                        .memberCount(0)
                        .createdAt(question.getCreatedAt())
                        .lastActivity(question.getCreatedAt())
                        .build();
                rooms.save(room);
            }

            String title = (room.getTitle() == null) ? "" : room.getTitle().toLowerCase();
            if (q.isEmpty() || title.contains(q)) {
                result.add(room);
            }
        }

        return result;
    }
}