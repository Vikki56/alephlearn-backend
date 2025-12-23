package com.example.demo.admin.dto;

public record CreateTeacherRequest(
        String name,
        String email,
        String password
) {}