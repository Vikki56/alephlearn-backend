package com.example.demo.domain.dto.quiz;

public class LeaderboardEntryDto {
    private String username;
    private int rank;
    private int score;
    private long timeTakenMillis;

    public LeaderboardEntryDto(String username, int rank, int score, long timeTakenMillis) {
        this.username = username;
        this.rank = rank;
        this.score = score;
        this.timeTakenMillis = timeTakenMillis;
    }

    public String getUsername() { return username; }
    public int getRank() { return rank; }
    public int getScore() { return score; }
    public long getTimeTakenMillis() { return timeTakenMillis; }
}