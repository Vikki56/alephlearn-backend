package com.example.demo.dto;

public class DashboardUserDto {

    private String name;
    private String email;
    private String initials;
    private String branchLabel;

    public DashboardUserDto() {
    }

    public DashboardUserDto(String name, String email, String initials, String branchLabel) {
        this.name = name;
        this.email = email;
        this.initials = initials;
        this.branchLabel = branchLabel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getBranchLabel() {
        return branchLabel;
    }

    public void setBranchLabel(String branchLabel) {
        this.branchLabel = branchLabel;
    }
}