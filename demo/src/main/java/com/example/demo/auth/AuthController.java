package com.example.demo.auth;

import com.example.demo.auth.dto.AuthResponse;
import com.example.demo.auth.dto.ForgotRequest;
import com.example.demo.auth.dto.LoginRequest;
import com.example.demo.auth.dto.ResetRequest;
import com.example.demo.auth.dto.SignupRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

@PostMapping("/forgot")
public Map<String, Object> forgot(@RequestBody Map<String, String> body) {
    String email = body.get("email");
    String message = authService.forgotPassword(email);
    return Map.of("message", message);
}

@PostMapping("/reset")
public Map<String, Object> reset(@RequestBody ResetRequest req) {
    authService.resetPassword(req.token(), req.newPassword());
    return Map.of("message", "Password updated");
}

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest req) {
        var out = authService.signup(req);
        return ResponseEntity.status(CREATED).body(out);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
        var out = authService.login(req);
        return ResponseEntity.ok(out);
    }
}