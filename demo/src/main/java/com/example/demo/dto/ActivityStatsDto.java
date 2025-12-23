package com.example.demo.dto;

public record ActivityStatsDto(
        long doubtsPosted,
        long answersGiven,
        long solutionsAccepted
) {}