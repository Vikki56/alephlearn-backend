package com.example.demo;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    private final MessageRepository repo;

    public MessageController(MessageRepository repo) {
        this.repo = repo;
    }


    public record PinnedDTO(Long id, String user, String text, Long ts, Long replyToId) {}

    @GetMapping("/pinned")
    public ResponseEntity<List<PinnedDTO>> getPinned(@RequestParam String room) {
        var page = PageRequest.of(0, 100);
        var list = repo.findPinnedByRoom(room, page).stream()
                .map(m -> new PinnedDTO(
                        m.getId(),
                        m.getUserName(),
                        m.getText(),
                        m.getTs() != null ? m.getTs().toEpochMilli() : Instant.now().toEpochMilli(),
                        m.getReplyToId()
                ))
                .toList();
        return ResponseEntity.ok(list);
    }
}

