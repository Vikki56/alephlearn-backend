package com.example.demo.domain.dto.quiz;

public class LatestAttemptResponse {

    private Long attemptId;
    private Long quizId;
    private String quizTitle;

    private Integer score;
    private Long timeTakenMillis;

    private String submittedAt;

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
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

    public String getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(String submittedAt) {
        this.submittedAt = submittedAt;
    }
}