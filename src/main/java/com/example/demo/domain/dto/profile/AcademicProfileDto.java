package com.example.demo.domain.dto.profile;

public class AcademicProfileDto {

    private Long id;

    private String educationLevel;
    private String mainStream;
    private String specialization;

    // ====== getters / setters ======

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getMainStream() {
        return mainStream;
    }

    public void setMainStream(String mainStream) {
        this.mainStream = mainStream;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
}