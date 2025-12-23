package com.example.demo;
import java.time.Instant;
import com.example.demo.entity.ChatMessage;
import com.example.demo.repository.ChatMessageRepository;
// import com.example.demo.dto.ChatMessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.demo.repo.RoomKickRepository;

@CrossOrigin(origins = {"http://127.0.0.1:5500","http://localhost:5500"})
@RestController
@RequestMapping("/api/chat")
public class ChatRestController {

    private final MessageRepository repo;
    private final RoomKickRepository kicks;
    public ChatRestController(MessageRepository repo, RoomKickRepository kicks) {
        this.repo = repo;
        this.kicks = kicks;
    }

    // --- Request DTOs ---
    public record SaveReq(String subject, String slug, String text, long ts,
                          String clientId, String userName, String userEmail) {}
    public record EditReq(String text, String clientId) {}
    public record DeleteReq(String clientId) {}
    public record PinReq(Boolean pinned) {}

    // -------------------------------
    // HISTORY
    // -------------------------------
    @GetMapping("/history/{subject}/{slug}")
    public List<Message> historyParts(@PathVariable String subject,
                                     @PathVariable String slug,
                                     @RequestParam(defaultValue = "50") int limit,
                                     @RequestParam(required = false) String userEmail) {
    
        String room = subject + "/" + slug;
        Long qid = qidFromRoom(room);
    
        if (qid != null && userEmail != null && isKicked(qid, userEmail)) {
            return List.of();
        }
    
        int size = Math.max(1, Math.min(limit, 200));
        return repo.findRecentByRoom(room, PageRequest.of(0, size));
    }

    @GetMapping("/history/{room:.+}")
    public List<Message> historyRoom(@PathVariable String room,
                                    @RequestParam(defaultValue = "50") int limit,
                                    @RequestParam(required = false) String userEmail) {
    
        Long qid = qidFromRoom(room);
    
        if (qid != null && userEmail != null && isKicked(qid, userEmail)) {
            return List.of();
        }
    
        int size = Math.max(1, Math.min(limit, 200));
        return repo.findRecentByRoom(room, PageRequest.of(0, size));
    }

    // -------------------------------
    // PINNED
    // -------------------------------
    @GetMapping("/pinned/{subject}/{slug}")
    public List<Message> listPinnedParts(@PathVariable String subject,
                                         @PathVariable String slug,
                                         @RequestParam(defaultValue = "5") int limit) {
        String room = subject + "/" + slug;
        int size = Math.max(1, Math.min(limit, 20));
        return repo.findPinnedByRoom(room, PageRequest.of(0, size));
    }

    @GetMapping("/pinned/{room:.+}")
    public List<Message> listPinnedRoom(@PathVariable String room,
                                        @RequestParam(defaultValue = "5") int limit) {
        int size = Math.max(1, Math.min(limit, 20));
        return repo.findPinnedByRoom(room, PageRequest.of(0, size));
    }

    // -------------------------------
    // EDIT / DELETE / PIN
    // -------------------------------
    @PatchMapping("/message/{id}")
    public ResponseEntity<?> edit(@PathVariable Long id, @RequestBody EditReq req) {
        if (req == null || req.clientId() == null || req.clientId().isBlank())
            return ResponseEntity.badRequest().body("clientId is required");

        return repo.findById(id).map(m -> {
            if (!req.clientId().equals(m.getClientId())) return ResponseEntity.status(403).body("Forbidden");
            if (m.isDeleted()) return ResponseEntity.status(409).body("Message already deleted");
            m.setText(req.text() == null ? "" : req.text().trim());
            m.setEditedAt(Instant.now());
            repo.save(m);
            return ResponseEntity.ok().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/message/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, @RequestBody DeleteReq req) {
        if (req == null || req.clientId() == null || req.clientId().isBlank())
            return ResponseEntity.badRequest().body("clientId is required");

        return repo.findById(id).map(m -> {
            if (!req.clientId().equals(m.getClientId())) return ResponseEntity.status(403).body("Forbidden");
            if (m.isDeleted()) return ResponseEntity.noContent().build();
            m.setDeleted(true);
            m.setText("");
            repo.save(m);
            return ResponseEntity.noContent().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/message/{id}/pin")
    public ResponseEntity<?> pin(@PathVariable Long id, @RequestBody(required = false) PinReq req) {
        boolean shouldPin = (req == null || req.pinned() == null) ? true : req.pinned();
        return repo.findById(id).map(m -> {
            m.setPinned(shouldPin);
            repo.save(m);
            return ResponseEntity.ok().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // -------------------------------
    // SAVE MESSAGE
    // -------------------------------
    @PostMapping("/message")
    public ResponseEntity<?> saveChatMessage(@RequestBody SaveReq req) {
    
        String room = req.subject() + "/" + req.slug();
        Long qid = qidFromRoom(room);
    
        if (qid != null && isKicked(qid, req.userEmail())) {
            return ResponseEntity.status(403).body("You were removed from this room");
        }
    
        try {
            if (isBlank(req.subject()) || isBlank(req.slug()) || isBlank(req.text())) {
                return ResponseEntity.badRequest().body("subject, slug, and text are required");
            }
    
            Message m = new Message();
            m.setRoom(room);
            m.setText(req.text().trim());
            m.setTs(req.ts());
            m.setClientId(req.clientId());
            m.setUserName(req.userName());
            m.setUserEmail(req.userEmail());
            m.setDeleted(false);
            m.setPinned(false);

            Message saved = repo.save(m);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to save message: " + e.getMessage());
        }
    }

    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    private boolean isKicked(Long qid, String email) {
        if (qid == null || email == null) return false;
        return kicks.existsByQuestionIdAndKickedUserEmailIgnoreCase(qid, email.trim().toLowerCase());
    }
    
    private Long qidFromRoom(String room) {
        if (room == null) return null;
        String slug = room.contains("/") ? room.substring(room.indexOf("/") + 1) : room;
        if (!slug.startsWith("doubt-q-")) return null;
        try { return Long.parseLong(slug.replace("doubt-q-", "").replaceAll("[^0-9]", "")); }
        catch (Exception e) { return null; }
    }
}