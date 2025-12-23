package com.example.demo.admin;

import com.example.demo.admin.dto.CreateTeacherRequest;
import com.example.demo.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @PostMapping("/teacher")
    public ResponseEntity<?> createTeacher(@RequestBody CreateTeacherRequest req) {
        User t = adminUserService.createTeacher(req);

        // ✅ Return safe payload (no passwordHash)
        return ResponseEntity.ok(Map.of(
                "id", t.getId(),
                "name", t.getName(),
                "email", t.getEmail(),
                "role", t.getRole().name()
        ));
    }
    @GetMapping("/teachers")
public ResponseEntity<?> listTeachers() {
    return ResponseEntity.ok(adminUserService.listTeachers());
}
@GetMapping("/blocked")
public ResponseEntity<?> blocked() {
  return ResponseEntity.ok(adminUserService.blockedUsers());
}

@GetMapping("/banned")
public ResponseEntity<?> banned() {
  return ResponseEntity.ok(adminUserService.bannedUsers());
}

@PostMapping("/{id}/unban")
public ResponseEntity<?> unban(@PathVariable Long id) {
  adminUserService.unban(id);
  return ResponseEntity.ok(Map.of("ok", true));
}
}