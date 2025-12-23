package com.example.demo.domain.dto.quiz;

public class AttemptHistoryItem {

    private Long quizId;
    private String quizTitle;
    private Integer bestScore;
    private Long fastestMs;
    private Integer totalAttempts;
    private Integer bestRank;   // ⭐ important

    public AttemptHistoryItem() {
    }

    public AttemptHistoryItem(Long quizId,
                              String quizTitle,
                              Integer bestScore,
                              Long fastestMs,
                              Integer totalAttempts,
                              Integer bestRank) {
        this.quizId = quizId;
        this.quizTitle = quizTitle;
        this.bestScore = bestScore;
        this.fastestMs = fastestMs;
        this.totalAttempts = totalAttempts;
        this.bestRank = bestRank;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public Integer getBestScore() {
        return bestScore;
    }

    public void setBestScore(Integer bestScore) {
        this.bestScore = bestScore;
    }

    public Long getFastestMs() {
        return fastestMs;
    }

    public void setFastestMs(Long fastestMs) {
        this.fastestMs = fastestMs;
    }

    public Integer getTotalAttempts() {
        return totalAttempts;
    }

    public void setTotalAttempts(Integer totalAttempts) {
        this.totalAttempts = totalAttempts;
    }

    public Integer getBestRank() {
        return bestRank;
    }

    public void setBestRank(Integer bestRank) {
        this.bestRank = bestRank;
    }
}