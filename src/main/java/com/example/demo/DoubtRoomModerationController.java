package com.example.demo;

import com.example.demo.domain.Question;
import com.example.demo.entity.RoomKick;
import com.example.demo.entity.RoomKickReason;
import com.example.demo.entity.NotificationType;
import com.example.demo.repo.QuestionRepository;
import com.example.demo.repo.RoomKickRepository;
import com.example.demo.security.JwtService;
import com.example.demo.service.NotificationService;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.repo.ClaimRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doubt-rooms")
@RequiredArgsConstructor
public class DoubtRoomModerationController {

    private final ChatHandler chatHandler;
    private final RoomKickRepository kicks;
    private final QuestionRepository questions;
    private final UserRepository users;
    private final JwtService jwt;
    private final NotificationService notificationService;
    private final ClaimRepository claims;
    public record KickReq(String userEmail, RoomKickReason reason, String note, String proofUrl, String subject) {}

    // ✅ Active members (online)
    @GetMapping("/{questionId}/members")
    public ResponseEntity<?> members(@PathVariable Long questionId,
                                    @RequestParam String subject,
                                    @RequestHeader("Authorization") String auth) {

        User me = jwt.extractUser(auth, users);
        Question q = questions.findById(questionId).orElse(null);
        if (q == null) return ResponseEntity.notFound().build();

        String askerEmail = q.getAskedBy() == null ? "" : q.getAskedBy().trim().toLowerCase();
        if (!askerEmail.equalsIgnoreCase(me.getEmail())) {
            return ResponseEntity.status(403).body("Only asker can view members");
        }

        String room = subject + "/doubt-q-" + questionId;
        List<String> active = chatHandler.activeMembers(room);
        return ResponseEntity.ok(active);
    }

    // ✅ Kick with reason + proof
    @PostMapping("/{questionId}/kick")
    public ResponseEntity<?> kick(@PathVariable Long questionId,
                                 @RequestBody KickReq req,
                                 @RequestHeader("Authorization") String auth) {

        if (req == null || req.userEmail() == null || req.userEmail().isBlank())
            return ResponseEntity.badRequest().body("userEmail required");
        if (req.reason() == null)
            return ResponseEntity.badRequest().body("reason required");
        if (req.note() == null || req.note().trim().length() < 10)
            return ResponseEntity.badRequest().body("note min 10 chars required");
        if (req.subject() == null || req.subject().isBlank())
            return ResponseEntity.badRequest().body("subject required");

        User me = jwt.extractUser(auth, users);
        Question q = questions.findById(questionId).orElse(null);
        if (q == null) return ResponseEntity.notFound().build();

        String askerEmail = q.getAskedBy() == null ? "" : q.getAskedBy().trim().toLowerCase();
        if (!askerEmail.equalsIgnoreCase(me.getEmail())) {
            return ResponseEntity.status(403).body("Only asker can kick");
        }

        String kicked = req.userEmail().trim().toLowerCase();
        String room = req.subject().trim() + "/doubt-q-" + questionId;

        // store kick
        RoomKick k = new RoomKick();
        k.setQuestionId(questionId);
        k.setRoom(room);
        k.setKickedUserEmail(kicked);
        k.setKickedByEmail(me.getEmail().trim().toLowerCase());
        k.setReason(req.reason());
        k.setNote(req.note().trim());
        k.setProofUrl(req.proofUrl());
        kicks.save(k);
// ✅ 1) Claim slot free (expire claim immediately)
claims.findFirstByQuestionIdAndUserId(questionId, kicked)
        .ifPresent(c -> {
            c.setExpiresAt(Instant.now()); // active nahi rahega => slot free
            claims.save(c);
        });

// ✅ 2) WS se turant nikaal do (force disconnect)
chatHandler.forceDisconnectUserFromRoom(room, kicked);
        // notify kicked user (if exists)
        users.findByEmailIgnoreCase(kicked).ifPresent(u -> {
            notificationService.notify(
                u,
                NotificationType.CLAIMED,
                "You were removed from the solving room. Reason: " + req.reason().name(),
                questionId,
                null
            );
        });

        return ResponseEntity.ok(Map.of(
                "status", "KICKED",
                "questionId", questionId,
                "userEmail", kicked
        ));
    }
}