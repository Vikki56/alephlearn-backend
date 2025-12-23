package com.example.demo.domain;

public enum QuizStatus {
    DRAFT,      // created but not live yet
    WAITING,    // realtime – waiting for users
    LIVE,       // realtime – started
    ENDED,      // finished
    COMPLETED
}