package com.example.demo.dto;

public class DashboardRecentQuizDto {

    private Long id;
    private String title;
    private String difficulty;
    private Integer lastAttemptScore;   // null if never attempted

    public DashboardRecentQuizDto() {
    }

    public DashboardRecentQuizDto(Long id,
                                  String title,
                                  String difficulty,
                                  Integer lastAttemptScore) {
        this.id = id;
        this.title = title;
        this.difficulty = difficulty;
        this.lastAttemptScore = lastAttemptScore;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getLastAttemptScore() {
        return lastAttemptScore;
    }

    public void setLastAttemptScore(Integer lastAttemptScore) {
        this.lastAttemptScore = lastAttemptScore;
    }
}