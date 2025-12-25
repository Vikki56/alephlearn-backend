package com.example.demo.ai.service;

import com.example.demo.ai.dto.AiChatMessageDto;
import com.example.demo.ai.dto.CreateAiExplanationResponse;
import com.example.demo.ai.dto.ResolvedDoubtCardDto;
import com.example.demo.ai.entity.AiChatMessage;
import com.example.demo.ai.entity.AiExplanation;
import com.example.demo.ai.repo.AiChatMessageRepository;
import com.example.demo.ai.repo.AiExplanationRepository;
import com.example.demo.entity.Doubt;
import com.example.demo.entity.DoubtAnswer;
import com.example.demo.entity.DoubtStatus;
import com.example.demo.repo.DoubtRepository;
import com.example.demo.security.AuthUser;
import com.example.demo.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AiExplanationService {

    private final DoubtRepository doubtRepository;
    private final AiExplanationRepository explanationRepo;
    private final AiChatMessageRepository msgRepo;
    private final AiPromptBuilder promptBuilder;
    private final AiLocalGenerator localGen;
    private final AiEntitlementService entitlement;
    private final AiLlmClient llm;
    private final UserStreamService userStreamService;
    public AiExplanationService(DoubtRepository doubtRepository,
                               AiExplanationRepository explanationRepo,
                               AiChatMessageRepository msgRepo,
                               AiPromptBuilder promptBuilder,
                               AiLocalGenerator localGen,
                               AiEntitlementService entitlement,
                               AiLlmClient llm,
                               UserStreamService userStreamService) {
        this.doubtRepository = doubtRepository;
        this.explanationRepo = explanationRepo;
        this.msgRepo = msgRepo;
        this.promptBuilder = promptBuilder;
        this.localGen = localGen;
        this.entitlement = entitlement;
        this.llm = llm;
        this.userStreamService = userStreamService;
    }
    private boolean eq(String a, String b){
        return (a==null?"":a.trim()).equalsIgnoreCase(b==null?"":b.trim());
    }

    public Page<ResolvedDoubtCardDto> listResolved(int page, int size) {
        var pageable = PageRequest.of(page, Math.min(Math.max(size, 1), 50));
    
        var sk = userStreamService.currentStream();
    
        return doubtRepository
                .findByEducationLevelIgnoreCaseAndMainStreamIgnoreCaseAndSpecializationIgnoreCaseAndStatusAndAcceptedAnswerIsNotNull(
                        sk.educationLevel(),
                        sk.mainStream(),
                        sk.specialization(),
                        DoubtStatus.RESOLVED,
                        pageable
                )
                .map(d -> new ResolvedDoubtCardDto(d.getId(), d.getSubject(), d.getTitle(), d.getUpdatedAt()));
    }


    public CreateAiExplanationResponse createExplanation(Long doubtId) {
        User current = AuthUser.current();
        if (current == null) throw new RuntimeException("Unauthenticated");
        if (doubtId == null) throw new RuntimeException("doubtId required");

        var existing = explanationRepo.findTopByUserIdAndDoubtIdOrderByCreatedAtDesc(current.getId(), doubtId);
        if (existing.isPresent()) {
            AiExplanation e = existing.get();
            return new CreateAiExplanationResponse(e.getId(), e.getStatus().name());
        }

        Doubt doubt = doubtRepository.findById(doubtId)
                .orElseThrow(() -> new RuntimeException("Doubt not found"));
                var sk = userStreamService.currentStream();

if (!eq(doubt.getEducationLevel(), sk.educationLevel()) ||
    !eq(doubt.getMainStream(), sk.mainStream()) ||
    !eq(doubt.getSpecialization(), sk.specialization())) {
    throw new RuntimeException("AI explanation allowed only for doubts in your stream");
}

        if (doubt.getStatus() != DoubtStatus.RESOLVED || doubt.getAcceptedAnswer() == null) {
            throw new RuntimeException("AI explanation allowed only for RESOLVED doubts with accepted answer");
        }

        boolean premium = entitlement.isPremium(current);
        if (!premium) enforceMonthlyFreeQuota(current.getId(), 3);

        DoubtAnswer accepted = doubt.getAcceptedAnswer();

        AiExplanation expl = new AiExplanation();
        expl.setUser(current);            
        expl.setDoubt(doubt);
        expl.setAcceptedAnswer(accepted);
        expl.setStatus(AiExplanation.Status.PENDING);

        AiExplanation saved = explanationRepo.save(expl);

        @SuppressWarnings("unused")
        String prompt = promptBuilder.buildPrompt(doubt, accepted);

        String explanationText = localGen.generate(doubt, accepted);

        saved.setFinalExplanation(explanationText);
        saved.setStatus(AiExplanation.Status.DONE);
        saved = explanationRepo.save(saved);

        AiChatMessage aiMsg = new AiChatMessage();
        aiMsg.setExplanation(saved);
        aiMsg.setSender(AiChatMessage.Sender.AI);
        aiMsg.setMessage(explanationText);
        msgRepo.save(aiMsg);

        return new CreateAiExplanationResponse(saved.getId(), saved.getStatus().name());

        
    }

    public List<AiChatMessageDto> getMessages(Long explanationId) {
        User current = AuthUser.current();
        if (current == null) throw new RuntimeException("Unauthenticated");

        AiExplanation expl = explanationRepo.findById(explanationId)
                .orElseThrow(() -> new RuntimeException("Explanation not found"));

        if (!expl.getUser().getId().equals(current.getId())) throw new RuntimeException("Forbidden");

        return msgRepo.findByExplanationOrderByCreatedAtAsc(expl)
                .stream()
                .map(m -> new AiChatMessageDto(m.getId(), m.getSender().name(), m.getMessage(), m.getCreatedAt()))
                .toList();
    }

    public AiChatMessageDto sendUserMessage(Long explanationId, String text) {
        User current = AuthUser.current();
        if (current == null) throw new RuntimeException("Unauthenticated");

        AiExplanation expl = explanationRepo.findById(explanationId)
                .orElseThrow(() -> new RuntimeException("Explanation not found"));

        if (!expl.getUser().getId().equals(current.getId())) throw new RuntimeException("Forbidden");
        if (text == null || text.isBlank()) throw new RuntimeException("Message cannot be blank");

        AiChatMessage um = new AiChatMessage();
        um.setExplanation(expl);
        um.setSender(AiChatMessage.Sender.USER);
        um.setMessage(text.trim());
        msgRepo.save(um);

        boolean premium = entitlement.isPremium(current);
        String reply = premium
                ? llm.followUpReply(expl.getDoubt(), expl.getAcceptedAnswer(), text.trim())
                : (" Here’s a simpler view based on the accepted answer:\n\n" + localGen.generate(expl.getDoubt(), expl.getAcceptedAnswer()));

        AiChatMessage am = new AiChatMessage();
        am.setExplanation(expl);
        am.setSender(AiChatMessage.Sender.AI);
        am.setMessage(reply);
        msgRepo.save(am);

        expl.setFinalExplanation(reply);
        expl.setStatus(AiExplanation.Status.DONE);
        explanationRepo.save(expl);

        return new AiChatMessageDto(um.getId(), um.getSender().name(), um.getMessage(), um.getCreatedAt());
    }

    private void enforceMonthlyFreeQuota(Long userId, int limitPerMonth) {
        LocalDate now = LocalDate.now();
        LocalDateTime start = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = LocalDateTime.now();
        long used = explanationRepo.countByUserIdAndCreatedAtBetween(userId, start, end);
        if (used >= limitPerMonth) throw new RuntimeException("Free limit reached: " + limitPerMonth + " AI explanations per month");
    }
}
