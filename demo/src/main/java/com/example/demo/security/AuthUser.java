package com.example.demo.security;

import com.example.demo.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class AuthUser {
  private AuthUser() {}

  public static User current() {
    Authentication a = SecurityContextHolder.getContext().getAuthentication();
    return (a != null && a.getPrincipal() instanceof User u) ? u : null;
  }
}
