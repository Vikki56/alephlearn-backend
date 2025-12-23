package com.example.demo.dto;

import java.util.List;

public class MiniProfileDto {
    private String name;
    private String email;
    private String initials;
    private String branchLabel;

    private long doubtsSolved;
    private long problemsAttempted;
    private long rank;
    private long totalUsers;

    private List<String> interests;

    private long likes;
    private boolean likedByMe;

    public MiniProfileDto() {}

    public MiniProfileDto(String name, String email, String initials, String branchLabel,
                          long doubtsSolved, long problemsAttempted,
                          long rank, long totalUsers,
                          List<String> interests,
                          long likes, boolean likedByMe) {
        this.name = name;
        this.email = email;
        this.initials = initials;
        this.branchLabel = branchLabel;
        this.doubtsSolved = doubtsSolved;
        this.problemsAttempted = problemsAttempted;
        this.rank = rank;
        this.totalUsers = totalUsers;
        this.interests = interests;
        this.likes = likes;
        this.likedByMe = likedByMe;
    }

    // ===== Getters & Setters =====

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getInitials() { return initials; }
    public void setInitials(String initials) { this.initials = initials; }

    public String getBranchLabel() { return branchLabel; }
    public void setBranchLabel(String branchLabel) { this.branchLabel = branchLabel; }

    public long getDoubtsSolved() { return doubtsSolved; }
    public void setDoubtsSolved(long doubtsSolved) { this.doubtsSolved = doubtsSolved; }

    public long getProblemsAttempted() { return problemsAttempted; }
    public void setProblemsAttempted(long problemsAttempted) { this.problemsAttempted = problemsAttempted; }

    public long getRank() { return rank; }
    public void setRank(long rank) { this.rank = rank; }

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public List<String> getInterests() { return interests; }
    public void setInterests(List<String> interests) { this.interests = interests; }

    public long getLikes() { return likes; }
    public void setLikes(long likes) { this.likes = likes; }

    public boolean isLikedByMe() { return likedByMe; }
    public void setLikedByMe(boolean likedByMe) { this.likedByMe = likedByMe; }
}