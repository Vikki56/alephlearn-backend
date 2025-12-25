package com.example.demo.service;

import com.example.demo.repo.ClaimRepository;
import com.example.demo.domain.Claim;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ClaimExpiryScheduler {

    private final ClaimRepository claims;

    @Scheduled(fixedRate = 60_000) 
    public void clearExpiredClaims() {
        Instant now = Instant.now();

        claims.findAll().forEach(c -> {
            if (c.getExpiresAt() != null && c.getExpiresAt().isBefore(now)) {
                claims.delete(c);
            }
        });
    }
}