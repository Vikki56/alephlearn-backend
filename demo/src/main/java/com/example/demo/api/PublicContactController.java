package com.example.demo.api;

import com.example.demo.dto.ContactMessageRequest;
import com.example.demo.entity.ContactMessage;
import com.example.demo.repository.ContactMessageRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
public class PublicContactController {

  private final ContactMessageRepository repo;

  public PublicContactController(ContactMessageRepository repo) {
    this.repo = repo;
  }

  @PostMapping("/contact")
  public ResponseEntity<?> contact(@Valid @RequestBody ContactMessageRequest req) {
    ContactMessage m = new ContactMessage();
    m.setName(req.name);
    m.setEmail(req.email);
    m.setSubject(req.subject);
    m.setMessage(req.message);
    repo.save(m);

    return ResponseEntity.ok().body(java.util.Map.of("ok", true));
  }
}