package com.example.demo.dto.question;

import jakarta.validation.constraints.Size;

public record UpdateQuestionRequest(
        @Size(max = 255)  String title,
        @Size(max = 4000) String body,
        Integer maxClaimers,
        String status,         
        Long acceptedAnswerId     
) {}
