package com.example.demo.domain.dto.quiz;

import com.example.demo.domain.DifficultyLevel;
import com.example.demo.domain.QuizStatus;

public class CreateQuizResponse {

    private Long quizId;
    private String joinCode;
    private String quizLink;
    private DifficultyLevel difficulty;
    private boolean isPublic;
    private boolean isRealtime;
    private QuizStatus status;

    public CreateQuizResponse(Long quizId, String joinCode, String quizLink,
                              DifficultyLevel difficulty, boolean isPublic,
                              boolean isRealtime, QuizStatus status) {
        this.quizId = quizId;
        this.joinCode = joinCode;
        this.quizLink = quizLink;
        this.difficulty = difficulty;
        this.isPublic = isPublic;
        this.isRealtime = isRealtime;
        this.status = status;
    }

    public Long getQuizId() { return quizId; }
    public String getJoinCode() { return joinCode; }
    public String getQuizLink() { return quizLink; }
    public DifficultyLevel getDifficulty() { return difficulty; }
    public boolean isPublic() { return isPublic; }
    public boolean isRealtime() { return isRealtime; }
    public QuizStatus getStatus() { return status; }
}