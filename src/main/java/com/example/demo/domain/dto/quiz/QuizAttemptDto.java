package com.example.demo.domain.dto.quiz;

import java.time.Instant;

public class QuizAttemptDto {

    private Long id;
    private Long quizId;
    private String quizTitle;

    private Integer score;
    private Long timeTakenMillis;
    private Instant submittedAt;

    // NEW
    private Integer totalQuestions;
    private Integer correctCount;
    private Integer wrongCount;
    private Integer skippedCount;

    // --- getters & setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Long getTimeTakenMillis() {
        return timeTakenMillis;
    }

    public void setTimeTakenMillis(Long timeTakenMillis) {
        this.timeTakenMillis = timeTakenMillis;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }
    
    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }
    
    public Integer getCorrectCount() {
        return correctCount;
    }
    
    public void setCorrectCount(Integer correctCount) {
        this.correctCount = correctCount;
    }
    
    public Integer getWrongCount() {
        return wrongCount;
    }
    
    public void setWrongCount(Integer wrongCount) {
        this.wrongCount = wrongCount;
    }
    
    public Integer getSkippedCount() {
        return skippedCount;
    }
    
    public void setSkippedCount(Integer skippedCount) {
        this.skippedCount = skippedCount;
    }
}