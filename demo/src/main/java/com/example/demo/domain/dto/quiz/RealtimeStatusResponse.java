package com.example.demo.domain.dto.quiz;

import com.example.demo.domain.QuizStatus;

public class RealtimeStatusResponse {

    private QuizStatus status;
    private long joinedCount;
    private Long remainingSeconds;

    public RealtimeStatusResponse(QuizStatus status, long joinedCount, Long remainingSeconds) {
        this.status = status;
        this.joinedCount = joinedCount;
        this.remainingSeconds = remainingSeconds;
    }

    public QuizStatus getStatus() { return status; }
    public long getJoinedCount() { return joinedCount; }
    public Long getRemainingSeconds() { return remainingSeconds; }
}