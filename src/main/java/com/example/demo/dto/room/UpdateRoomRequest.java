package com.example.demo.dto.room;

import jakarta.validation.constraints.Size;

public record UpdateRoomRequest(
        @Size(max = 120) String title,
        @Size(max = 200) String tags,
        String visibility 
) {}
