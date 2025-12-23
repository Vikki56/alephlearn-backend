package com.example.demo.auth;

import com.example.demo.auth.dto.AuthResponse;
import com.example.demo.auth.dto.LoginRequest;
import com.example.demo.auth.dto.SignupRequest;
import com.example.demo.auth.dto.UserResponse;
import com.example.demo.security.JwtService;
import com.example.demo.service.LoginActivityService;
import com.example.demo.service.MailService;
import com.example.demo.user.Role;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.service.LoginActivityService;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.time.Instant;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MailService mailService;
    private final LoginActivityService loginActivityService;

    public AuthService(UserRepository users,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,MailService mailService,
                       LoginActivityService loginActivityService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.mailService = mailService;
        this.loginActivityService = loginActivityService;
    }

    /* ------------ Sign up ------------ */
    @Transactional
    public AuthResponse signup(SignupRequest req) {
        final String email = req.email().trim().toLowerCase();
        if (users.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        User u = new User();
        u.setName(req.name().trim());
        u.setEmail(email);
        u.setPasswordHash(passwordEncoder.encode(req.password()));
        u.setRole(Role.USER);

        User saved = users.save(u);
        loginActivityService.recordLogin(saved);
        String token = jwtService.generateToken(saved);
        return new AuthResponse(token, UserResponse.from(saved));
    }

    /* ------------ Log in ------------ */
    public AuthResponse login(LoginRequest req) {
        final String email = req.email().trim().toLowerCase();
    
        User user = users.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
    
        // ✅ auto-unblock if expired
        if (user.isBlocked() && user.getBlockedUntil() != null && Instant.now().isAfter(user.getBlockedUntil())) {
            user.setBlocked(false);
            user.setBlockedUntil(null);
            user.setBlockReason(null);
            users.save(user);
        }
    
        // ✅ still blocked => stop login
        if (user.isBlocked()) {
            String msg = (user.getBlockedUntil() == null)
                    ? ("You are permanently banned. Contact admin: admin@alephlearn.com")
                    : ("You are blocked till " + user.getBlockedUntil() + ". Contact admin: admin@alephlearn.com");
            if (user.getBlockReason() != null) msg += " Reason: " + user.getBlockReason();
    
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, msg);
        }
    
        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
    
        loginActivityService.recordLogin(user);
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, UserResponse.from(user));
    }

    /* ------------ Forgot password (DEV: returns token) ------------ */
@Transactional
public String forgotPassword(String rawEmail) {
    final String email = rawEmail.trim().toLowerCase();
    User user = users.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("No account with that email"));

    String token = UUID.randomUUID().toString();
    user.setResetToken(token);
    users.save(user);

    // ✅ Send the actual email
    mailService.sendResetEmail(email, token);

    return token;
}

    /* ------------ Reset password ------------ */
    @Transactional
    public void resetPassword(String token, String newPassword) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Reset token required");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }

        User user = users.findByResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setResetToken(null); // clear token after use
        users.save(user);
    }
}