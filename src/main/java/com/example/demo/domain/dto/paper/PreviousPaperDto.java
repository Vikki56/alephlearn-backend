package com.example.demo.domain.dto.paper;

import java.time.Instant;

public class PreviousPaperDto {

    private Long id;
    private String collegeName;
    private String subjectName;
    private Integer examYear;
    private String examType;
    private String uploadedByName;
    private long downloads;
    private long likes;
    private Instant createdAt;
    private boolean ownedByMe;
    private boolean likedByMe;
    private Integer studentYear;

    public PreviousPaperDto() {
    }

    public PreviousPaperDto(
            Long id,
            String collegeName,
            String subjectName,
            Integer examYear,
            String examType,
            String uploadedByName,
            long downloads,
            long likes,
            Instant createdAt,
            boolean ownedByMe,
            boolean likedByMe,
            Integer studentYear
    ) {
        this.id = id;
        this.collegeName = collegeName;
        this.subjectName = subjectName;
        this.examYear = examYear;
        this.examType = examType;
        this.uploadedByName = uploadedByName;
        this.downloads = downloads;
        this.likes = likes;
        this.createdAt = createdAt;
        this.ownedByMe = ownedByMe;
        this.likedByMe = likedByMe;
        this.studentYear = studentYear;
    }

    public Long getId() {
        return id;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public Integer getExamYear() {
        return examYear;
    }

    public String getExamType() {
        return examType;
    }

    public String getUploadedByName() {
        return uploadedByName;
    }

    public long getDownloads() {
        return downloads;
    }

    public long getLikes() {
        return likes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isOwnedByMe() {
        return ownedByMe;
    }

    public void setOwnedByMe(boolean ownedByMe) {
        this.ownedByMe = ownedByMe;
    }

    public boolean isLikedByMe() {
        return likedByMe;
    }

    public void setLikedByMe(boolean likedByMe) {
        this.likedByMe = likedByMe;
    }

    public Integer getStudentYear() {
        return studentYear;
    }

    public void setStudentYear(Integer studentYear) {
        this.studentYear = studentYear;
    }
}