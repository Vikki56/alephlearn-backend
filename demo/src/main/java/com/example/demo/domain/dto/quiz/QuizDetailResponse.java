package com.example.demo.domain.dto.quiz;

import com.example.demo.domain.DifficultyLevel;
import com.example.demo.domain.QuizStatus;

import java.util.List;

public class QuizDetailResponse {

    private Long id;
    private String title;
    private String description;
    private DifficultyLevel difficulty;
    private boolean isPublic;
    private boolean isRealtime;
    private QuizStatus status;
    private Integer durationSeconds;
    private boolean isHost;

    private List<QuizQuestionDto> questions;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public DifficultyLevel getDifficulty() { return difficulty; }
    public void setDifficulty(DifficultyLevel difficulty) { this.difficulty = difficulty; }

    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean aPublic) { isPublic = aPublic; }

    public boolean isRealtime() { return isRealtime; }
    public void setRealtime(boolean realtime) { isRealtime = realtime; }

    public QuizStatus getStatus() { return status; }
    public void setStatus(QuizStatus status) { this.status = status; }

    public Integer getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }

    public List<QuizQuestionDto> getQuestions() { return questions; }
    public void setQuestions(List<QuizQuestionDto> questions) { this.questions = questions; }

    public boolean isHost() { return isHost; }
public void setHost(boolean host) { this.isHost = host; }
}