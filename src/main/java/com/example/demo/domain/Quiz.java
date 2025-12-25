package com.example.demo.domain;

import com.example.demo.user.User;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;
import com.example.demo.domain.entity.QuizQuestion;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quizzes")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DifficultyLevel difficulty;

    @Column(nullable = false)
    private boolean isPublic;

    @Column(nullable = false)
    private boolean isRealtime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuizStatus status = QuizStatus.DRAFT;

    // For private / realtime links
    @Column(nullable = false, unique = true, updatable = false)
    private String joinCode;

    // in seconds (only for realtime)
    private Integer durationSeconds;

    // stats
    @Column(nullable = false)
    private long totalAttempts = 0L;

    @Column(nullable = false)
    private long joinedCount = 0L;   

    private Instant startTime;
    private Instant endTime;

@Column(name = "education_level")
private String educationLevel;

@Column(name = "main_stream")
private String mainStream;

@Column(name = "specialization")
private String specialization;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
@OrderBy("ordinalPosition ASC")
private List<QuizQuestion> questions = new ArrayList<>();

public List<QuizQuestion> getQuestions() {
    return questions; 
}

public void setQuestions(List<QuizQuestion> questions) {
    this.questions = questions;
}



    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.joinCode == null) {
            this.joinCode = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        }

        if (this.isRealtime) {
            this.status = QuizStatus.WAITING;
        } else {
            this.status = QuizStatus.DRAFT;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // ------------ getters/setters ------------

    public Long getId() { return id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public DifficultyLevel getDifficulty() { return difficulty; }

    public void setDifficulty(DifficultyLevel difficulty) { this.difficulty = difficulty; }

    public boolean isPublic() { return isPublic; }

    public void setPublic(boolean aPublic) { isPublic = aPublic; }

    public boolean isRealtime() { return isRealtime; }

    public void setRealtime(boolean realtime) { isRealtime = realtime; }

    public QuizStatus getStatus() { return status; }

    public void setStatus(QuizStatus status) { this.status = status; }

    public String getJoinCode() { return joinCode; }

    public Integer getDurationSeconds() { return durationSeconds; }

    public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }

    public long getTotalAttempts() { return totalAttempts; }

    public void setTotalAttempts(long totalAttempts) { this.totalAttempts = totalAttempts; }

    public long getJoinedCount() { return joinedCount; }

    public void setJoinedCount(long joinedCount) { this.joinedCount = joinedCount; }

    public Instant getStartTime() { return startTime; }

    public void setStartTime(Instant startTime) { this.startTime = startTime; }

    public Instant getEndTime() { return endTime; }

    public void setEndTime(Instant endTime) { this.endTime = endTime; }

    public User getCreatedBy() { return createdBy; }

    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public Instant getCreatedAt() { return createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }

    public String getEducationLevel() { return educationLevel; }
public void setEducationLevel(String educationLevel) { this.educationLevel = educationLevel; }

public String getMainStream() { return mainStream; }
public void setMainStream(String mainStream) { this.mainStream = mainStream; }

public String getSpecialization() { return specialization; }
public void setSpecialization(String specialization) { this.specialization = specialization; }
}