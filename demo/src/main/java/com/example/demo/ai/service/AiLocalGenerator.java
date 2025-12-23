package com.example.demo.ai.service;

import com.example.demo.entity.Doubt;
import com.example.demo.entity.DoubtAnswer;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class AiLocalGenerator {

    public String generate(Doubt doubt, DoubtAnswer accepted) {
        String title = safe(doubt == null ? null : doubt.getTitle());
        String body = safe(accepted == null ? null : accepted.getBody());
        String img = safe(findAnyImageUrl(accepted));

        StringBuilder out = new StringBuilder();
        out.append("### Step-by-step Explanation (from Accepted Answer)\n\n");
        out.append("**Doubt:** ").append(title.isBlank() ? "(untitled)" : title).append("\n\n");

        if (!img.isBlank()) {
            // UI usually renders either direct <img> or markdown image, so provide both.
            out.append("🖼️ Attachment: ").append(img).append("\n\n");
            out.append("![](").append(img).append(")\n\n");
        }

        if (body.isBlank()) {
            out.append("(Accepted answer has no text body.)\n");
        } else {
            String[] lines = body.split("\\r?\\n");
            int step = 1;
            for (String ln : lines) {
                String t = ln == null ? "" : ln.trim();
                if (t.isBlank()) continue;

                // Keep bullet/numbered lines as-is, otherwise auto-number.
                if (t.startsWith("-") || t.startsWith("*") || t.matches("^\\d+\\..*")) {
                    out.append(t).append("\n");
                } else {
                    out.append(step++).append(". ").append(t).append("\n");
                }
            }
        }

        out.append("\n---\n");
        out.append("**Summary:** Above explanation is derived from the accepted solution only. Ask me to simplify or add an example.\n");
        return out.toString().trim();
    }

    private String findAnyImageUrl(Object obj) {
        if (obj == null) return "";
        String[] names = {"getImageUrl","getImagePath","getImage","getAttachmentUrl","getPhotoUrl","getFileUrl","getFilePath","getAttachment"};
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

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
