package com.example.demo.ai.service;

import com.example.demo.entity.Doubt;
import com.example.demo.entity.DoubtAnswer;
import org.springframework.stereotype.Component;

@Component
public class AiPromptBuilder {

    public String buildPrompt(Doubt doubt, DoubtAnswer accepted) {
        String title = safe(doubt.getTitle());
        String subject = safe(doubt.getSubject());
        String desc = safe(doubt.getDescription());
        String code = safe(doubt.getCodeSnippet());
        String ans = safe(accepted.getBody());

        StringBuilder sb = new StringBuilder();
        sb.append("You are an expert tutor. Create an easy, step-by-step explanation.\n");
        sb.append("Rules:\n- Explain in simple words.\n- Use numbered steps.\n- End with a short summary.\n\n");
        sb.append("DOUBT SUBJECT: ").append(subject).append("\n");
        sb.append("DOUBT TITLE: ").append(title).append("\n");
        sb.append("DOUBT DESCRIPTION: ").append(desc).append("\n");
        if (!code.isBlank()) sb.append("CODE SNIPPET:\n").append(code).append("\n");
        sb.append("\nACCEPTED ANSWER:\n").append(ans).append("\n");
        return sb.toString().trim();
    }

    private String safe(String s) { return s == null ? "" : s.trim(); }
}
