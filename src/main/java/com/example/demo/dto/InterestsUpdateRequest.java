package com.example.demo.dto;

import java.util.List;

public class InterestsUpdateRequest {

    private List<String> interests;

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }
}