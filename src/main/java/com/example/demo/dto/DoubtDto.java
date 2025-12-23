package com.example.demo.dto;

import java.util.List;

public class DoubtDto {

    private String subject;
    private String title;
    private String description;
    private String codeSnippet;
    private String attachmentUrl;
    private Long userId;
    private List<String> tags;

    // --- getters & setters ---

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCodeSnippet() {
        return codeSnippet;
    }

    public void setCodeSnippet(String codeSnippet) {
        this.codeSnippet = codeSnippet;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    public Long getUserId() { return userId; }      // 🔥 ADD GETTER
    public void setUserId(Long userId) { this.userId = userId; } 
}