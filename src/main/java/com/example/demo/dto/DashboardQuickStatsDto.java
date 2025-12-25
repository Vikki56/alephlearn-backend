package com.example.demo.dto;

public class DashboardQuickStatsDto {

    private long doubtsAsked;    
    private long doubtsSolved;      
    private long answersGiven;      
    private long problemsAttempted;  

    private long quizzesCreated;     
    private long quizzesAttempted;  

    private long totalPoints;       
    private long rankGlobal;        
    private long totalUsersGlobal;   
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