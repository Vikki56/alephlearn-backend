package com.example.demo.ai.api;

import com.example.demo.domain.Answer;
import com.example.demo.domain.Question;
import com.example.demo.repo.AnswerRepository;
import com.example.demo.repo.QuestionRepository;
import com.example.demo.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/aiqa")
@RequiredArgsConstructor
public class AiQaController {

    private final QuestionRepository questionRepo;
    private final AnswerRepository answerRepo;

    public static record ExplainReq(Long questionId) {}

    @PostMapping("/explain")
    public ResponseEntity<?> explain(@AuthenticationPrincipal User me,
                                    @RequestBody ExplainReq req) {

        if (me == null) return ResponseEntity.status(401).body("Unauthenticated");
        if (req == null || req.questionId() == null) return ResponseEntity.badRequest().body("questionId required");

        Question q = questionRepo.findById(req.questionId()).orElse(null);
        if (q == null) return ResponseEntity.status(404).body("Question not found");

        if (q.getAcceptedAnswerId() == null) {
            return ResponseEntity.status(409).body("No accepted answer yet");
        }

        Answer acc = answerRepo.findById(q.getAcceptedAnswerId()).orElse(null);
        if (acc == null) return ResponseEntity.status(404).body("Accepted answer not found");

        // ✅ simple local “AI” explanation (abhi ke liye)
        String explanation =
                "✅ Accepted Answer:\n" +
                (acc.getBody() == null ? "" : acc.getBody()) +
                (acc.getImageUrl() != null ? ("\n\n🖼 Attachment: " + acc.getImageUrl()) : "") +
                "\n\n🧠 AI Explanation (simplified):\n" +
                "Is solution ka core idea ye hai ki accepted answer me jo steps diye gaye hain, unko concept-wise break karke samjhaaya jaaye. "
                + "Agar tum chaho to main ise aur beginner-friendly steps me convert kar dunga.";

        return ResponseEntity.ok(Map.of(
                "questionId", q.getId(),
                "acceptedAnswerId", q.getAcceptedAnswerId(),
                "explanation", explanation
        ));
    }
}