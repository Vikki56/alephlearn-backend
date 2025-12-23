package com.example.demo.domain;

import com.example.demo.user.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "quiz_attempts")
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ---- basic result ----
    @Column(name = "score")
    private Integer score;

    @Column(name = "time_taken_millis")
    private Long timeTakenMillis;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "is_realtime")
    private boolean realtime;

    // ---- realtime helpers ----
    @Column(name = "joined_at")
    private Instant joinedAt;

    @Column(name = "completed")
    private boolean completed;

    @Column(name = "banned")
private boolean banned = false;

    // ---- NEW: detailed stats ----
 // analytics fields
@Column(name = "total_questions")
private Integer totalQuestions;

@Column(name = "correct_count")
private Integer correctCount;

@Column(name = "wrong_count")
private Integer wrongCount;

@Column(name = "skipped_count")
private Integer skippedCount;



@Column(name = "approved")
private boolean approved = false;

    

    // ========= getters & setters =========

    public Long getId() {
        return id;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Long getTimeTakenMillis() {
        return timeTakenMillis;
    }

    public void setTimeTakenMillis(Long timeTakenMillis) {
        this.timeTakenMillis = timeTakenMillis;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public boolean isRealtime() {
        return realtime;
    }
    
    public void setRealtime(boolean realtime) {
        this.realtime = realtime;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Instant joinedAt) {
        this.joinedAt = joinedAt;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }
    
    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }
    
    public Integer getCorrectCount() {
        return correctCount;
    }
    
    public void setCorrectCount(Integer correctCount) {
        this.correctCount = correctCount;
    }
    
    public Integer getWrongCount() {
        return wrongCount;
    }
    
    public void setWrongCount(Integer wrongCount) {
        this.wrongCount = wrongCount;
    }
    
    public Integer getSkippedCount() {
        return skippedCount;
    }
    
    public void setSkippedCount(Integer skippedCount) {
        this.skippedCount = skippedCount;
    }
    public boolean isBanned() {
        return banned;
    }
    
    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public boolean isApproved() {
        return approved;
    }
    
    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}