package com.example.demo.domain.dto.quiz;

import com.example.demo.domain.DifficultyLevel;
import com.example.demo.domain.QuizStatus;

public class QuizSummaryResponse {

    private Long id;
    private String title;
    private String description;
    private DifficultyLevel difficulty;
    private boolean isPublic;
    private boolean isRealtime;
    private QuizStatus status;
    private long totalAttempts;
    private boolean host;

    public QuizSummaryResponse(Long id, String title, String description,
                               DifficultyLevel difficulty, boolean isPublic,
                               boolean isRealtime, QuizStatus status,
                               long totalAttempts, boolean host) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.isPublic = isPublic;
        this.isRealtime = isRealtime;
        this.status = status;
        this.totalAttempts = totalAttempts;
        this.host = host;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public DifficultyLevel getDifficulty() { return difficulty; }
    public boolean isPublic() { return isPublic; }
    public boolean isRealtime() { return isRealtime; }
    public QuizStatus getStatus() { return status; }
    public long getTotalAttempts() { return totalAttempts; }
    public boolean isHost() {
        return host;
    }
}