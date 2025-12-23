package com.example.demo.dto.question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;

public record CreateQuestionRequest(
        @NotBlank @Size(max = 255)  String title,
        @NotBlank @Size(max = 4000) String body,
        @Size(max = 255)            String imageUrl,
        @Min(1)                     Integer maxClaimers, // optional; default in service if null
        @NotBlank                   String askedBy       // can be taken from auth later
) {}
