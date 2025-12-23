package com.example.demo.domain.dto.profile;

import java.util.List;

public class AcademicOptionsDto {
    private List<String> educationLevels;
    private List<String> mainStreams;
    private List<String> specializations;

    // 🔥 NEW
    private List<String> validCombos;

    public List<String> getEducationLevels() {
        return educationLevels;
    }

    public void setEducationLevels(List<String> educationLevels) {
        this.educationLevels = educationLevels;
    }

    public List<String> getMainStreams() {
        return mainStreams;
    }

    public void setMainStreams(List<String> mainStreams) {
        this.mainStreams = mainStreams;
    }

    public List<String> getSpecializations() {
        return specializations;
    }

    public void setSpecializations(List<String> specializations) {
        this.specializations = specializations;
    }

    public List<String> getValidCombos() {
        return validCombos;
    }

    public void setValidCombos(List<String> validCombos) {
        this.validCombos = validCombos;
    }
}