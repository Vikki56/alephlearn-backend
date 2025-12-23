package com.example.demo.api;

import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/api/messages")
public class MessageMediaController {

    private static final Set<String> ALLOWED = Set.of(
            "audio/webm", "audio/ogg", "audio/mpeg", "audio/mp4", "audio/3gpp", "audio/3gpp2", "audio/wav"
    );

    @PostMapping("/upload-audio")
    public ResponseEntity<?> uploadAudio(@RequestParam("audio") MultipartFile audio) throws IOException {
        if (audio == null || audio.isEmpty()) {
            return ResponseEntity.badRequest().body("No file");
        }
        // Validate mimetype (best effort)
        String ct = Optional.ofNullable(audio.getContentType()).orElse("");
        if (!ALLOWED.contains(ct)) {
            // accept unknown if extension is supported by browsers
            // but safer to restrict; your call:
            // return ResponseEntity.badRequest().body("Unsupported audio type: " + ct);
        }

        Path root = Paths.get("uploads/audio");
        Files.createDirectories(root);

        // Pick extension
        String ext = guessExt(ct, audio.getOriginalFilename());
        String filename = UUID.randomUUID() + ext;
        Path target = root.resolve(filename);

        try (InputStream in = audio.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        String url = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/uploads/audio/")
                .path(filename)
                .toUriString();

        return ResponseEntity.ok(Map.of("url", url));
    }

    private static String guessExt(String contentType, String originalName) {
        // map a few common types
        if (MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE.equals(contentType)) {
            // fallthrough to filename
        } else if ("audio/webm".equals(contentType)) return ".webm";
        else if ("audio/ogg".equals(contentType))   return ".ogg";
        else if ("audio/mpeg".equals(contentType))  return ".mp3";
        else if ("audio/mp4".equals(contentType))   return ".m4a";
        else if ("audio/wav".equals(contentType))   return ".wav";

        if (originalName != null && originalName.contains(".")) {
            return originalName.substring(originalName.lastIndexOf('.'));
        }
        return ".webm";
    }
}