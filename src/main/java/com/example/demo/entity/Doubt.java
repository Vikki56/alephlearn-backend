package com.example.demo.entity;

import com.example.demo.user.User;
import jakarta.persistence.*;
import com.example.demo.entity.DoubtAnswer;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Doubt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User asker;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String codeSnippet;

    private String attachmentUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> tags;

    @Enumerated(EnumType.STRING)
    private DoubtStatus status;

    private int likeCount;
    private int answerCount;
    private long viewCount;
    private String educationLevel;   
    private String mainStream;       
    private String specialization;   
    // private long viewCount;

    @OneToOne
    private DoubtAnswer acceptedAnswer;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        if (status == null) {
            status = DoubtStatus.OPEN;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ---------- GETTERS & SETTERS ----------

    public Long getId() {
        return id;
    }

    public User getAsker() {
        return asker;
    }

    public void setAsker(User asker) {
        this.asker = asker;
    }

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

    public DoubtStatus getStatus() {
        return status;
    }

    public void setStatus(DoubtStatus status) {
        this.status = status;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(int answerCount) {
        this.answerCount = answerCount;
    }

    public long getViewCount() {
        return viewCount;
    }

    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public DoubtAnswer getAcceptedAnswer() {
        return acceptedAnswer;
    }

    public void setAcceptedAnswer(DoubtAnswer acceptedAnswer) {
        this.acceptedAnswer = acceptedAnswer;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getMainStream() {
        return mainStream;
    }

    public void setMainStream(String mainStream) {
        this.mainStream = mainStream;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
}