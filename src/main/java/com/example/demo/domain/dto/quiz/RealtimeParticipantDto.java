package com.example.demo.domain.dto.quiz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RealtimeParticipantDto {

    private Long userId;
    private String username;
    private String email;
    private boolean removable;   
}