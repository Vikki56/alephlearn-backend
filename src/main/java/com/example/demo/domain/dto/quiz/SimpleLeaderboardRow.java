package com.example.demo.domain.dto.quiz;

public class SimpleLeaderboardRow {
    private Integer rank;
    private String userDisplayName;
    private Integer score;
    private Long timeTakenMillis;

    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }

    public String getUserDisplayName() { return userDisplayName; }
    public void setUserDisplayName(String userDisplayName) { this.userDisplayName = userDisplayName; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public Long getTimeTakenMillis() { return timeTakenMillis; }
    public void setTimeTakenMillis(Long timeTakenMillis) { this.timeTakenMillis = timeTakenMillis; }
}