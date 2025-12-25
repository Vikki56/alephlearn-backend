package com.example.demo.dto.room;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRoomRequest(
        @NotBlank @Size(max = 32)  String subject,
        @NotBlank @Size(max = 140) String slug,
        @NotBlank @Size(max = 120) String title,
        @Size(max = 200)           String tags,
        @NotBlank                  String visibility 
) {}
