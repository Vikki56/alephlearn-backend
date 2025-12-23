package com.example.demo.domain.dto.quiz;

public class LatestAttemptDto {
    private Long quizId;
    private String quizTitle;
    private Integer score;
    private Integer totalQuestions;
    private Integer correctCount;
    private Long timeTakenMillis;
    private String submittedAt;
    private Integer rank;   // optional

    // getters / setters
    public Long getQuizId() { return quizId; }
    public void setQuizId(Long quizId) { this.quizId = quizId; }

    public String getQuizTitle() { return quizTitle; }
    public void setQuizTitle(String quizTitle) { this.quizTitle = quizTitle; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }

    public Integer getCorrectCount() { return correctCount; }
    public void setCorrectCount(Integer correctCount) { this.correctCount = correctCount; }

    public Long getTimeTakenMillis() { return timeTakenMillis; }
    public void setTimeTakenMillis(Long timeTakenMillis) { this.timeTakenMillis = timeTakenMillis; }

    public String getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(String submittedAt) { this.submittedAt = submittedAt; }

    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }
}