package com.example.demo.dto;

import java.time.LocalDate;
import java.util.List;   

public class ProfileSummaryDto {
    private Long id;
    private String name;
    private String email;
    private String avatarInitials;
    private String branchLabel;

    // core stats
    private long problemsAttempted;   // doubts answered + F&Q claims
    private long doubtsSolved;        // answers accepted
    private long quizzesCreated;      // quizzes hosted
    private long totalQuizAttempts;   // jitni baar user ne quiz diya

    // score
    private long totalPoints;         // gamified score

    // global ranking
    private long rankGlobal;          // 1,2,3...
    private long totalUsersGlobal;    // kitne users consider hue

    // optional: duplicate summary (agar kahin aur use ho)
    private long totalUsers;

    // 🔥 NEW: streak-info
    private long daysActiveThisYear;
    private LocalDate lastLoginDate;
    private List<String> loginDatesThisYear;   // 👈 naya field

    public ProfileSummaryDto() {
    }

    public ProfileSummaryDto(String name,
                             String email,
                             String avatarInitials,
                             String branchLabel,
                             long problemsAttempted,
                             long doubtsSolved,
                             long quizzesCreated,
                             long totalQuizAttempts,
                             long totalPoints,
                             long rankGlobal,
                             long totalUsersGlobal,
                             long daysActiveThisYear,
                             LocalDate lastLoginDate) {
        this.name = name;
        this.email = email;
        this.avatarInitials = avatarInitials;
        this.branchLabel = branchLabel;
        this.problemsAttempted = problemsAttempted;
        this.doubtsSolved = doubtsSolved;
        this.quizzesCreated = quizzesCreated;
        this.totalQuizAttempts = totalQuizAttempts;
        this.totalPoints = totalPoints;
        this.rankGlobal = rankGlobal;
        this.totalUsersGlobal = totalUsersGlobal;
        this.daysActiveThisYear = daysActiveThisYear;
        this.lastLoginDate = lastLoginDate;
    }

    // -------- getters & setters --------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAvatarInitials() { return avatarInitials; }
    public void setAvatarInitials(String avatarInitials) { this.avatarInitials = avatarInitials; }

    public String getBranchLabel() { return branchLabel; }
    public void setBranchLabel(String branchLabel) { this.branchLabel = branchLabel; }

    public long getProblemsAttempted() { return problemsAttempted; }
    public void setProblemsAttempted(long problemsAttempted) { this.problemsAttempted = problemsAttempted; }

    public long getDoubtsSolved() { return doubtsSolved; }
    public void setDoubtsSolved(long doubtsSolved) { this.doubtsSolved = doubtsSolved; }

    public long getQuizzesCreated() { return quizzesCreated; }
    public void setQuizzesCreated(long quizzesCreated) { this.quizzesCreated = quizzesCreated; }

    public long getTotalQuizAttempts() { return totalQuizAttempts; }
    public void setTotalQuizAttempts(long totalQuizAttempts) { this.totalQuizAttempts = totalQuizAttempts; }

    public long getTotalPoints() { return totalPoints; }
    public void setTotalPoints(long totalPoints) { this.totalPoints = totalPoints; }

    public long getRankGlobal() { return rankGlobal; }
    public void setRankGlobal(long rankGlobal) { this.rankGlobal = rankGlobal; }

    public long getTotalUsersGlobal() { return totalUsersGlobal; }
    public void setTotalUsersGlobal(long totalUsersGlobal) { this.totalUsersGlobal = totalUsersGlobal; }

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public long getDaysActiveThisYear() { return daysActiveThisYear; }
    public void setDaysActiveThisYear(long daysActiveThisYear) { this.daysActiveThisYear = daysActiveThisYear; }
    public LocalDate getLastLoginDate() {
        return lastLoginDate;
    }
    
    public void setLastLoginDate(LocalDate lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }
    public List<String> getLoginDatesThisYear() {
        return loginDatesThisYear;
    }

    public void setLoginDatesThisYear(List<String> loginDatesThisYear) {
        this.loginDatesThisYear = loginDatesThisYear;
    }
}