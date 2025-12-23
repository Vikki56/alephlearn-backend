package com.example.demo.dto;

public class DashboardRecentPaperDto {

    private Long id;
    private String subjectName;
    private String collegeName;
    private Integer examYear;
    private String examType;

    public DashboardRecentPaperDto() {
    }

    public DashboardRecentPaperDto(Long id,
                                   String subjectName,
                                   String collegeName,
                                   Integer examYear,
                                   String examType) {
        this.id = id;
        this.subjectName = subjectName;
        this.collegeName = collegeName;
        this.examYear = examYear;
        this.examType = examType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public Integer getExamYear() {
        return examYear;
    }

    public void setExamYear(Integer examYear) {
        this.examYear = examYear;
    }

    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }
}