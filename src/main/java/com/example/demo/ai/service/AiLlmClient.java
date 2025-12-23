package com.example.demo.ai.service;

import com.example.demo.entity.Doubt;
import com.example.demo.entity.DoubtAnswer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
public class AiLlmClient {

    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build();
    private final ObjectMapper om = new ObjectMapper();

    @Value("${openai.api.key:}")
    private String apiKey;

    @Value("${openai.model:gpt-4o-mini}")
    private String model;

    @Value("${openai.api.base:https://api.openai.com}")
    private String baseUrl;

    public String followUpReply(Doubt doubt, DoubtAnswer accepted, String userText) {
        if (apiKey == null || apiKey.isBlank()) {
            return "⚠️ Premium AI is not configured (missing OPENAI_API_KEY).";
        }

        String prompt = buildPrompt(doubt, accepted, userText);

        try {
            String payload = om.createObjectNode()
                    .put("model", model)
                    .put("instructions",
                            "You are a senior tutor. Explain ONLY using the accepted answer content. " +
                            "Be specific to the steps/code shown. If user is vague, infer the most relevant block " +
                            "from the accepted answer and explain it. End with 1–2 line summary.")
                    .set("input", om.createArrayNode()
                            .add(om.createObjectNode()
                                    .put("role", "user")
                                    .put("content", prompt)))
                    .toString();

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/v1/responses"))
                    .timeout(Duration.ofSeconds(60))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
                return "⚠️ Premium AI failed (" + resp.statusCode() + "): " + safeSnippet(resp.body());
            }

            return extractText(resp.body());
        } catch (Exception e) {
            return "⚠️ Premium AI error: " + e.getClass().getSimpleName() + " - " + e.getMessage();
        }
    }

    private String buildPrompt(Doubt doubt, DoubtAnswer accepted, String userText) {
        String title = safe(doubt.getTitle());
        String desc = safe(doubt.getDescription());
        String code = safe(doubt.getCodeSnippet());

        String ansBody = safe(accepted.getBody());
        String ansImg = safe(findAnyImageUrl(accepted));

        StringBuilder sb = new StringBuilder();
        sb.append("Doubt title: ").append(title).append("\n");
        if (!desc.isBlank()) sb.append("Doubt description: ").append(desc).append("\n");
        if (!code.isBlank()) sb.append("Doubt code:\n").append(code).append("\n");

        sb.append("\n--- ACCEPTED ANSWER (source of truth) ---\n");
        sb.append(ansBody.isBlank() ? "(no text body)" : ansBody).append("\n");
        if (!ansImg.isBlank()) sb.append("Attachment URL: ").append(ansImg).append("\n");

        sb.append("\n--- USER QUESTION ---\n").append(userText == null ? "" : userText.trim()).append("\n");

        return sb.toString().trim();
    }

    private String extractText(String json) throws Exception {
        JsonNode root = om.readTree(json);
        JsonNode ot = root.get("output_text");
        if (ot != null && ot.isTextual() && !ot.asText().isBlank()) return ot.asText();

        JsonNode output = root.get("output");
        if (output != null && output.isArray()) {
            for (JsonNode item : output) {
                if (!"message".equals(item.path("type").asText())) continue;
                JsonNode content = item.path("content");
                if (content != null && content.isArray()) {
                    for (JsonNode c : content) {
                        if ("output_text".equals(c.path("type").asText())) {
                            String t = c.path("text").asText("");
                            if (!t.isBlank()) return t;
                        }
                    }
                }
            }
        }
        return "⚠️ Premium AI returned no text.";
    }

    private String findAnyImageUrl(Object obj) {
        String[] names = {"getImageUrl","getImagePath","getImage","getAttachmentUrl","getPhotoUrl","getFileUrl"};
        for (String n : names) {
            try {
                Method m = obj.getClass().getMethod(n);
                Object v = m.invoke(obj);
                if (v != null) {
                    String s = v.toString().trim();
                    if (!s.isBlank()) return s;
                }
            } catch (Exception ignored) {}
        }
        return "";
    }

    private String safe(String s) { return s == null ? "" : s.trim(); }

    private String safeSnippet(String s) {
        if (s == null) return "";
        s = s.replaceAll("\\s+", " ").trim();
        return s.length() > 220 ? s.substring(0, 220) + "…" : s;
    }
}
