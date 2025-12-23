package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ContactMessageRequest {
  @NotBlank public String name;
  @Email @NotBlank public String email;
  @NotBlank public String subject;
  @NotBlank public String message;
}