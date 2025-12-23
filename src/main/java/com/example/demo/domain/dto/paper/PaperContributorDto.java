package com.example.demo.domain.dto.paper;

public class PaperContributorDto {

    private String name;
    private long totalPapers;

    public PaperContributorDto() {
    }

    public PaperContributorDto(String name, long totalPapers) {
        this.name = name;
        this.totalPapers = totalPapers;
    }

    public String getName() {
        return name;
    }

    public long getTotalPapers() {
        return totalPapers;
    }
}