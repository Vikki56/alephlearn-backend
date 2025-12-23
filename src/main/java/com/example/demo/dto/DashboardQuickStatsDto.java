package com.example.demo.dto;

public class DashboardQuickStatsDto {

    private long doubtsAsked;        // jitne doubts khud ne post kiye
    private long doubtsSolved;       // answers accepted (solver = user)
    private long answersGiven;       // total answers user ne diye
    private long problemsAttempted;  // doubtsSolved + FAQ claims (ProfileSummary se)

    private long quizzesCreated;     // host kiye hue quizzes
    private long quizzesAttempted;   // jitni baar quiz diya

    private long totalPoints;        // gamified score
    private long rankGlobal;         // 1,2,3,...
    private long totalUsersGlobal;   // total users
    private Double latestQuizScorePercent;

    public DashboardQuickStatsDto() {
    }

    public long getDoubtsAsked() {
        return doubtsAsked;
    }

    public void setDoubtsAsked(long doubtsAsked) {
        this.doubtsAsked = doubtsAsked;
    }

    public long getDoubtsSolved() {
        return doubtsSolved;
    }

    public void setDoubtsSolved(long doubtsSolved) {
        this.doubtsSolved = doubtsSolved;
    }

    public long getAnswersGiven() {
        return answersGiven;
    }

    public void setAnswersGiven(long answersGiven) {
        this.answersGiven = answersGiven;
    }

    public long getProblemsAttempted() {
        return problemsAttempted;
    }

    public void setProblemsAttempted(long problemsAttempted) {
        this.problemsAttempted = problemsAttempted;
    }

    public long getQuizzesCreated() {
        return quizzesCreated;
    }

    public void setQuizzesCreated(long quizzesCreated) {
        this.quizzesCreated = quizzesCreated;
    }

    public long getQuizzesAttempted() {
        return quizzesAttempted;
    }

    public void setQuizzesAttempted(long quizzesAttempted) {
        this.quizzesAttempted = quizzesAttempted;
    }

    public long getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(long totalPoints) {
        this.totalPoints = totalPoints;
    }

    public long getRankGlobal() {
        return rankGlobal;
    }

    public void setRankGlobal(long rankGlobal) {
        this.rankGlobal = rankGlobal;
    }

    public long getTotalUsersGlobal() {
        return totalUsersGlobal;
    }

    public void setTotalUsersGlobal(long totalUsersGlobal) {
        this.totalUsersGlobal = totalUsersGlobal;
    }

    public Double getLatestQuizScorePercent() {
        return latestQuizScorePercent;
    }

    public void setLatestQuizScorePercent(Double latestQuizScorePercent) {
        this.latestQuizScorePercent = latestQuizScorePercent;
    }
}