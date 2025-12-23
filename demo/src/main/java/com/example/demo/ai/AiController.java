package com.example.demo.ai;

import com.example.demo.ai.dto.*;
import com.example.demo.ai.service.AiExplanationService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiExplanationService service;

    public AiController(AiExplanationService service) {
        this.service = service;
    }

    // ✅ Resolved doubts library
    @GetMapping("/resolved")
    public Page<ResolvedDoubtCardDto> resolved(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.listResolved(page, size);
    }

    // ✅ Create AI explanation request
    @PostMapping("/explanations")
    public CreateAiExplanationResponse create(@RequestBody CreateAiExplanationRequest req) {
        return service.createExplanation(req.doubtId());
    }

    // ✅ Get chat history
    @GetMapping("/explanations/{id}/messages")
    public ResponseEntity<?> messages(@PathVariable Long id) {
        return ResponseEntity.ok(service.getMessages(id));
    }

    // ✅ Send user message in AI chat
    @PostMapping("/explanations/{id}/messages")
    public ResponseEntity<?> send(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String text = body.get("text");
        return ResponseEntity.ok(service.sendUserMessage(id, text));
    }
}