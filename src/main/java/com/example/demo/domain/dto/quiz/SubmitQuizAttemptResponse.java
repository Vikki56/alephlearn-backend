package com.example.demo.domain.dto.quiz;

public class SubmitQuizAttemptResponse {

    private int score;
    private int totalQuestions;
    private long timeTakenMillis;

    public SubmitQuizAttemptResponse(int score, int totalQuestions, long timeTakenMillis) {
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.timeTakenMillis = timeTakenMillis;
    }

    public int getScore() { return score; }
    public int getTotalQuestions() { return totalQuestions; }
    public long getTimeTakenMillis() { return timeTakenMillis; }
}