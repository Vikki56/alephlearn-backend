package com.example.demo.domain.dto.quiz;

import java.util.List;

public class LatestAttemptAndLeaderboardResponse {
    private LatestAttemptDto latestAttempt;
    private List<SimpleLeaderboardRow> leaderboard;

    public LatestAttemptDto getLatestAttempt() { return latestAttempt; }
    public void setLatestAttempt(LatestAttemptDto latestAttempt) { this.latestAttempt = latestAttempt; }

    public List<SimpleLeaderboardRow> getLeaderboard() { return leaderboard; }
    public void setLeaderboard(List<SimpleLeaderboardRow> leaderboard) { this.leaderboard = leaderboard; }
}